package com.atguigu.common.exception;

import lombok.Data;

@Data
public class GulimallException extends RuntimeException{
	private String msg;
	private int code = 500;

	public GulimallException(ErrorEnum errorEnum) {
		super(errorEnum.getMsg());
		this.msg = errorEnum.getMsg();
		this.code=errorEnum.getCode();
	}


}
