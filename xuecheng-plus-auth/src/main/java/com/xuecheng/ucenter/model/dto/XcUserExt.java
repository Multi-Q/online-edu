package com.xuecheng.ucenter.model.dto;

import com.xuecheng.ucenter.model.po.XcUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 用户扩展信息
 * @date 2022/9/30 13:56
 */
@Data
@ApiModel(value = "XcUserExt", description = "用户扩展信息")
public class XcUserExt extends XcUser {
    @ApiModelProperty("用户权限")
    List<String> permissions = new ArrayList<>();
}
