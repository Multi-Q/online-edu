package com.xuecheng.minio;

import com.sun.media.jfxmediaimpl.MediaUtils;
import io.minio.*;
import io.minio.errors.*;
import javafx.scene.media.Media;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author QRH
 * @date 2023/6/25 19:30
 * @description
 */
@SpringBootTest
public class MinIOTest {
    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.101.65:9000")
            .credentials("minioadmin", "minioadmin")
            .build();

    @Test
    public void upload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //上传文件的参数信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket") //桶
                .filename("C:\\Users\\qrh19\\Pictures\\078235f1baa0772982d1d0b43f058edb.mp4") //要上传的文件路径
                .object("078235f1baa0772982d1d0b43f058edb.mp4") //在桶下存储的文件名
//                .object("test/01/1.mp4")  //在桶下的test/01/1.mp4
                .contentType("video/mp4") //设置媒体文件类型
                .build();
        //上传文件
        minioClient.uploadObject(uploadObjectArgs);
    }

    @Test
    public void delete() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //删除文件的参数信息
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("078235f1baa0772982d1d0b43f058edb.mp4")
                .build();
        //删除文件
        minioClient.removeObject(removeObjectArgs);
    }

    @Test
    public void get() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //文件的参数信息
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("078235f1baa0772982d1d0b43f058edb.mp4")
                .build();
        //获取文件流
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        //指定输出流
        FileOutputStream outputStream = new FileOutputStream(new File("C:\\Users\\qrh19\\Pictures\\1b.mp4"));
        IOUtils.copy(inputStream, outputStream);
        //校验文件的完整性对文件的内容进行MD5
        String source = DigestUtils.md5Hex(inputStream);
        String local = DigestUtils.md5Hex(new FileInputStream(new File("C:\\Users\\qrh19\\Pictures\\1b.mp4")));
        if (source.equals(local)) {
            System.out.println("下载成功");
        }

    }


    //将文件上传到minio
    @Test
    public void uploadChunk() throws IOException {
        for (int i = 0; i < 30; i++) {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket") //桶
                    .filename("C:\\Users\\qrh19\\Pictures\\" + i) //要上传的文件路径
                    .object("chunk/" + i) //在桶下存储的文件名
//                .object("test/01/1.mp4")  //在桶下的test/01/1.mp4
                    .contentType("video/mp4") //设置媒体文件类型
                    .build();
        }


    }

    @Test
    public void downloadChunk() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<ComposeSource> sources = null;

        for (int i = 0; i < 30; i++) {
            ComposeSource composeSource = ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build();
            sources.add(composeSource);
        }

        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge1.mp4")
                .sources(sources)
                .build();

        for (int i = 0; i < 30; i++) {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket") //桶
                    .filename("C:\\Users\\qrh19\\Pictures\\" + i) //要上传的文件路径
                    .object("chunk/" + i) //在桶下存储的文件名
//                .object("test/01/1.mp4")  //在桶下的test/01/1.mp4
                    .contentType("video/mp4") //设置媒体文件类型
                    .build();
        }

        minioClient.composeObject(composeObjectArgs);
    }

}
