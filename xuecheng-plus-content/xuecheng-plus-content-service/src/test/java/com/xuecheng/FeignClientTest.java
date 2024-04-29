package com.xuecheng;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author QRH
 * @date 2023/7/9 20:13
 * @description
 */
@SpringBootTest
public class FeignClientTest {
    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Test
    public void test() throws IOException {
        //将file文件转成multipart
        File file = new File("e:/test.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        mediaServiceClient.upload(multipartFile,"course/test.html");
    }

}
