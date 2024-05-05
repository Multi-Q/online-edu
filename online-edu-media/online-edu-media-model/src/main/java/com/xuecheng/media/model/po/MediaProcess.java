package com.xuecheng.media.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

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
@ToString
@TableName("media_process")
@ApiModel(value = "MediaProcess", description = "媒资信息处理po类")
public class MediaProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件标识
     */
    @ApiModelProperty(value = "文件id")
    private String fileId;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String filename;

    /**
     * 存储源
     */
    @ApiModelProperty(value = "存储源")
    private String bucket;

    @ApiModelProperty(value = "文件路径")
    private String filePath;

    /**
     * 状态,1:未处理，视频处理完成更新为2
     */
    @ApiModelProperty(value = "状态,1:未处理，视频处理完成更新为2")
    private String status;

    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 完成时间
     */
    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishDate;

    /**
     * 媒资文件访问地址
     */
    @ApiModelProperty(value = "媒资文件访问地址")
    private String url;

    /**
     * 失败原因
     */
    @ApiModelProperty(value = "失败原因")
    private String errormsg;

    /**
     * 失败次数
     */
    @ApiModelProperty(value = "失败次数")
    private int failCount;


}
