package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseTeacher;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author QRH
 * @date 2023/6/24 22:31
 * @description CourseTeacherDto
 */
@Data
@ApiModel(value = "CourseTeacherDto",description = "课程教师信息的dto")
public class CourseTeacherDto extends CourseTeacher implements Serializable {
}
