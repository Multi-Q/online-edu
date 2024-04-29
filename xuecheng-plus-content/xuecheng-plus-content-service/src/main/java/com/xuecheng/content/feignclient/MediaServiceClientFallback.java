package com.xuecheng.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author QRH
 * @date 2023/7/9 21:19
 * @description
 */

public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String upload(MultipartFile upload, String objectName) {
        return null;
    }
}
