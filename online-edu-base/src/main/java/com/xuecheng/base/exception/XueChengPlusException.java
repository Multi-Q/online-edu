package com.xuecheng.base.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author QRH
 * @date 2023/6/19 23:49
 * @description
 */
@Getter
@Setter
public class XueChengPlusException extends RuntimeException{

    private String errMessage;

    public XueChengPlusException() {
    }

    public XueChengPlusException(String message) {
        super(message);
        this.errMessage = message;

    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public static void cast(String message){
        throw new XueChengPlusException(message);
    }
    public static void cast(CommonError error){
        throw new XueChengPlusException(error.getErrMessage());
    }

}
