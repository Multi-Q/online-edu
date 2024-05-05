package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程-教师关系表
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("course_teacher")
@ApiModel(value = "CourseTeacher", description = "课程教师po类")
public class CourseTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程标识
     */
    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    /**
     * 教师标识
     */
    @ApiModelProperty(value = "教师姓名")
    private String teacherName;

    /**
     * 教师职位
     */
    @ApiModelProperty(value = "教师职位")
    private String position;

    /**
     * 教师简介
     */
    @ApiModelProperty(value = "教师简介")
    private String introduction;

    /**
     * 照片
     */
    @ApiModelProperty(value = "照片，照片的地址链接")
    private String photograph;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;


}
