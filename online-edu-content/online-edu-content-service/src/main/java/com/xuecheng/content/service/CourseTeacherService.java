package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author QRH
 * @date 2023/6/24 18:54
 * @description
 */
public interface CourseTeacherService {
    /**
     * 获取该课程的师资信息
     * @param courseId  课程id
     * @return
     */
    public List<CourseTeacherDto> getCourseTeacherData(Long courseId);

    /**
     * 新增课程教师
     * @param courseTeacherDto 课程教师信息dto
     * @return  courseTeacher
     */
    List<CourseTeacherDto> createCourseTeacher(Long companyId,CourseTeacherDto courseTeacherDto);

    /**
     * 修改课程教师信息
     * @param courseTeacherDto 课程教师信息dto
     * @return  courseTeacher
     */
    List<CourseTeacherDto> updateCourseTeacher(Long companyId,CourseTeacherDto courseTeacherDto);

    /**
     * 删除课程教师信息
     * @param courseId  课程id
     * @param teacherId 教师id
     * @return  void
     */
    void deleteCourseTeacher(Long companyId,Long courseId, Long teacherId);
}
