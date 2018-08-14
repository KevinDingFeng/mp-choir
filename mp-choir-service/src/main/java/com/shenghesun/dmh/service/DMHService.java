package com.shenghesun.dmh.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.core.exception.ApiException;
import com.shenghesun.util.HttpClientService;
import com.shenghesun.util.NameValue;
import com.shenghesun.util.PropertyConfigurer;
import com.shenghesun.util.RedisUtil;
import com.shenghesun.util.Signature;
import com.shenghesun.util.dmh.DMHConstants;
import com.shenghesun.util.dmh.OpenApiLogin;
import com.shenghesun.util.dmh.OpenApiLoginModel;
import com.shenghesun.util.dmh.UrlConstants;

 /**
  * 调用太合接口service
  * @ClassName: DMHService 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月8日 下午4:44:27  
  */
@Service
public class DMHService {
	@Autowired
	private PropertyConfigurer propertyConfigurer;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private OpenApiLogin openApiLogin;
	
	/**
	 * 通过分类 ID 获取歌单列表
	 * @Title: getTrackListByCatgory 
	 * @Description: TODO 
	 * @param subCateId 分类 ID
	 * @param pageNo 第几页, 必传. 必须为大于 1 的整数
	 * @param pageSize 一页返回多少结果集, 必传, 默认为 10
	 * @return  String 
	 * @author yangzp
	 * @date 2018年8月8日下午4:07:42
	 **/ 
	public String getTrackListByCatgory(String subCateId, int pageNo, int pageSize) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(DMHConstants.ACTION, UrlConstants.TRACKLIST_GETTRACKLISTBYCATGORY);
		dataMap.put(DMHConstants.PAGE_NO, pageNo);
		dataMap.put(DMHConstants.PAGE_SIZE, pageSize);
		dataMap.put("subCateId", subCateId);
		
		return postDMHService(dataMap);
	}
	
	
	/**
	 * 查询当前已授权可使用的所有专辑列表。
	 * @Title: albumGetAll 
	 * @Description: TODO 
	 * @param pageNo 第几页, 必传. 必须为大于 1 的整数
	 * @param pageSize 一页返回多少结果集, 必传, 默认为 10
	 * @return  String 
	 * @author yangzp
	 * @date 2018年8月10日上午9:45:31
	 **/ 
	public String albumGetAll(int pageNo, int pageSize) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(DMHConstants.ACTION, UrlConstants.ALBUM_ALBUMGETALL);
		dataMap.put(DMHConstants.PAGE_NO, pageNo);
		dataMap.put(DMHConstants.PAGE_SIZE, pageSize);
		
		return postDMHService(dataMap);
	}
	
	/**
	 * 调用太合接口公共方法
	 * @Title: postDMHService 
	 * @Description: TODO 
	 * @param dataMap 接口请求参数
	 * @return  String 
	 * @author yangzp
	 * @date 2018年8月9日下午4:01:08
	 **/ 
	private String postDMHService(Map<String, Object> dataMap) {
		// key: method, value: POST, 这个是固定的, 所有都是 POST.
		dataMap.put(DMHConstants.METHOD, DMHConstants.METHOD_VALUE);
		//请求参数转换成json
		String q_source = JSONObject.toJSONString(dataMap);
		try {
			// 从redis中获取公钥/cookie
			String dmhPublicKey = redisUtil.get(DMHConstants.DMH_PUBLICKEY);
			String dmhCookie = redisUtil.get(DMHConstants.DMH_COOKIE);
			if (StringUtils.isEmpty(dmhCookie)) {// redis中无公钥/cookie或者过期
				// 重新调用登录接口, 获取新的 Cookie 和 公钥
				OpenApiLoginModel oalModel = openApiLogin.getPublicKey();
				if(oalModel != null) {
					dmhPublicKey = oalModel.getPublicKey();
					dmhCookie = oalModel.getCookie();
				}
			}
			CookieStore cookieStore = null;
			if(StringUtils.isNotEmpty(dmhCookie)) {
				//设置新的cookieStore
				cookieStore = new BasicCookieStore();
				// 新建一个Cookie
			    BasicClientCookie cookie = new BasicClientCookie("JSESSID",dmhCookie);
			    cookie.setVersion(0);
			    cookie.setDomain(propertyConfigurer.getDomain());
			    cookie.setPath("/");
			    cookieStore.addCookie(cookie);
			}
		    
			if (StringUtils.isNotEmpty(dmhPublicKey)) {
				// 使用公钥进行 RSA 加密
				String rsaResult = Signature.rsaEncrypt(q_source, dmhPublicKey, "UTF-8");
				NameValue nv = new NameValue();
				nv.setName("q");
				nv.setValue(rsaResult);
				List<NameValuePair> data = new ArrayList<>();
				data.add(nv);
				String result = HttpClientService.postForm(propertyConfigurer.getAuthUrl(), cookieStore, data);
				//System.out.println("" + result);
				return result;
			}

		} catch (ApiException e) {
			e.printStackTrace();
		}
		return null;
	}
}