package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author QRH
 * @date 2023/6/17 18:21
 * @description 分页查询结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    //    数据列表
    @ApiModelProperty(value="返回的数据列表")
    private List<T> items;
    //    总记录数
    @ApiModelProperty(value="总记录数")
    private long counts;
    //    当前页码
    @ApiModelProperty(value="当前页码")
    private long page;
    //    每页记录数
    @ApiModelProperty(value="每页记录数")
    private long pageSize;
}
