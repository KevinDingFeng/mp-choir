package com.shenghesun.util.dmh;

 /**
  * 太合接口常量
  * @ClassName: DMHConstants 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月8日 下午3:18:43  
  */
public class DMHConstants {
	/**
     * 如果 cookie 或者公钥过期, 需要重新调用登录接口, 获取新的 Cookie 和 公钥, 建议一周登录一次
     * 过期时间 单位：秒
     */
	public static final long EXPIRETIME = 60 * 60 * 24 * 7;
	
	/**
	 * 公钥存入redis中的key
	 */
	public static final String DMH_PUBLICKEY = "dmhPublicKey";
	
	/**
	 * 具体要调用的方法名的key
	 */
	public static final String ACTION = "action";
	
	/**
	 * 调用方法的key
	 */
	public static final String METHOD = "method";
	
	/**
	 * value: POST, 这个是固定的, 所有都是 POST.
	 */
	public static final String METHOD_VALUE = "POST";
	
	/**
	 * 第几页
	 */
	public static final String PAGE_NO = "pageNo";
	
	/**
	 * 一页返回多少结果集
	 */
	public static final String PAGE_SIZE = "pageSize";
}
