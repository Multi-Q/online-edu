package com.xuecheng.content.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import springfox.documentation.spring.web.json.Json;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author QRH
 * @date 2023/7/11 9:16
 * @description
 */
@Slf4j
public class SecurityUtil {
    public static XcUser getUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof String) {
                //取出用户身份信息
                String userObj = principal.toString();
                XcUser user = JSON.parseObject(userObj, XcUser.class);
                return user;
            }
        } catch (Exception e) {
            log.error("获取当前登录用户身份出错:{}", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Data
    public static class XcUser implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private String username;
        private String password;
        private String salt;
        private String name;
        private String nickname;
        private String wxUnionid;
        private String companyId;
        private String userpic;
        private String utype;
        private LocalDateTime birthday;
        private String sex;
        private String email;
        private String cellphone;
        private String qq;
        private String status;
        //    @TableField(fill=FieldFill.INSERT)
        private LocalDateTime createTime;

        private LocalDateTime updateTime;
    }
}
