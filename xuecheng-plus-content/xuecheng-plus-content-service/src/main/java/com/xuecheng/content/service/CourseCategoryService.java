package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author QRH
 * @date 2023/6/19 18:37
 * @description 课程分类
 */
public interface CourseCategoryService {
    /**
     * 课程分类树型结构查询
     * @param id course_base表中的id（varchar）
     * @return  一个数组
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);


}
