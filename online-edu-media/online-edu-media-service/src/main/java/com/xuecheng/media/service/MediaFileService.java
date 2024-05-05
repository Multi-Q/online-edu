package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;

import java.io.File;


/**
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     *
     * @param companyId           机构id
     * @param uploadFileParamsDto
     * @param localFilePath       文件的本地路径
     * @param objectName          路径名
     * @return
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName);

    /**
     * 添加文件到数据库
     *
     * @param companyId           机构id
     * @param fileMd5             文件的MD5值
     * @param uploadFileParamsDto uploadFileParamsDto
     * @param bucket              桶
     * @param objectName          对象名
     * @return MediaFiles
     */
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    /**
     * 将文件上传到minio
     *
     * @param localFilePath 本地文件路径
     * @param mimeType      文件类型
     * @param bucket        桶
     * @param objectName    存在桶里的名称
     * @return true or false
     */
    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);

    /**
     * 文件上传检查文件
     *
     * @param fileMd5 文件的MD5值
     * @return
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 分块文件上传前的检查
     *
     * @param fileMd5    文件的MD5值
     * @param chunkIndex 分块的index
     * @return
     */
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传分块
     *
     * @param fileMd5            文件的MD5值
     * @param chunk              分块序号
     * @param localChunkFilePath 本地文件路径
     * @return
     */
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath);

    /**
     * 合并分块
     *
     * @param companyId           机构ID
     * @param fileMd5             文件的MD5值
     * @param chunkTotal          块的总数
     * @param uploadFileParamsDto uploadFileParamsDto
     * @return
     */
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 从minio中下载文件
     *
     * @param bucket     桶名
     * @param objectName 文件在minio中的目录
     * @return
     */
    public File downloadFileFromMinIO(String bucket, String objectName);

    /**
     * 获取媒资文件
     *
     * @param mediaId 媒资id
     * @return MediaFiles
     */
    MediaFiles getFilesById(String mediaId);
}