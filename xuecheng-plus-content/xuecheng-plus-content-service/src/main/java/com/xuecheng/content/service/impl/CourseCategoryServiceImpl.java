package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author QRH
 * @date 2023/6/19 18:37
 * @description
 */
@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //调用mapper递归查询出分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .collect((Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2)));
        //定义一个list作为最终返回list
        ArrayList<CourseCategoryTreeDto> courseCategoryList = new ArrayList<>();
        //从头便利List<CourseCategoryTreeDto>,一边遍历一编找子节点放在父节点的childrenTreeNodes
        courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .forEach(item -> {
                    if (item.getParentid().equals(id)) {
                        courseCategoryList.add(item);
                    }
                    //找到结点的父节点
                    CourseCategoryTreeDto courseCategoryParent = mapTemp.get(item.getParentid());
                    if (courseCategoryParent != null) {
                        if (courseCategoryParent.getChildrenTreeNodes() == null) {
                            //如果该父节点的ChildrenTreeNodes属性为空就要new一个集合，因为我们要向集合中放他的子节点
                            courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                        }
                        //找到每个子节点的父节点的childrenTreeNodes属性中
                        courseCategoryParent.getChildrenTreeNodes().add(item);
                    }
                });

        return courseCategoryList;
    }
}
