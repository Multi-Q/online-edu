package com.xuecheng.ucenter.feighclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author QRH
 * @date 2023/7/12 19:13
 * @description
 */
@FeignClient(value = "checkcode",fallbackFactory = CheckCodeClientFactory.class)
@RequestMapping("/checkcode")
public interface CheckCodeClient {
    @PostMapping("/verify")
    public Boolean verify(@RequestParam("key")String key,@RequestParam("code")String code);
}
