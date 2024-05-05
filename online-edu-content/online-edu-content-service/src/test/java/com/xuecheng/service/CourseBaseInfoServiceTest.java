package com.xuecheng.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author QRH
 * @date
 * @description
 */
@SpringBootTest
public class CourseBaseInfoServiceTest {
    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;


    @Test
    public void testCourseBaseInfoService(){
        //查询条件
        QueryCourseParamDto courseParamDto = new QueryCourseParamDto();
        courseParamDto.setPublishStatus("203001");
        Long companyId=1l;
        //分页参数对象
        PageParams pageParams = new PageParams(1L, 2L);
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(companyId,pageParams, courseParamDto);
        System.out.println("courseBasePageResult = " + courseBasePageResult.getCounts());
    }






    @Test
    public void testCourseBaseMapper(){
        CourseBase courseBase = courseBaseMapper.selectById(18);
        Assertions.assertNotNull(courseBase);

        //分页查询单元测试
        //查询条件
        QueryCourseParamDto courseParamDto = new QueryCourseParamDto();
        courseParamDto.setCourseName("java"); //课程名称查询
        //拼装查询对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据名称模糊查询，在sql拼装course_base.name like %值%
        queryWrapper.like(StringUtils.isNotEmpty(courseParamDto.getCourseName()),CourseBase::getName,courseParamDto.getCourseName());
        //课程的审核状态 course_base.audit_status=?
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamDto.getAuditStatus()),CourseBase::getAuditStatus,courseParamDto.getAuditStatus());
        //创建分页参数对象
        PageParams pageParams = new PageParams(1L,2L);
        //创建分页参数对象，参数：当前页码，每页记录数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //开始并进行分页查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //数据列表
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();
        PageResult<CourseBase> result = new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
        System.out.println("result = " + result);
    }
}
