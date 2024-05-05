package com.xuecheng.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author QRH
 * @date 2023/6/19 19:19
 * @description
 */
@SpringBootTest
public class CourseCategoryServiceTest {
    @Autowired
    private CourseCategoryService courseCategoryService;

    @Test
    public void testQueryTreeNodes() {
        List<CourseCategoryTreeDto> res = courseCategoryService.queryTreeNodes("1");
        System.out.println(res);
    }

}
