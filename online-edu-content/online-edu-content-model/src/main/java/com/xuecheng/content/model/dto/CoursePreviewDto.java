package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author QRH
 * @date 2023/7/6 23:18
 * @description
 */
@Data
@ApiModel(value = "CoursePreviewDto", description = "课程预览dto")
public class CoursePreviewDto {

    //课程基本信息，营销信息
    @ApiModelProperty(value = "课程基本信息dto")
    private  CourseBaseInfoDto courseBase;
    //课程计划信息
    @ApiModelProperty(value = "课程计划信息dto")
    private List<TeachplanDto> teachplan;
    //课程教师信息
    @ApiModelProperty(value = "课程教师信息dto")
    private List<CourseTeacherDto> courseTeacher;

    //课程营销信息
//    ....

}
