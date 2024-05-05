package com.xuecheng.search.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @description 搜索课程参数dtl
 * @author Mr.M
 * @date 2022/9/24 22:36
 * @version 1.0
 */
 @Data
 @ToString
 @ApiModel(value="SearchCourseParamDto",description = "搜索课程参数Dto")
public class SearchCourseParamDto {

  //关键字

    @ApiModelProperty("关键字")
  private String keywords;

  //大分类
  @ApiModelProperty("大分类")
  private String mt;

  //小分类
  @ApiModelProperty("小分类")
  private String st;
  //难度等级
  @ApiModelProperty("难度等级")
  private String grade;




}
