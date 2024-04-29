package com.xuecheng.auth.controller;

import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.WxAuthService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2023/7/12 23:53
 * @description
 */
@Controller
@Slf4j
@Api(value = "WxLonginController", tags = "微信登录接口")
public class WxLonginController {
    @Autowired
    private WxAuthService wxAuthService;

    @RequestMapping("/wxLogin")
    public String WxLogin(String code, String state) {
        log.debug("微信扫码回调，code:{},state:{}", code, state);
        //请求微信携带令牌，拿到令牌查询用户信息，将用户信息写入本地数据库
        XcUser xcUser1 = wxAuthService.wxAuth(code);

        XcUser user = new XcUser();
        //暂时硬编码
        user.setUsername("t1");
        if (user == null) {
            return "redirect:http://www.51xuecehng.cn/error.html";
        }
        String username = user.getUsername();
        return "redirect:http://www.51xuecehng.cn/sign.html?username=" + username + "&authType=wx";
    }
}
