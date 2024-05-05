package com.xuecheng.search.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程索引信息
 * </p>
 *
 * @author itcast
 */
@Data
@ApiModel(value = "小分类列表", description = "课程索引po")
public class CourseIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("课程索引id")
    private Long id;

    /**
     * 机构ID
     */
    @ApiModelProperty("机构ID")
    private Long companyId;

    /**
     * 公司名称
     */
    @ApiModelProperty("公司名称")
    private String companyName;

    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String name;

    /**
     * 适用人群
     */
    @ApiModelProperty("适用人群")
    private String users;

    /**
     * 标签
     */
    @ApiModelProperty("标签")
    private String tags;


    /**
     * 大分类
     */
    @ApiModelProperty("大分类")
    private String mt;

    /**
     * 大分类名称
     */
    @ApiModelProperty("大分类名称")
    private String mtName;

    /**
     * 小分类
     */
    @ApiModelProperty("小分类")
    private String st;

    /**
     * 小分类名称
     */
    @ApiModelProperty("小分类名称")
    private String stName;


    /**
     * 课程等级
     */
    @ApiModelProperty("课程等级")
    private String grade;

    /**
     * 教育模式
     */
    @ApiModelProperty("教育模式")
    private String teachmode;
    /**
     * 课程图片
     */
    @ApiModelProperty("课程图片")
    private String pic;

    /**
     * 课程介绍
     */
    @ApiModelProperty("课程介绍")
    private String description;


    /**
     * 发布时间
     */
    @ApiModelProperty("发布时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 收费规则，对应数据字典--203
     */
    @ApiModelProperty("收费规则，对应数据字典--203")
    private String charge;

    /**
     * 现价
     */
    @ApiModelProperty("现价")
    private Float price;
    /**
     * 原价
     */
    @ApiModelProperty("原价")
    private Float originalPrice;

    /**
     * 课程有效期天数
     */
    @ApiModelProperty("课程有效期天数")
    private Integer validDays;


}
