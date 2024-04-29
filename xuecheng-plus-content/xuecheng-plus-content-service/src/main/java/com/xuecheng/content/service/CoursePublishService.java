package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

/**
 * @author QRH
 * @date 2023/7/6 23:23
 * @description 课程发布
 */
public interface CoursePublishService {
    /**
     * 获取课程预览信息
     *
     * @param courseId 课程id
     * @return CoursePreviewDto
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交审核
     *
     * @param companyId 机构id
     * @param courseId  课程id
     */
    public void commitAudit(Long companyId, Long courseId);

    /**
     * 课程发布
     *
     * @param companyId 机构id
     * @param courseId  课程id
     */
    void publish(Long companyId, Long courseId);

    /**
     * @param courseId 课程id
     * @return File 静态化文件
     * @description 课程静态化
     */
    public File generateCourseHtml(Long courseId);

    /**
     * @param file 静态化文件
     * @return void
     * @description 上传课程静态化页面
     */
    public void uploadCourseHtml(Long courseId, File file);

    /**
     * 获取课程发布信息
     * @param courseId 课程id
     * @return
     */
    public CoursePublish getCoursePublish(Long courseId);
}
