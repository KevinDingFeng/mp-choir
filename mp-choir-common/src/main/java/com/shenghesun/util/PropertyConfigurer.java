package com.shenghesun.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

 /**
  * 读取配置文件信息公共类
  * @ClassName: PropertyConfigurer 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月8日 下午2:25:48  
  */
@Component
public class PropertyConfigurer {
	
	/**
	 * 太合音乐openapi服务地址
	 */
	@Value("${dmh.openapi.url}")
	private String dmhServerUrl;
	
	/**
	 * 为每个 OPENAPI 用户配置的独一的 APIKEY
	 */
	@Value("${dmh.apikey}")
	private String apikey;
	
	/**
	 * 太合音乐所有接口调用地址
	 */
	@Value("${dmh.auth.url}")
	private String authUrl;
	

	public String getDmhServerUrl() {
		return dmhServerUrl;
	}

	public String getApikey() {
		return apikey;
	}

	public String getAuthUrl() {
		return authUrl;
	}
}
