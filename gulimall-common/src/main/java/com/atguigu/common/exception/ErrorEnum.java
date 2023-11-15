package com.atguigu.common.exception;

public enum ErrorEnum {

    VALID_EXCEPTION(10001,"参数格式校验异常"),
    UNKNOWN_ERROR(10000,"未知异常");

    private int code;
    private String msg;

    ErrorEnum(int code , String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
