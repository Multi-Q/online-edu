package com.xuecheng.media.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author QRH
 * @version 1.0
 * @description 媒资文件查询请求模型类
 */
@Data
@ToString
@ApiModel(value = "QueryMediaParamsDto",description = "用于查询媒资信息的参数dto")
public class QueryMediaParamsDto {

    @ApiModelProperty("媒资文件名称")
    private String filename;
    @ApiModelProperty("媒资类型")
    private String fileType;
    @ApiModelProperty("审核状态")
    private String auditStatus;
}
