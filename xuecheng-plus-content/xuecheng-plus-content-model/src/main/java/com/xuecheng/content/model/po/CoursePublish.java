package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程发布
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("course_publish")
@ApiModel(value = "CoursePublish", description = "课程发布po类")
public class CoursePublish implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    private Long companyId;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称")
    private String name;

    /**
     * 适用人群
     */
    @ApiModelProperty(value = "适用人群")
    private String users;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    private String tags;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String username;

    /**
     * 大分类
     */
    @ApiModelProperty(value = "大分类")
    private String mt;

    /**
     * 大分类名称
     */
    @ApiModelProperty(value = "大分类名称")
    private String mtName;

    /**
     * 小分类
     */
    @ApiModelProperty(value = "小分类")
    private String st;

    /**
     * 小分类名称
     */
    @ApiModelProperty(value = "小分类名称")
    private String stName;

    /**
     * 课程等级
     */
    @ApiModelProperty(value = "课程等级")
    private String grade;

    /**
     * 教育模式
     */
    @ApiModelProperty(value = "教育模式")
    private String teachmode;

    /**
     * 课程图片
     */
    @ApiModelProperty(value = "课程图片")
    private String pic;

    /**
     * 课程介绍
     */
    @ApiModelProperty(value = "课程介绍")
    private String description;

    /**
     * 课程营销信息，json格式
     */
    @ApiModelProperty(value = "课程营销信息，json格式")
    private String market;

    /**
     * 所有课程计划，json格式
     */
    @ApiModelProperty(value = "所有课程计划，json格式")
    private String teachplan;

    /**
     * 教师信息，json格式
     */
    @ApiModelProperty(value = "教师信息，json格式")
    private String teachers;

    /**
     * 发布时间
     */
    @ApiModelProperty(value = "发布时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 上架时间
     */
    @ApiModelProperty(value = "上架时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime onlineDate;

    /**
     * 下架时间
     */
    @ApiModelProperty(value = "下架时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime offlineDate;

    /**
     * 发布状态
     */
    @ApiModelProperty(value = "发布状态")
    private String status;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 收费规则，对应数据字典--203
     */
    @ApiModelProperty(value = "收费规则，对应数据字典--203")
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
     * 课程有效期天数
     */
    @ApiModelProperty(value = "课程有效期天数")
    private Integer validDays;


}
