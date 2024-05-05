package com.xuecheng.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.Teachplan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author QRH
 * @date 2023/6/21 16:27
 * @description
 */
@SpringBootTest
public class TeachplanServiceTest {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private CourseBaseMapper courseBaseMapper;


    @Test
    public void deleteTeachplan() {
        Long courseId = 82l;
        //找出该courseId的记录
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(courseId != 0 && courseId > 0, Teachplan::getCourseId, courseId)
                .eq(courseId != 0 && courseId > 0, Teachplan::getParentid, 0);

        List<Teachplan> teachplan = teachplanMapper.selectList(queryWrapper);
        System.out.println(teachplan);
    }

    @Test
    public void testMoveUp() {
        //通过该章节的id判断是否是父节点
        Teachplan teachplan = teachplanMapper.selectById(270l);
        //找到课程基本信息id
        Long courseBaseId = courseBaseMapper.selectById(teachplan.getCourseId()).getId();

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();

        if (teachplan.getParentid() == 0) {
            //是父节点。如果是父节点，就找该节点以及其后面的结点的父节点，找出用一个list接收，遍历list并逐条修改orderby数值
            LambdaQueryWrapper<Teachplan> wrapper = queryWrapper.
                    eq(teachplan.getParentid() == 0, Teachplan::getParentid, teachplan.getParentid())
                    .eq(teachplan.getCourseId() == courseBaseId, Teachplan::getCourseId, teachplan.getCourseId())
                    .between(Teachplan::getOrderby, teachplan.getOrderby()-1,teachplan.getOrderby());

            List<Teachplan> teachplanFatherList = teachplanMapper.selectList(wrapper);

            //交换两个数组中的orderby值
            Teachplan one = teachplanFatherList.get(0);
            Teachplan two = teachplanFatherList.get(1);
            //创建临时变量存储第一个数组元素的orderby值
            Integer tempOrder = one.getOrderby();
            one.setOrderby(two.getOrderby());
            two.setOrderby(tempOrder);
//            //更新数据
            teachplanMapper.updateById(one);
            teachplanMapper.updateById(two);
        } else {
            //那就是大章节下面的子节点
            LambdaQueryWrapper<Teachplan> wrapper = queryWrapper
                    .eq(Teachplan::getParentid, teachplan.getParentid())
                    .eq(courseBaseId == teachplan.getCourseId(), Teachplan::getCourseId, teachplan.getCourseId())
                    .eq(Teachplan::getGrade, teachplan.getGrade())
                    .between(Teachplan::getOrderby, teachplan.getOrderby()-1,teachplan.getOrderby());

            List<Teachplan> teachplanFatherList = teachplanMapper.selectList(wrapper);

            //交换两个数组中的orderby值
            Teachplan one = teachplanFatherList.get(0);
            Teachplan two = teachplanFatherList.get(1);
            //创建临时变量存储第一个数组元素的orderby值
            Integer tempOrder = one.getOrderby();
            one.setOrderby(two.getOrderby());
            two.setOrderby(tempOrder);
//            //更新数据
            teachplanMapper.updateById(one);
            teachplanMapper.updateById(two);
        }
    }

}
