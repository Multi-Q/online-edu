package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 课程分类
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("course_category")
@ApiModel(value = "CourseCategory", description = "课程分类po类")
public class CourseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    private String name;

    /**
     * 分类标签默认和名称一样
     */
    @ApiModelProperty(value = "分类标签默认和名称一样")
    private String label;

    /**
     * 父结点id（第一级的父节点是0，自关联字段id）
     */
    @ApiModelProperty(value = "父结点id（第一级的父节点是0，自关联字段id）")
    private String parentid;

    /**
     * 是否显示
     */
    @ApiModelProperty(value = "是否显示")
    private Integer isShow;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    private Integer orderby;

    /**
     * 是否叶子
     */
    @ApiModelProperty(value = "是否叶子")
    private Integer isLeaf;


}
