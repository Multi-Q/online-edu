package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author QRH
 * @date
 * @description 分页查询参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParams {
    //    当前页码
    @ApiModelProperty(value = "当前页码")
    private Long pageNo = 1L;
    //    每页显示记录数
    @ApiModelProperty(value="每页显示记录数")
    private Long pageSize = 30L;
}
