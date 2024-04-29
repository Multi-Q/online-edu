package com.xuecheng.ucenter.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.M
 * @version 1.0
 * @description 认证用户请求参数
 * @date 2022/9/29 10:56
 */
@Data
@ApiModel(value = "AuthParamsDto", description = "认证授权参数Dto")
public class AuthParamsDto {
    @ApiModelProperty("用户名")
    private String username; //用户名
    @ApiModelProperty("密码")
    private String password; //域  用于扩展
    @ApiModelProperty("手机号码")
    private String cellphone;//手机号
    @ApiModelProperty("验证码")
    private String checkcode;//验证码
    @ApiModelProperty("验证码key")
    private String checkcodekey;//验证码key
    @ApiModelProperty("认证类型")
    private String authType; // 认证的类型   password:用户名密码模式类型    sms:短信模式类型
    @ApiModelProperty("附加数据")
    private Map<String, Object> payload = new HashMap<>();//附加数据，作为扩展，不同认证类型可拥有不同的附加数据。如认证类型为短信时包含smsKey : sms:3d21042d054548b08477142bbca95cfa; 所有情况下都包含clientId


}
