package com.xuecheng.learning.service.impl;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author QRH
 * @date 2023/8/3 20:39
 * @description TODO
 */
@Service
@Slf4j
public class LearningServiceImpl implements LearningService {
    @Autowired
    private MyCourseTablesService myCourseTablesService;
    @Autowired
    private ContentServiceClient contentServiceClient;
    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
       //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish==null){
            return RestResponse.validfail("课程不存在");
        }
        //TODO:从coursepublish中解析出课程计划信息去判断是否支持试学
//        String teachplanJSON = coursepublish.getTeachplan();

        //用户已登录
        if (StringUtils.isNotEmpty(userId)) {
            //通过我的课程表
            XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);
            String learnStatus = learningStatus.getLearnStatus();

            if ("702002".equals(learnStatus)){
                return RestResponse.validfail("无法学习，因为没有选课或选课后没有支付");
            }else if("702003".equals(learnStatus)){
                return RestResponse.validfail("已过期需要申请续期或重新支付");
            }else{
                //有资格学习，要返回视频的播放地址
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }
        }
        //如果用户没有登录
        String charge = coursepublish.getCharge();
        if ("201000".equals(charge)){
            //有资格学习,返回播放地址
            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
            return playUrlByMediaId;
        }
        return RestResponse.validfail("该课程需要购买");
    }
}
