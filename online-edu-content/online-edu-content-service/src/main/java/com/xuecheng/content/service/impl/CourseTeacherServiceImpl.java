package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author QRH
 * @date 2023/6/24 18:57
 * @description
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Autowired
    private CourseBaseMapper courseBaseMapper;


    @Override
    public List<CourseTeacherDto> getCourseTeacherData(Long courseId) {
        if (courseId == null || courseId <= 0) {
            XueChengPlusException.cast("课程id为空或id为非法值");
        }
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);

        return changeToCourseTeachDto(courseTeachers);
    }

    @Override
    public List<CourseTeacherDto> createCourseTeacher(Long companyId, CourseTeacherDto courseTeacherDto) {
        CourseBase courseBase = courseBaseMapper.selectById(courseTeacherDto.getCourseId());
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在，无法删除");
        }
        if (companyId.intValue() != courseBase.getCompanyId()) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        CourseTeacher teacher = new CourseTeacher();

        //将传过来的courseTeacher数据复制到新对象中
        BeanUtils.copyProperties(courseTeacherDto, teacher);
        //插入数据库
        int i = courseTeacherMapper.insert(teacher);
        if (i <= 0) {
            XueChengPlusException.cast("插入课程教师信息失败");
        }
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CourseTeacher> wrapper = queryWrapper.eq(CourseTeacher::getCourseId, teacher.getCourseId());

        List<CourseTeacher> newCourseTeacher = courseTeacherMapper.selectList(wrapper);

        return changeToCourseTeachDto(newCourseTeacher);
    }

    @Override
    public List<CourseTeacherDto> updateCourseTeacher(Long companyId, CourseTeacherDto courseTeacherDto) {
        CourseBase courseBase = courseBaseMapper.selectById(courseTeacherDto.getCourseId());
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在，无法删除");
        }
        if (companyId.intValue() != courseBase.getCompanyId()) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        CourseTeacher teacher = courseTeacherMapper.selectById(courseTeacherDto.getId());
        //将传过来的courseTeacher数据复制到新对象中
        BeanUtils.copyProperties(courseTeacherDto, teacher);
        //更新教师信息
        int i = courseTeacherMapper.updateById(teacher);
        if (i <= 0) {
            XueChengPlusException.cast("更新教师信息失败");
        }

        List<CourseTeacher> teacherList = courseTeacherMapper.selectList(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseTeacherDto.getCourseId())
        );

        return changeToCourseTeachDto(teacherList);
    }

    @Override
    public void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在，无法删除");
        }
        if (companyId.intValue() != courseBase.getCompanyId()) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }
        if (courseId == null || courseId <= 0) {
            XueChengPlusException.cast("courseId有误，无法删除教师信息");
        }
        if (teacherId == null || teacherId <= 0) {
            XueChengPlusException.cast("教师id有误");
        }
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getId, teacherId).eq(CourseTeacher::getCourseId, courseId);
        int i = courseTeacherMapper.delete(queryWrapper);
        if (i <= 0) {
            XueChengPlusException.cast("删除失败，不存在该条记录");
        }
    }

    /**
     * 将CourseTeacher转换成CourseTeacherDto
     *
     * @param courseTeachers
     * @return
     */
    private List<CourseTeacherDto> changeToCourseTeachDto(List<CourseTeacher> courseTeachers) {
        if (courseTeachers.size() > 0) {
            List<CourseTeacherDto> courseTeacherDtos = new ArrayList<CourseTeacherDto>();
            CourseTeacherDto courseTeacherDto = new CourseTeacherDto();

            if (courseTeachers.size() > 1) {
                for (int i = 0; i < courseTeachers.size(); i++) {
                    BeanUtils.copyProperties(courseTeachers.get(i), courseTeacherDto);
                    courseTeacherDtos.add(courseTeacherDto);
                    courseTeacherDto = new CourseTeacherDto();
                }
            } else if (courseTeachers.size() == 1) {
                BeanUtils.copyProperties(courseTeachers.get(0), courseTeacherDto);
                courseTeacherDtos.add(courseTeacherDto);
            }
            //根据id实现元素正排序
            courseTeacherDtos = courseTeacherDtos.stream()
                    .sorted(Comparator.comparing(
                            CourseTeacherDto::getId)
                    )
                    .collect(Collectors.toList()
                    );
            return courseTeacherDtos;

        } else {
            return null;
        }
    }
}