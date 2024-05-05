package com.xuecheng.content.api;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author QRH
 * @date 2023/7/6 23:01
 * @description
 */
@Controller
@Api(value = "CoursePublishController", tags = "课程发布Controller")
public class CoursePublishController {
    @Autowired
    private CoursePublishService coursePublishService;

    @ApiOperation(value = "根据课程id查询课程预览信息")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId) {
        ModelAndView modelAndView = new ModelAndView();

        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        modelAndView.addObject("model", coursePreviewInfo);
        modelAndView.setViewName("course_template"); //视图名称+.ftl
        return modelAndView;
    }

    @ApiOperation(value = "根据课程id查询课程预览信息")
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") @ApiParam("课程id") Long courseId) {
        Long companyId = 1232141425l;
        coursePublishService.commitAudit(companyId, courseId);
    }

    @ApiOperation(value = "课程发布")
    @ResponseBody
    @PostMapping("/coursepublish/{courseId}")
//    @PostMapping("/r/coursepublish/{courseId}")
    public void coursePublish(@PathVariable("courseId") @ApiParam("课程id") Long courseId) {
        Long companyId = 1232141425l;

        coursePublishService.publish(companyId, courseId);
    }

    @ApiOperation("获取课程发布信息")
    @ResponseBody
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePublish(@PathVariable("courseId") Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        //查询课程发布表
        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        if (coursePublish == null) {
            return coursePreviewDto;
        }
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish, courseBaseInfoDto);
        String teachplanJSON = coursePublish.getTeachplan();
        List<TeachplanDto> teachplansDto = JSON.parseArray(teachplanJSON, TeachplanDto.class);

        String teachersJSON = coursePublish.getTeachers();
        List<CourseTeacherDto> courseTeachersDto = JSON.parseArray(teachersJSON, CourseTeacherDto.class);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplan(teachplansDto);
        coursePreviewDto.setCourseTeacher(courseTeachersDto);
        return coursePreviewDto;
    }

}
