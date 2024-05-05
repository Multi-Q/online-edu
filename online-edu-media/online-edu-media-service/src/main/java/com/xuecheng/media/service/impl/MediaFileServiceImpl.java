package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Resource
    private MinioClient minioClient;
    @Resource
    MediaFilesMapper mediaFilesMapper;
    @Resource
    MediaFileService currentProxy;
    @Resource
    private MediaProcessMapper mediaProcessMapper;


    @Value("${minio.bucket.files}")
    private String bucket_mediafile;

    @Value("${minio.bucket.videofiles}")
    private String bucket_video;

    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<MediaFiles>()
                .eq(StringUtils.isNotBlank(queryMediaParamsDto.getFileType()),
                        MediaFiles::getFileType,
                        queryMediaParamsDto.getFileType()
                )
                .like(StringUtils.isNotBlank(queryMediaParamsDto.getFilename()),
                        MediaFiles::getFilename,
                        queryMediaParamsDto.getFilename());

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath,String objectName) {
        //文件名
        String filename = uploadFileParamsDto.getFilename();
        //得到扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //得到mimeType
        String mimeType = getMimeType(extension);
        //子目录
        String folderPath = getDefaultFolderPath();
        //文件的MD5值
        String fileMD5 = getFileMD5(new File(localFilePath));
        if (StringUtils.isEmpty(objectName)){
            //使用默认日期去存储
             objectName = folderPath + fileMD5 + extension;
        }


        boolean res = addMediaFilesToMinIO(localFilePath, mimeType, bucket_mediafile, objectName);
        if (!res) {
            XueChengPlusException.cast("上传文件失败,minio内已有这张图片");
        }
        //将文件信息到存到media_files表
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMD5, uploadFileParamsDto, bucket_mediafile, objectName);
        if (mediaFiles == null) {
            XueChengPlusException.cast("文件上传后保存信息失败");
        }

        //准备返回的对象
        UploadFileResultDto resultDto = new UploadFileResultDto();
        //如果上传的是课程教师图片则，将media_files表中url值赋值给photograph
        if (extension.equals(".jpeg") || extension.equals(".jpg") || extension.equals(".png")) {
            resultDto.setPhotograph(resultDto.getUrl());
            mediaFiles.setTags("图片");
        } else {
            mediaFiles.setTags("课程视频");
        }
        BeanUtils.copyProperties(mediaFiles, resultDto);

        return resultDto;
    }

    /**
     * 根据扩展名获取mimetype
     *
     * @param extension 扩展名
     * @return
     */
    private String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        //根据扩展名去除mimetype
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    @Override
    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}", bucket, objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}", bucket, objectName, e.getMessage(), e);
            XueChengPlusException.cast("上传文件到文件系统失败");
        }
        return false;
    }

    /**
     * 设置文件夹格式
     *
     * @return
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/") + "/";
        return folder;
    }

    /**
     * 获取文件的MD5值
     *
     * @param file 文件
     * @return
     */
    private String getFileMD5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String md5Hex = DigestUtils.md5Hex(fileInputStream);
            return md5Hex;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5值
     * @param uploadFileParamsDto 上传文件的信息
     * @param bucket              桶
     * @param objectName          对象名称
     * @return com.xuecheng.media.model.po.MediaFiles
     * @description 将文件信息添加到文件表
     */
    @Transactional
    @Override
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            //id,md5
            mediaFiles.setId(fileMd5);
            //file_id
            mediaFiles.setFileId(fileMd5);
            //公司id
            mediaFiles.setCompanyId(companyId);
            //url
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            //桶
            mediaFiles.setBucket(bucket);
            //对象名
            mediaFiles.setFilePath(objectName);
            //创建时间
            mediaFiles.setCreateDate(LocalDateTime.now());
            //审核状态,默认不审核
            mediaFiles.setAuditStatus("002003");
            //文件状态
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.error("保存文件信息到数据库失败,{}", mediaFiles.toString());
                return null;
            }
            //记录待处理任务
            //添加到待处理任务表
            addWaitingTask(mediaFiles);
            log.debug("保存文件信息到数据库成功,{}", mediaFiles.toString());
            return mediaFiles;

        }
        return mediaFiles;
    }

    /**
     * 添加待处理任务
     *
     * @param mediaFiles
     */
    private void addWaitingTask(MediaFiles mediaFiles) {
        //获取mimeType
        //文件名称
        String filename = mediaFiles.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //获取文件的mimeTYpe
        String mimeType = getMimeType(extension);
        if (mimeType.equals("video/x-msvideo")) {
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles, mediaProcess);
            //状态是未处理
            mediaProcess.setStatus("1");
            mediaProcess.setCreateDate(LocalDateTime.now());
            mediaProcess.setFailCount(0);//失败次数默认为0
            mediaProcess.setUrl("");
            mediaProcessMapper.insert(mediaProcess);
        }
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //先查询数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //objectName
            String filePath = mediaFiles.getFilePath();
            //如果数据库存在，再查询minio
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build();
            //查询远程服务器获取一个对象
            try {
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                if (inputStream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (ErrorResponseException | InsufficientDataException
                    | InternalException | InvalidKeyException
                    | InvalidResponseException | IOException
                    | NoSuchAlgorithmException | ServerException
                    | XmlParserException e) {
                e.printStackTrace();
            }
        }
        //文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //分块存储路径是 md5前两位为两个目录，chunk存储分块文件
        //根据MD5得到分块文件所在路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //如果数据库存在，再查询minio
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket_video)
                .object(chunkFileFolderPath + chunkIndex)
                .build();
        //查询远程服务器获取一个对象
        try {
            FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream != null) {
                //分块文件已存在
                return RestResponse.success(true);
            }
        } catch (ErrorResponseException | InsufficientDataException
                | InternalException | InvalidKeyException
                | InvalidResponseException | IOException
                | NoSuchAlgorithmException | ServerException
                | XmlParserException e) {
            e.printStackTrace();
        }
        //文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        //分块文件的路径
        String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunk;
        //获取mimeType
        String mimeType = getMimeType(null);
        //将分块文件上传到minio
        boolean b = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucket_video, chunkFilePath);
        if (!b) {
            return RestResponse.validfail(false, "上传分块文件失败,minio内已有该视频分块");
        }
        //上传成功
        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //=====获取分块文件路径=====
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //组成将分块文件路径组成 List<ComposeSource>
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_video)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        //=====合并=====
        //文件名称
        String fileName = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        //合并文件路径
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);
        try {
            //合并文件
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucket_video)
                            .object(mergeFilePath)
                            .sources(sourceObjectList)
                            .build());
            log.debug("合并文件成功:{}", mergeFilePath);
        } catch (Exception e) {
            log.debug("合并文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            return RestResponse.validfail(false, "合并文件失败。");
        }

        // ====验证md5====
        File minioFile = downloadFileFromMinIO(bucket_video, mergeFilePath);
        if (minioFile == null) {
            log.debug("从minio内下载文件为null,mergeFilePath:{}", mergeFilePath);
            return RestResponse.validfail(false, "从minio内下载文件为null。");
        }
        //todo:这里的代码有个问题，若是上传相同的视频前端会卡进度条，但是后端会报一个错
        try (InputStream newFileInputStream = new FileInputStream(minioFile)) {
            //minio上文件的md5值
            String md5Hex = DigestUtils.md5Hex(newFileInputStream);
            //比较md5值，不一致则说明文件不完整
            if (!fileMd5.equals(md5Hex)) {
                return RestResponse.validfail(false, "文件合并校验失败，minio内文件不完整。");
            }
            //文件大小
            uploadFileParamsDto.setFileSize(minioFile.length());
        } catch (Exception e) {
            log.debug("校验文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
        } finally {
            if (minioFile != null) {
                minioFile.delete();
            }
        }
        //文件入库
        currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_video, mergeFilePath);
        //=====清除分块文件=====
        clearChunkFiles(chunkFileFolderPath, chunkTotal);
        return RestResponse.success(true);
    }

    /**
     * 清除分块文件
     *
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal          分块文件总数
     */
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {

        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("video").objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清除分块文件失败,objectName:{}", deleteError.objectName(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清除分块文件失败,chunkFileFolderPath:{}", chunkFileFolderPath, e);
        }
    }


    @Override
    public File downloadFileFromMinIO(String bucket, String objectName) {
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public MediaFiles getFilesById(String mediaId) {
        return mediaFilesMapper.selectById(mediaId);
    }

    /**
     * 得到合并后的文件路径
     *
     * @param fileMd5 文件的MD5值
     * @param fileExt 文件的扩展名
     * @return
     */
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    /**
     * 获取分块文件所在的路径
     *
     * @param fileMd5 文件的MD5值
     * @return
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

}
