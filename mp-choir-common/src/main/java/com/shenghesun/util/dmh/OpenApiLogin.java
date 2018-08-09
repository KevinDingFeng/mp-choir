package com.shenghesun.util.dmh;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.core.exception.ApiException;
import com.shenghesun.util.HttpClientService;
import com.shenghesun.util.NameValue;
import com.shenghesun.util.PropertyConfigurer;
import com.shenghesun.util.RedisUtil;

 /**
  * 第一次接口调用握手登录
  * @ClassName: OpenApiLogin 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月8日 下午2:30:53  
  */
@Service
public class OpenApiLogin {
	
	@Autowired
	private PropertyConfigurer propertyConfigurer;
	
	@Autowired
	private RedisUtil redisUtil;
	
	/**
	 * 将获取到的公钥返回并存入redis
	 * @Title: getPublicKey 
	 * @Description: TODO 
	 * @return 公钥
	 * @throws ApiException  String 
	 * @author yangzp
	 * @date 2018年8月8日下午3:26:27
	 **/ 
	public String getPublicKey() throws ApiException{
		String publicKey = openApiLogin();
		if(StringUtils.isNotEmpty(publicKey)) {
			//将获取到的公钥存入redis
			redisUtil.set(DMHConstants.DMH_PUBLICKEY, publicKey,DMHConstants.EXPIRETIME);
		}
		return publicKey;
	}
	
	/**
	 * 第一次接口调用握手登录
	 * @Title: openApiLogin 
	 * @Description: TODO 
	 * @return
	 * @throws ApiException  String 
	 * @author yangzp
	 * @date 2018年8月8日下午3:09:03
	 **/ 
	private String openApiLogin() throws ApiException {
		NameValue nv = new NameValue();
		nv.setName("q_source");
		nv.setValue(propertyConfigurer.getApikey());
		List<NameValuePair> datas = new ArrayList<>();
		datas.add(nv);
		String result = HttpClientService.postForm(propertyConfigurer.getDmhServerUrl()+UrlConstants.OPEN_API_LOGIN, 
				null, datas);
		if(StringUtils.isNotEmpty(result)) {
			JSONObject jsonObj = JSONObject.parseObject(result);
			String data = jsonObj.getString("data");
			if(StringUtils.isNotEmpty(data)) {
				data = data.replaceAll("-----BEGIN PUBLIC KEY-----", "");
				data = data.replaceAll("-----END PUBLIC KEY-----", "");
			}
			return data.trim();
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		//String str = "{\"state\":false,\"errcode\":\"100101\",\"errmsg\":\"q_source为必填数据.\"}";
		String str2 = "{\"state\":true,\"errcode\":0,\"errmsg\":\"\",\"data\":\"-----BEGIN PUBLIC KEY-----\\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjMylZu3Dcy6qTYKkeQtVOkI8J\\nhAmq0HqcybMYVCB3ctz2nyLf5NBkOmZMiO+QiLZXnKCE\\/YsYfuTctcZKfSUug0Nu\\n7fyzoAm\\/08zm03H\\/xXQ7+Z6g0CBH3pcUMVl\\/fEDSVkrXGhTDLKVDLYQwG\\/m+NsTx\\nboJMO5bRgSDSV3mx+wIDAQAB\\n-----END PUBLIC KEY-----\\n\"}";
		JSONObject jsonObj = JSONObject.parseObject(str2);
		String data = jsonObj.getString("data");
		if(StringUtils.isNotEmpty(data)) {
			data = data.replaceAll("-----BEGIN PUBLIC KEY-----", "");
			data = data.replaceAll("-----END PUBLIC KEY-----", "");
		}
		System.out.println(data.trim());
	}
}
