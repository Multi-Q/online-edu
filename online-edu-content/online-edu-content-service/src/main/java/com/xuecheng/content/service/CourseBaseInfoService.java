package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * @author QRH
 * @date 2023/6/18 20:07
 * @description 课程信息管理的接口
 */

public interface CourseBaseInfoService {
    /**
     * 课程分页查询
     *
     * @param pageParams          分页查询对象
     * @param queryCourseParamDto 查询条件
     * @return 查询结果
     */
    public PageResult<CourseBase> queryCourseBaseList(Long companyId,PageParams pageParams, QueryCourseParamDto queryCourseParamDto);

    /**
     * 新增课程
     *
     * @param companyId    机构id
     * @param addCourseDto 课程信息
     * @return 课程添加信息
     */
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据课程id查询课程
     * @param courseId 课程id
     * @return  CourseBaseInfoDto
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 修改课程
     * @param companyId 机构id
     * @param editCourseDto 修改的课程信息
     * @return  CourseBaseInfoDto
     */
    public CourseBaseInfoDto updateCourseBase(Long companyId,EditCourseDto editCourseDto);
}
