package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author QRH
 * @date 2023/6/20 20:42
 * @description
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;


    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程id判断是否是新增还是修改
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null) {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            //确定排序字段，找到同级节点个数，排序字段加1
            Long parentid = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();

            teachplan.setOrderby(getTeachplanCount(courseId, parentid));

            teachplanMapper.insert(teachplan);
        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            //将参数赋值到teachplan
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long id, Long companyId) {
        /*
        * 1、删除大章节，大章节下有小章节时不允许删除。
            2、删除大章节，大单节下没有小章节时可以正常删除。
            3、删除小章节，同时将关联的信息进行删除。
        * */

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<TeachplanMedia> teachplanMediaWrapper = new LambdaQueryWrapper<>();

        // 1、删除大章节，大章节下有小章节时不允许删除。
        //判断id和company是否为空
        if (id == null || id <= 0) {
            XueChengPlusException.cast("该课程计划id值不存在或非法");
        }
        if (companyId == null || companyId <= 0) {
            XueChengPlusException.cast("该公司不存在");
        }
        //获取teachplan对象
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
            XueChengPlusException.cast("没有该课程计划");
        }
        //查询该课程是否是该公司的
        CourseBase courseBase = courseBaseMapper.selectById(teachplan.getCourseId());
        if (courseBase == null) {
            XueChengPlusException.cast("课程基本信息为空");
        }
        //是大章节
        boolean is_parent = teachplan.getParentid() == 0 ? true : false;
        if (is_parent) {
            //查找大章节下是否有小章节
            LambdaQueryWrapper<Teachplan> wrapper = queryWrapper.eq(Teachplan::getParentid, teachplan.getId())
                    .eq(Teachplan::getCourseId, teachplan.getCourseId())
                    .eq(Teachplan::getGrade, teachplan.getGrade() + 1);
            long childrenCount = teachplanMapper.selectList(wrapper).stream().count();
            if (childrenCount == 0) {
                //没有子级章节，允许删除。由于大章节没有视频所以不需要处理teachplan_media表中的数据
                teachplanMapper.deleteById(teachplan.getId());
            } else {
                //有子级章节，不允许删除，并提示
                XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
            }
        } else {
            //不是大章节
            LambdaQueryWrapper<Teachplan> wrapper = queryWrapper.eq(Teachplan::getId, teachplan.getId())
                    .eq(Teachplan::getCourseId, teachplan.getCourseId())
                    .eq(Teachplan::getGrade, teachplan.getGrade());
            //删除teachplan_media表的数据
            LambdaQueryWrapper<TeachplanMedia> mediaWrapper = teachplanMediaWrapper.eq(TeachplanMedia::getTeachplanId, teachplan.getId())
                    .eq(TeachplanMedia::getCourseId, teachplan.getCourseId());
            teachplanMapper.delete(wrapper);
            teachplanMediaMapper.delete(mediaWrapper);
        }
    }

    @Override
    public void moveUp(Long id) {
        //判断id是否为空
        if (id == null || id <= 0) {
            XueChengPlusException.cast("id为空或id为非法值");
        }
        //通过该章节的id判断是否是父节点
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
            XueChengPlusException.cast("无法查询出该id的数据");
        }
        //找到课程基本信息id
        Long courseBaseId = courseBaseMapper.selectById(teachplan.getCourseId()).getId();

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();

        if (teachplan.getOrderby() == 1) {
            XueChengPlusException.cast("该章节序号排在第一，无法上移");
        } else {
            if (teachplan.getParentid() == 0) {
                //是父节点。如果是父节点，就找该节点以及其后面的结点的父节点，找出用一个list接收，遍历list并逐条修改orderby数值
                LambdaQueryWrapper<Teachplan> wrapper = queryWrapper.
                        eq(teachplan.getParentid() == 0, Teachplan::getParentid, teachplan.getParentid())
                        .eq(teachplan.getCourseId() == courseBaseId, Teachplan::getCourseId, teachplan.getCourseId())
                        .between(Teachplan::getOrderby, teachplan.getOrderby() - 1, teachplan.getOrderby());

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
                        .between(Teachplan::getOrderby, teachplan.getOrderby() - 1, teachplan.getOrderby());

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

    @Override
    public void moveDown(Long id) {
        //判断id是否为空
        if (id == null || id <= 0) {
            XueChengPlusException.cast("id为空或id为非法值");
        }
        //后去该id的teachplan对象
        Teachplan teachplan = teachplanMapper.selectById(id);

        if (teachplan == null) {
            XueChengPlusException.cast("无法查询出该id的数据");
        }
        //找到课程基本信息id
        Long courseBaseId = courseBaseMapper.selectById(teachplan.getCourseId()).getId();

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();

        //获取该id的parentid的个数
        Integer counts = teachplanMapper.selectCount(queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid())
                .eq(Teachplan::getCourseId, courseBaseId));
        //判断如果是最后章节，抛出异常
        if (teachplan.getOrderby() == counts) {
            XueChengPlusException.cast("该章节是最后一章，无法下移");
        }

        if (teachplan.getParentid() == 0) {
            //是父节点。如果是父节点，就找该节点以及其后面的结点的父节点，找出用一个list接收，遍历list并逐条修改orderby数值
            LambdaQueryWrapper<Teachplan> wrapper = queryWrapper.
                    eq(teachplan.getParentid() == 0, Teachplan::getParentid, teachplan.getParentid())
                    .eq(teachplan.getCourseId() == courseBaseId, Teachplan::getCourseId, teachplan.getCourseId())
                    .between(Teachplan::getOrderby, teachplan.getOrderby(), teachplan.getOrderby() + 1);

            List<Teachplan> teachplanFatherList = teachplanMapper.selectList(wrapper);

            //交换两个数组中的orderby值
            Teachplan one = teachplanFatherList.get(0);
            Teachplan two = teachplanFatherList.get(1);
            //创建临时变量存储第一个数组元素的orderby值
            Integer tempOrder = one.getOrderby();
            one.setOrderby(two.getOrderby());
            two.setOrderby(tempOrder);
            //更新数据
            teachplanMapper.updateById(one);
            teachplanMapper.updateById(two);
        } else {
            //那就是大章节下面的子节点
            LambdaQueryWrapper<Teachplan> wrapper = queryWrapper
                    .eq(Teachplan::getParentid, teachplan.getParentid())
                    .eq(courseBaseId == teachplan.getCourseId(), Teachplan::getCourseId, teachplan.getCourseId())
                    .eq(Teachplan::getGrade, teachplan.getGrade())
                    .between(Teachplan::getOrderby, teachplan.getOrderby(), teachplan.getOrderby() + 1);

            List<Teachplan> teachplanFatherList = teachplanMapper.selectList(wrapper);

            //交换两个数组中的orderby值
            Teachplan one = teachplanFatherList.get(0);
            Teachplan two = teachplanFatherList.get(1);
            //创建临时变量存储第一个数组元素的orderby值
            Integer tempOrder = one.getOrderby();
            one.setOrderby(two.getOrderby());
            two.setOrderby(tempOrder);
            //更新数据
            teachplanMapper.updateById(one);
            teachplanMapper.updateById(two);
        }
    }

    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null) {
            XueChengPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if (grade != 2) {
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原有记录，根据课程计划id删除它所绑定的媒资
        int delete = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId())
        );
        //再添加记录
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto, teachplanMedia);
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }


    @Override
    public RestResponse deleteAssociationMedia(Long id, String mediaId) {
        //删除teachplan_media表中的记录就行了，并不是删除和media_files表的视频

        int i = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(!(id <= 0), TeachplanMedia::getTeachplanId, id)
                .eq(StringUtils.isNotEmpty(mediaId), TeachplanMedia::getMediaId, mediaId));
        if (i > 0) {
            return RestResponse.success(200, "删除成功");
        } else {
            return RestResponse.validfail(201, "删除失败");
        }

    }

    /**
     * 获取同级章节个数
     *
     * @param courseId 课程id
     * @param parentId 该课程的父节点
     * @return 同级结点总数
     */
    private int getTeachplanCount(long courseId, long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId)
                .eq(Teachplan::getParentid, parentId);
        return teachplanMapper.selectCount(queryWrapper) + 1;
    }
}
