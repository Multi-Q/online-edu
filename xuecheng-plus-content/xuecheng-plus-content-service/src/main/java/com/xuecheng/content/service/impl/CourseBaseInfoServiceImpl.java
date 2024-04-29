package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author QRH
 * @date 2023/6/18 20:09
 * @description
 */
@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Resource
    private CourseBaseMapper courseBaseMapper;
    @Resource
    private CourseMarketMapper courseMarketMapper;
    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(Long companyId,PageParams pageParams, QueryCourseParamDto queryCourseParamDto) {

        //拼装查询对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据名称模糊查询，在sql拼装course_base.name like %值%
        queryWrapper.like(
                StringUtils.isNotEmpty(queryCourseParamDto.getCourseName())
                , CourseBase::getName
                , queryCourseParamDto.getCourseName()
        )
        //课程的审核状态 course_base.audit_status=?
       .eq(
                StringUtils.isNotEmpty(queryCourseParamDto.getAuditStatus())
                , CourseBase::getAuditStatus
                , queryCourseParamDto.getAuditStatus()
        )
        //课程发布状态
        .eq(
                StringUtils.isNotEmpty(queryCourseParamDto.getPublishStatus())
                , CourseBase::getStatus
                , queryCourseParamDto.getPublishStatus()
        )
        //根据培训机构查询条件
        .eq(CourseBase::getCompanyId,companyId);

           //创建分页参数对象，参数：当前页码，每页记录数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //开始并进行分页查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //数据列表
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();
        PageResult<CourseBase> result = new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
        return result;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //       向课程基本信息表course_base导入数据
        CourseBase courseBase = new CourseBase();
        //        将传入的页面参数放到courseBase中
        BeanUtils.copyProperties(dto, courseBase); //只要属性名称一致就可以复制
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");
        //插入数据库
        int i = courseBaseMapper.insert(courseBase);
        if (i <= 0) {
            throw new RuntimeException("新增课程基本信息失败");
        }
        //向课程营销表course_market写入数据库
        CourseMarket courseMarket = new CourseMarket();
        //        将页面数据拷贝到courseMarket中
        BeanUtils.copyProperties(dto, courseMarket);
        Long courseId = courseMarket.getId();
        courseMarket.setId(courseId);
        //保存营销信息
        int save = saveCourseMarket(courseMarket);
        if (save <= 0) {
            throw new RuntimeException("保存课程营销信息失败");
        }
        //        从数据库中查出课程
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

        return courseBaseInfo;
    }

    //查询课程基本信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        //        从课程基本信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        //从课程营销表查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //组装在一起
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;
    }

    //单独写一个方法保存营销信息，逻辑，存在则更新，不存在则添加
    private int saveCourseMarket(CourseMarket courseMarket) {
        //参数校验
        String charge = courseMarket.getCharge();
        if (StringUtils.isEmpty(charge)) {
            throw new XueChengPlusException("收费规则没有选择");
        }
        //        如果课程收费，价格没有填写也需要抛出异常
        if ("201001".equals(charge)) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <= 0) {
                throw new XueChengPlusException("课程价格不能为空并且大于0");
            }
        }
        //        从数据库查询营销信息，存在则更新，不存在则添加
        Long id = courseMarket.getId();
        CourseMarket market = courseMarketMapper.selectById(id);
        if (market == null) {
            //插入数据库
            return courseMarketMapper.insert(courseMarket);
        } else {
            //            将courseMarket拷贝到market中
            BeanUtils.copyProperties(courseMarket, market);
            market.setId(courseMarket.getId());

            //更新
            return courseMarketMapper.updateById(market);
        }

    }

    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        //先拿到课程id
        Long courseId = editCourseDto.getId();
        //查询课程信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在");
        }
        //参数合法性校验
        //根据具体的业务逻辑去校验
        //本机构只能修改本机构的课程内容
        if (companyId.intValue()!=courseBase.getCompanyId()) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }
        //封装数据
        BeanUtils.copyProperties(editCourseDto, courseBase);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        //更新数据库
        int i = courseBaseMapper.updateById(courseBase);
        if (i <= 0) {
            XueChengPlusException.cast("修改课程失败");
        }
        //更新营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        saveCourseMarket(courseMarket);
        //查询课程信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }
}
