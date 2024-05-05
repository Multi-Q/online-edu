package com.xuecheng.base.exception;

import java.io.Serializable;

/**
 * @author QRH
 * @date 2023/6/19 23:48
 * @description 和前端约定返回异常信息 错误响应参数包装
 */
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
