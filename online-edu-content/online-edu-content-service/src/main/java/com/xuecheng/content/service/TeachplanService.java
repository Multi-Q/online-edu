package com.xuecheng.content.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author QRH
 * @date 2023/6/20 20:42
 * @description 课程计划管理相关接口
 */
public interface TeachplanService {
    /**
     * 查找课程计划
     *
     * @param courseId 课程id
     * @return List<TeachplanDto>
     */
    public List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 新增/修改/保存课程计划
     *
     * @param saveTeachplanDto saveTeachplanDto
     */
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     *
     * @param id      课程计划的id
     * @param company 该课程所属的公司id
     */
    public void deleteTeachplan(Long id, Long company);

    /**
     * 上移
     *
     * @param id 该章节的id
     */
    void moveUp(Long id);

    /**
     * 下移
     *
     * @param id 该章节的id
     */
    void moveDown(Long id);

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return com.xuecheng.content.model.po.TeachplanMedia
     * @author Mr.M
     * @date 2022/9/14 22:20
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * 删除教学计划绑定媒资
     * @param id   teachplan_media表的id
     * @param mediaId   teachplan_media表的id
     * @return
     */
    RestResponse deleteAssociationMedia(Long id, String mediaId);
}
