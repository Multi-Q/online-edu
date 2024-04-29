package com.xuecheng.ucenter.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("xc_company")
@ApiModel(value = "XcCompany", description = "公司")
public class XcCompany implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("公司id")
    private String id;

    /**
     * 联系人名称
     */
    @ApiModelProperty("联系人名称")
    private String linkname;

    /**
     * 名称
     */
    @ApiModelProperty("公司名称")
    private String name;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 简介
     */
    @ApiModelProperty("简介")
    private String intro;

    /**
     * logo
     */
    @ApiModelProperty("logo，图片路径")
    private String logo;

    /**
     * 身份证照片
     */
    @ApiModelProperty("身份证照片，图片路径")
    private String identitypic;

    /**
     * 工具性质
     */
    @ApiModelProperty("工具性质")
    private String worktype;

    /**
     * 营业执照
     */
    @ApiModelProperty("营业执照,图片路径")
    private String businesspic;

    /**
     * 企业状态
     */
    @ApiModelProperty("企业状态")
    private String status;


}
