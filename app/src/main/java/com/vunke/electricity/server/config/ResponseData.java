package com.vunke.electricity.server.config;

/**
 * Created by zhuxi on 2019/5/14.
 */

public class ResponseData {

    /**
     * code : 200
     * data : {}
     * message : 请求成功
     */

    private int code;
    private Object data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
