package com.xuecheng.media.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 媒资信息
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("media_files")
@ApiModel(value = "MediaFiles", description = "媒资文件信息po类")
public class MediaFiles implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "id,主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构id")
    private Long companyId;

    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称")
    private String companyName;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String filename;

    /**
     * 文件类型（文档，音频，视频）
     */
    @ApiModelProperty(value = "文件类型（文档，音频，视频）")
    private String fileType;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    private String tags;

    /**
     * 存储目录
     */
    @ApiModelProperty(value = "存储目录")
    private String bucket;

    /**
     * 存储路径
     */
    @ApiModelProperty(value = "存储路径")
    private String filePath;


    /**
     * 文件标识
     */
    @ApiModelProperty(value = "文件id")
    private String fileId;

    /**
     * 媒资文件访问地址
     */
    @ApiModelProperty(value = "媒资文件访问地址")
    private String url;


    /**
     * 上传人
     */
    @ApiModelProperty(value = "上传人")
    private String username;

    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime changeDate;

    /**
     * 状态,1:未处理，视频处理完成更新为2
     */
    @ApiModelProperty(value = "状态,1:未处理，视频处理完成更新为2")
    private String status;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    private String auditStatus;

    /**
     * 审核意见
     */
    @ApiModelProperty(value = "审核意见")
    private String auditMind;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    private Long fileSize;

}
