package com.xuecheng.learning.service;

import com.xuecheng.base.model.RestResponse;

/**
 * @author QRH
 * @date 2023/8/3 20:39
 * @description TODO
 */
public interface LearningService {
    /**
     * 获取教学视频
     * @param userId 用户id
     * @param courseId 课程id
     * @param teachplanId 教学计划id
     * @param mediaId 媒资文件id
     * @return
     */
    public RestResponse<String> getVideo(String userId,Long courseId,Long teachplanId,String mediaId);
}
