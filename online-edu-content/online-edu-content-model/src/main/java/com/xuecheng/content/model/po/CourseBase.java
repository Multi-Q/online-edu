package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("course_base")
@ApiModel(value = "CourseBase", description = "课程基本po类")
public class CourseBase implements Serializable {

    private static final long serialVersionUID = 1L;





    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "id,主键")
    private Long id;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    private Long companyId;

    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称")
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
     * 课程标签
     */
    @ApiModelProperty(value = "课程标签")
    private String tags;

    /**
     * 大分类
     */
    @ApiModelProperty(value = "大分类")
    private String mt;

    /**
     * 小分类
     */
    @ApiModelProperty(value = "小分类")
    private String st;

    /**
     * 课程等级
     */
    @ApiModelProperty(value = "课程等级")
    private String grade;

    /**
     * 教育模式(common普通，record 录播，live直播等）
     */
    @ApiModelProperty(value = "教育模式(common普通，record 录播，live直播等）")
    private String teachmode;

    /**
     * 课程介绍
     */
    @ApiModelProperty(value = "课程介绍")
    private String description;

    /**
     * 课程图片
     */
    @ApiModelProperty(value = "课程图片")
    private String pic;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime changeDate;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPeople;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String changePeople;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    private String auditStatus;

    /**
     * 课程发布状态 未发布  已发布 下线
     */
    @ApiModelProperty(value = "课程发布状态 未发布  已发布 下线")
    private String status;


}
