package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author QRH
 * @date 2023/6/17 18:16
 * @description 课程条件查询类
 */
@Data
@ApiModel(value = "QueryCourseParamDto",description = "用于查询课程的参数dto")
public class QueryCourseParamDto {
    //    审核状态
    @ApiModelProperty("审核状态")
    private String auditStatus;
    //    课程名称
    @ApiModelProperty("课程名称")
    private String courseName;
    //    发布状态
    @ApiModelProperty("发布状态")
    private String publishStatus;
}
