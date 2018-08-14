package com.shenghesun.common;

import java.io.Serializable;

import lombok.Data;

@Data
public class BaseResponse implements Serializable {

	private static final long serialVersionUID = 4020096204163058662L;
	public static final int invalid_login_code = 101;// token 过期
	public static final int user_check_failed_code = 102;
	public static final long ex = 60 * 60 * 24 * 30;//token 过期时间 单位：秒
	
	protected long serverDateTime = System.currentTimeMillis();
	//接口是否调用成功
	protected boolean success = true;
	protected int errorCode = 0;
	protected String message = "操作成功";
	protected String extraMessage;
	protected Object data;
	
}
