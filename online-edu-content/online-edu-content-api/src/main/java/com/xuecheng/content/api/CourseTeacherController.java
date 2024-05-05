package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author QRH
 * @date 2023/6/24 18:53
 * @description
 */
@RestController
@Slf4j
@Api(value = "师资管理接口", tags = "师资管理Controller")
public class CourseTeacherController {
    @Autowired
    private CourseTeacherService courseTeacherService;

    @ApiOperation("查询该课程的教师信息")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacherDto> courseTeacher(@PathVariable @ApiParam("课程id") Long courseId) {
        return courseTeacherService.getCourseTeacherData(courseId);
    }

    @ApiOperation("新增该课程的教师信息")
    @PostMapping("/courseTeacher")
    public List<CourseTeacherDto> createCourseTeacher(@RequestBody @ApiParam("该课程的教师信息对象") CourseTeacherDto courseTeacherDto) {
        Long companyId = 1232141425l;
        return courseTeacherService.createCourseTeacher(companyId, courseTeacherDto);
    }

    @ApiOperation("修改该课程的教师信息")
    @PutMapping("/courseTeacher")
    public List<CourseTeacherDto> modifyCourseTeacher(@RequestBody @ApiParam("该课程的教师信息对象") CourseTeacherDto courseTeacherDto) {
        Long companyId = 1232141425l;
        return courseTeacherService.updateCourseTeacher(companyId, courseTeacherDto);
    }

    @ApiOperation("删除该课程的教师信息")
    @DeleteMapping("/courseTeacher/courseId/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable("courseId") Long courseId, @PathVariable("teacherId") Long teacherId) {
        Long companyId = 1232141425l;
        courseTeacherService.deleteCourseTeacher(companyId, courseId, teacherId);
    }

}
