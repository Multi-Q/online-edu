package com.xuecheng.media.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("mq_message_history")
@ApiModel(value = "MqMessageHistory", description = "消息历史po类")
public class MqMessageHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     */
    @ApiModelProperty(value = "消息id")
    private String id;

    /**
     * 消息类型代码
     */
    @ApiModelProperty(value = "消息类型代码")
    private String messageType;

    /**
     * 关联业务信息
     */
    @ApiModelProperty(value = "关联业务信息")
    private String businessKey1;

    /**
     * 关联业务信息
     */
    @ApiModelProperty(value = "关联业务信息")
    private String businessKey2;

    /**
     * 关联业务信息
     */
    @ApiModelProperty(value = "关联业务信息")
    private String businessKey3;

    /**
     * 消息队列主机
     */
    @ApiModelProperty(value = "消息队列主机")
    private String mqHost;

    /**
     * 消息队列端口
     */
    @ApiModelProperty(value = "消息队列端口")
    private Integer mqPort;

    /**
     * 消息队列虚拟主机
     */
    @ApiModelProperty(value = "消息队列虚拟主机")
    private String mqVirtualhost;

    /**
     * 队列名称
     */
    @ApiModelProperty(value = "队列名称")
    private String mqQueue;

    /**
     * 通知次数
     */
    @ApiModelProperty(value = "通知次数")
    private Integer informNum;

    /**
     * 处理状态，0:初始，1:成功，2:失败
     */
    @ApiModelProperty(value = "处理状态，0:初始，1:成功，2:失败")
    private Integer state;

    /**
     * 回复失败时间
     */
    @ApiModelProperty(value = "回复失败时间")
    private LocalDateTime returnfailureDate;

    /**
     * 回复成功时间
     */
    @ApiModelProperty(value = "回复成功时间")
    private LocalDateTime returnsuccessDate;

    /**
     * 回复失败内容
     */
    @ApiModelProperty(value = "回复失败内容")
    private String returnfailureMsg;

    /**
     * 最近通知时间
     */
    @ApiModelProperty(value = "最近通知时间")
    private LocalDateTime informDate;


}
