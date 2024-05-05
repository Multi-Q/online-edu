package com.xuecheng.learning.service;

import com.xuecheng.base.model.PageResult;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcCourseTables;

/**
 * @author QRH
 * @date 2023/7/31 13:21
 * @description
 */

public interface MyCourseTablesService {
    /**
     * 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @return
     */
    public XcChooseCourseDto addChooseCourse(String userId,Long courseId);
    /**
     * @description 判断学习资格
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto
     */
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    /**
     * 保存选课成功消息
     * @param chooseCourseId
     * @return
     */
    public boolean saveChooseCourseSuccess(String chooseCourseId);

    /**
     * 我的课程表
     * @param params
     * @return
     */
    public PageResult<XcCourseTables> myCourseTables(MyCourseTableParams params);
}
