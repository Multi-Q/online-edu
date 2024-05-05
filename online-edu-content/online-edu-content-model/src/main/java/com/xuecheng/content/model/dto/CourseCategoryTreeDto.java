package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/12 11:51
 */
@Data
@ApiModel(value = "CourseCategoryTreeDto",description = "课程分类树dto")
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

   //子节点
   @ApiModelProperty(value="子节点(数组)")
   List<CourseCategoryTreeDto> childrenTreeNodes;

}
