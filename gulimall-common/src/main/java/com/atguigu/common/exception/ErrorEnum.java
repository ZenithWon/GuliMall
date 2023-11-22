package com.atguigu.common.exception;

public enum ErrorEnum {
    UNKNOWN_ERROR(10000,"未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验异常"),
    ILLEGAL_REQUEST(10002,"请求不合法"),
    UPLOAD_FIGURE_FAILED(10003,"上传图片失败"),
    DATABASE_ERROR(10010,"数据库异常"),
    DATABASE_DUPLICATE_ERROR(10011,"该数据不可重复"),
    DATABASE_INSERT_ERROR(10012,"数据库异常，新增失败"),
    DATABASE_UPDATE_ERROR(10013,"数据库异常，修改失败"),
    DATABASE_DELETE_ERROR(10014,"数据库异常，删除失败"),
    PURCHASE_MERGE_ERROR(10021,"采购需求无法合并"),
    PRODUCT_PUBLISH_ERROR(10022,"商品上架失败"),
    ELASTICSEARCH_SAVE_ERROR(10051,"搜索服务保存失败"),
    ;

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
