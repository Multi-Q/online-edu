package com.xuecheng.content.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author QRH
 * @date 2023/6/20 20:37
 * @description 课程计划管理相关接口
 */
@RestController
@Slf4j
@Api(value = "课程计划管理相关接口",tags = "课程计划管理Controller")
public class TeachplanController {
    @Autowired
    private TeachplanService teachplanService;

    @ApiOperation(value="查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTressNodes(@PathVariable(value="courseId") @ApiParam(value="课程id") Long courseId){
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto){
        teachplanService.saveTeachplan(saveTeachplanDto);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{id}")
    public void delTeachplan(@PathVariable @ApiParam("课程计划的id") Long id){
        Long companyId=1232141425l;
        teachplanService.deleteTeachplan(id,companyId);
    }

    @ApiOperation("上移")
    @PostMapping("/teachplan/moveup/{id}")
    public void moveUp(@PathVariable @ApiParam("课程计划的id") Long id){
        teachplanService.moveUp(id);
    }

    @ApiOperation("下移")
    @PostMapping("/teachplan/movedown/{id}")
    public void moveDown(@PathVariable @ApiParam("课程计划的id") Long id){
        teachplanService.moveDown(id);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public TeachplanMedia associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        return  teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation(value = "删除课程计划和媒资绑定")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public RestResponse deleteAssociationMedia(
            @PathVariable("teachPlanId") @ApiParam(value = "xcplus_content数据库下teachplan_media表的id")Long id,
            @PathVariable("mediaId")@ApiParam(value = "xcplus_content数据库下teachplan_media表的media_id")String mediaId){
        Long companyId=1232141425l;
        return teachplanService.deleteAssociationMedia(id,mediaId);
    }
}


