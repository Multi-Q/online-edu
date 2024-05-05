package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author QRH
 * @date 2023/6/17 18:28
 * @description
 */
@Api(value = "课程基本信息接口", tags = "课程基本信息Controller")
@RestController
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @ApiOperation(value = "课程分页查询")
    @PostMapping("/course/list")
//    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')") //制定权限标识符
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamDto queryCourseParamDto) {
//        SecurityUtil.XcUser user = SecurityUtil.getUser();
//        String companyIdStr = user.getCompanyId();
//        Long companyId = StringUtils.isNotEmpty(companyIdStr)? 0l:Long.parseLong(companyIdStr);
        Long companyId = 1232141425l;
        return courseBaseInfoService.queryCourseBaseList(companyId,pageParams, queryCourseParamDto);
    }

    @ApiOperation("新增基本课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBaseInfo(@RequestBody @Validated(value = ValidationGroups.Insert.class) AddCourseDto addCourseDto) {

        Long companyId = 1232141425l;
        return courseBaseInfoService.createCourseBase(companyId, addCourseDto);
    }

    @ApiOperation("根据课程id查询课程")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable(value = "courseId") @ApiParam(value = "课程id") Long courseId) {
        //获取当前用户的身份
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("principal = " + principal);
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated(value = ValidationGroups.Update.class) EditCourseDto editCourseDto) {
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
    }
}
