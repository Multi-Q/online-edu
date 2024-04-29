package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author QRH
 * @date 2023/7/31 13:23
 * @description
 */
@Service
@Slf4j
public class MyCourseTablesServiceImpl implements MyCourseTablesService {
    @Autowired
    private XcChooseCourseMapper xcChooseCourseMapper;
    @Autowired
    private XcCourseTablesMapper xcCourseTablesMapper;
    @Autowired
    private ContentServiceClient contentServiceClient;

    @Override
    public boolean saveChooseCourseSuccess(String chooseCourseId) {
        //更具选课id查询选课表
        XcChooseCourse chooseCourse = xcChooseCourseMapper.selectById(chooseCourseId);
        if (chooseCourse==null){
            log.debug("接收到购买的课程的消息，根据选课id从数据库找不到选课记录表,{}",chooseCourseId);
            return false;
        }
        String status = chooseCourse.getStatus();
        if (status.equals("701002")){
            //更新选课记录的状态为支付成功
            chooseCourse.setStatus("701001");
            int i = xcChooseCourseMapper.updateById(chooseCourse);
            if (i<=0){
                log.debug("添加选课记录失败，{}",chooseCourse);
                XueChengPlusException.cast("添加选课记录失败");
            }
            //向我的课程表插入记录
            XcCourseTables xcCourseTables = addCourseTables(chooseCourse);
            return true;
        }

        return false;
    }

    @Transactional
    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        //选课调用内容管理查询课程的收费规则
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish == null) {
            XueChengPlusException.cast("课程不存在");
        }
        //收费规则
        XcChooseCourse chooseCourse = null;
        String charge = coursepublish.getCharge();
        if ("201000".equals(charge)) {
            //如果免费课程，会向选课记录表，我的课程表写数据
            chooseCourse = addFreeCourse(userId, coursepublish);
            //向我的课程表写
            XcCourseTables courseTables = addCourseTables(chooseCourse);
        } else {
            //如果课程收费，会向选课记录表写数据
            chooseCourse = addChargeCourse(userId, coursepublish);
        }
        XcCourseTablesDto learningStatus = getLearningStatus(userId, courseId);
        XcChooseCourseDto chooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(learningStatus, chooseCourseDto);
        chooseCourseDto.setLearnStatus(learningStatus.getLearnStatus());
        return chooseCourseDto;
    }

    @Override
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId) {
        //查询我的课程表，如果查询不到说明没有选课
        XcCourseTables courseTables = getXcCourseTables(userId, courseId);
        XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();
        if (courseTables == null) {
            xcCourseTablesDto.setLearnStatus("702002");
            return xcCourseTablesDto;
        }
        //如果查到，判断是否过期，如果过期不能继续学习，没有过期则可以选课学习
        boolean before = courseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if (before) {
            BeanUtils.copyProperties(courseTables, xcCourseTablesDto);
            xcCourseTablesDto.setLearnStatus("702003");
            return xcCourseTablesDto;
        } else {
            BeanUtils.copyProperties(courseTables, xcCourseTablesDto);
            xcCourseTablesDto.setLearnStatus("702001");
            return xcCourseTablesDto;
        }
    }

    @Override
    public PageResult<XcCourseTables> myCourseTables(MyCourseTableParams params) {
        //当前页码
        int pageNo = params.getPage();
        //每页记录数
        int size = params.getSize();

        Page<XcCourseTables> courseTablesPage = new Page<>(pageNo,size);
        LambdaQueryWrapper<XcCourseTables> queryWrapper = new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, params.getUserId());
        Page<XcCourseTables> result = xcCourseTablesMapper.selectPage(courseTablesPage, queryWrapper);
        //数据列表
        List<XcCourseTables> records = result.getRecords();
        //总记录数
        long total = result.getTotal();
        PageResult<XcCourseTables> pageResult = new PageResult<>(records, total, pageNo, size);
        return pageResult;
    }

    /**
     * 添加免费课程，免费课程加入选课记录表，我的课程表
     *
     * @param userId
     * @param coursePublish
     * @return
     */
    private XcChooseCourse addFreeCourse(String userId, CoursePublish coursePublish) {
        //如果存在免费选课记录且选课状态为成功，直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, coursePublish.getId())
                .eq(XcChooseCourse::getOrderType, "700001")
                .eq(XcChooseCourse::getStatus, "701001");
        List<XcChooseCourse> chooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if (chooseCourses.size() > 0) {
            return chooseCourses.get(0);
        }
        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();
        chooseCourse.setCourseId(coursePublish.getId());
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType("700001");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursePublish.getPrice());
        chooseCourse.setValidDays(365);
        chooseCourse.setStatus("701001");
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365L));
        int insert = xcChooseCourseMapper.insert(chooseCourse);
        if (insert <= 0) {
            XueChengPlusException.cast("添加选课失败");
        }
        return chooseCourse;
    }

    /**
     * 添加到我的课程表
     *
     * @param chooseCourse
     * @return
     */
    private XcCourseTables addCourseTables(XcChooseCourse chooseCourse) {
        //选课成功了才能写入我的课程表
        String status = chooseCourse.getStatus();
        if (!"701001".equals(status)) {
            XueChengPlusException.cast("选课没有成功，无法添加到课程表");
        }
        XcCourseTables courseTables = getXcCourseTables(chooseCourse.getUserId(), chooseCourse.getId());
        if (courseTables != null) {
            return courseTables;
        }
        courseTables = new XcCourseTables();
        BeanUtils.copyProperties(chooseCourse, courseTables);
        courseTables.setChooseCourseId(chooseCourse.getId());//记录选课表的主键
        courseTables.setCourseType(chooseCourse.getOrderType()); //选课类型
        courseTables.setUpdateDate(LocalDateTime.now());
        int insert = xcCourseTablesMapper.insert(courseTables);
        if (insert <= 0) {
            XueChengPlusException.cast("添加免费课程到我的课程表失败");
        }
        return courseTables;
    }

    /**
     * 添加收费课程
     *
     * @param userId
     * @param coursePublish
     * @return
     */
    private XcChooseCourse addChargeCourse(String userId, CoursePublish coursePublish) {
        //如果存在收费选课记录且选课状态为待支付，直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, coursePublish.getId())
                .eq(XcChooseCourse::getOrderType, "700002")//收费订单
                .eq(XcChooseCourse::getStatus, "701002");//待支付
        List<XcChooseCourse> chooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if (chooseCourses.size() > 0) {
            return chooseCourses.get(0);
        }
        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();
        chooseCourse.setCourseId(coursePublish.getId());
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType("700002");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursePublish.getPrice());
        chooseCourse.setValidDays(365);
        chooseCourse.setStatus("701002");
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365L));
        int insert = xcChooseCourseMapper.insert(chooseCourse);
        if (insert <= 0) {
            XueChengPlusException.cast("添加选课失败");
        }
        return chooseCourse;
    }

    /**
     * 获取该用户的课程
     *
     * @param userId
     * @param courseId
     * @return
     */
    private XcCourseTables getXcCourseTables(String userId, Long courseId) {
        LambdaQueryWrapper<XcCourseTables> queryWrapper = new LambdaQueryWrapper<XcCourseTables>()
                .eq(XcCourseTables::getUserId, userId)
                .eq(XcCourseTables::getCourseId, courseId);

        return xcCourseTablesMapper.selectOne(queryWrapper);
    }

}
