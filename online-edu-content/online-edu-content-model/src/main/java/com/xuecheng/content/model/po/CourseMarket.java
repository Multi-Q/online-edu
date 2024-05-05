package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 课程营销信息
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("course_market")
@ApiModel(value = "CourseMarket", description = "课程营销po类")
public class CourseMarket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，课程id
     */
    @ApiModelProperty(value = "主键，课程id")
    private Long id;

    /**
     * 收费规则，对应数据字典
     */
    @ApiModelProperty(value = "收费规则，对应数据字典")
    private String charge;

    /**
     * 现价
     */
    @ApiModelProperty(value = "现价")
    private Float price;

    /**
     * 原价
     */
    @ApiModelProperty(value = "原价")
    private Float originalPrice;

    /**
     * 咨询qq
     */
    @ApiModelProperty(value = "咨询qq")
    private String qq;

    /**
     * 微信
     */
    @ApiModelProperty(value = "微信")
    private String wechat;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * 有效期天数
     */
    @ApiModelProperty(value = "有效期天数")
    private Integer validDays;


}
