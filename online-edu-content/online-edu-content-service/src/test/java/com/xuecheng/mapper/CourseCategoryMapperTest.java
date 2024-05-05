package com.xuecheng.mapper;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author QRH
 * @date 2023/6/19 16:48
 * @description
 */
@SpringBootTest
public class CourseCategoryMapperTest {
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Test
    public void testSelectTreeNodes(){
        List<CourseCategoryTreeDto> res =  courseCategoryMapper.selectTreeNodes("1");
        System.out.println(res.stream().count());
    }
}
