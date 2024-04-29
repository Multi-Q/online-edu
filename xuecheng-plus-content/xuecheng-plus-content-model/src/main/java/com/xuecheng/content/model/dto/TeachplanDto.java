package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程计划信息模型类
 * @date 2023/2/14 11:23
 */
@Data
@ToString
@ApiModel(value = "TeachplanDto",description = "课程计划dto")
public class TeachplanDto extends Teachplan {
  //与媒资管理的信息
    @ApiModelProperty(value="与媒资管理的信息")
   private TeachplanMedia teachplanMedia;

  //小章节list
  @ApiModelProperty(value="小章节list")
   private List<TeachplanDto> teachPlanTreeNodes;
}
