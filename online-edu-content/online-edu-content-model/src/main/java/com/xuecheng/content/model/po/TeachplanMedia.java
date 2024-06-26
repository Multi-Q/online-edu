package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("teachplan_media")
@ApiModel(value = "TeachplanMedia", description = "课程计划媒资po类")
public class TeachplanMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键,id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 媒资文件id
     */
    @ApiModelProperty(value = "媒资文件id")
    private String mediaId;

    /**
     * 课程计划标识
     */
    @ApiModelProperty(value = "课程计划iD")
    private Long teachplanId;

    /**
     * 课程标识
     */
    @ApiModelProperty(value = "课程iD")
    private Long courseId;

    /**
     * 媒资文件原始名称
     */
    @ApiModelProperty(value = "媒资文件原始名称")
    @TableField("media_fileName")
    private String mediaFilename;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPeople;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String changePeople;


}
