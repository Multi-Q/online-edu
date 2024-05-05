package com.xuecheng.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 数据字典
 * </p>
 *
 * @author QRH
 */
@Data
@TableName("dictionary")
@ApiModel(value = "Dictionary", description = "数据字典po类")
public class Dictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id标识
     */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据字典名称
     */
    @ApiModelProperty(value = "数据字典名称")
    private String name;

    /**
     * 数据字典代码
     */
    @ApiModelProperty(value = "数据字典代码")
    private String code;

    /**
     * 数据字典项--json格式
     * [{
     * "sd_name": "低级",
     * "sd_id": "200001",
     * "sd_status": "1"
     * }, {
     * "sd_name": "中级",
     * "sd_id": "200002",
     * "sd_status": "1"
     * }, {
     * "sd_name": "高级",
     * "sd_id": "200003",
     * "sd_status": "1"
     * }]
     */
    @ApiModelProperty(value = "数据字典项--json格式")
    private String itemValues;


}
