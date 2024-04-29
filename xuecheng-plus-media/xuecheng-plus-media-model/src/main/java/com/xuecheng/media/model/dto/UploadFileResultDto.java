package com.xuecheng.media.model.dto;

import com.xuecheng.media.model.po.MediaFiles;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author QRH
 * @version 1.0
 * @description 上传文件成功返回信息
 */
@Data
@ToString
@ApiModel(value = "UploadFileResultDto", description = "上传文件成功返回结果dto")
public class UploadFileResultDto extends MediaFiles {

    @ApiModelProperty(value = "图片的URL地址", notes = "图片上传后将MediaFiles的url值赋值给photograph作为教师图片的URL地址")
    private String photograph;

}
