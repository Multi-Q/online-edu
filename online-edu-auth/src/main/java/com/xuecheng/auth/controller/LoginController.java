package com.xuecheng.auth.controller;

import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.po.XcUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 测试controller
 * @date 2022/9/27 17:25
 */
@Slf4j
@RestController
@ApiOperation(value = "LoginController", tags = "登录Controller")
public class LoginController {

    @Autowired
    XcUserMapper userMapper;


    @RequestMapping("/login-success")
    @ApiOperation("登陆成功")
    public String loginSuccess() {

        return "登录成功";
    }


    @RequestMapping("/user/{id}")
    @ApiOperation("获取用户信息")
    public XcUser getuser(@PathVariable("id") @ApiParam("用户id") String id) {
        XcUser xcUser = userMapper.selectById(id);
        return xcUser;
    }

    @RequestMapping("/r/r1")
    @PreAuthorize(value = "hasAuthority('p1')")
    @ApiOperation("访问r1资源")
    public String r1() {
        return "访问r1资源";
    }

    @RequestMapping("/r/r2")
    @ApiOperation("访问r2资源")
    @PreAuthorize(value="hasAuthority('p2')")
    public String r2() {
        return "访问r2资源";
    }


}
