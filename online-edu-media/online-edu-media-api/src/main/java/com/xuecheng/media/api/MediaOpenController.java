package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.simpleframework.xml.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2023/7/7 17:14
 * @description
 */
@Api(value = "MediaOpenController", tags = "媒资文件公开Controller")
@RestController
@RequestMapping("/open")
public class MediaOpenController {
    @Autowired
    private MediaFileService mediaFileService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") @ApiParam("媒资id") String mediaId) {
        MediaFiles mediaFiles = mediaFileService.getFilesById(mediaId);
        if (mediaFiles == null) {
            return RestResponse.validfail("找不到该视频");
        }
        String url = mediaFiles.getUrl();
        if (StringUtils.isEmpty(url)) {
            return RestResponse.validfail("该视频正在处理中");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }
}
