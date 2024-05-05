package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author QRH
 * @date 2023/6/18 23:40
 * @description 课程分类相关的接口
 */
@RestController
@Slf4j
@Api(value = "课程分类相关的接口", tags = "课程分类相关Controller")
public class CourseCategoryController {
    @Autowired
    private CourseCategoryService courseCategoryService;

    @ApiOperation(value = "查找TreeNode")
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        return courseCategoryService.queryTreeNodes("1");
    }
}
