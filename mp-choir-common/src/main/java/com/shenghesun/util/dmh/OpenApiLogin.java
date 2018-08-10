package com.shenghesun.util.dmh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
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
	public OpenApiLoginModel getPublicKey() throws ApiException{
		OpenApiLoginModel oalModel = openApiLogin();
		if(oalModel != null) {
			if(StringUtils.isNotEmpty(oalModel.getPublicKey()) && StringUtils.isNotEmpty(oalModel.getCookie())) {
				//将获取到的公钥存入redis
				redisUtil.set(DMHConstants.DMH_PUBLICKEY, oalModel.getPublicKey(),DMHConstants.EXPIRETIME);
				//将获取到的Cookie存入redis
				redisUtil.set(DMHConstants.DMH_COOKIE, oalModel.getCookie(),DMHConstants.EXPIRETIME);
			}
		}
		
		return oalModel;
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
	private OpenApiLoginModel openApiLogin() throws ApiException {
		OpenApiLoginModel oalModel = new OpenApiLoginModel();
		
		NameValue nv = new NameValue();
		nv.setName("q_source");
		nv.setValue(propertyConfigurer.getApikey());
		List<NameValuePair> datas = new ArrayList<>();
		datas.add(nv);
		//获取新的httpclient
		CloseableHttpClient httpClient = HttpClientService.getNewHttpClient();
		HttpRequest request = new HttpPost(propertyConfigurer.getDmhServerUrl() + UrlConstants.OPEN_API_LOGIN);
		CloseableHttpResponse response = null;
		UrlEncodedFormEntity uefEntity;
		try {
			HttpPost httpPost = (HttpPost) request;
			uefEntity = new UrlEncodedFormEntity(datas, "UTF-8");
			httpPost.setEntity(uefEntity);
			// httpPost.setEntity(new StringEntity(data,
			// ContentType.create("application/json", "UTF-8")));
			response = httpClient.execute(httpPost);
			int status = response.getStatusLine().getStatusCode();
			if ((status >= 200) && (status < 300)) {
				HttpEntity entity = response.getEntity();
				// System.out.println("=========="+EntityUtils.toString(response.getEntity()));
				String result =  entity != null ? EntityUtils.toString(entity) : null;
				if (StringUtils.isNotEmpty(result)) {
					JSONObject jsonObj = JSONObject.parseObject(result);
					String data = jsonObj.getString("data");
					if (StringUtils.isNotEmpty(data)) {
						data = data.replaceAll("-----BEGIN PUBLIC KEY-----", "");
						data = data.replaceAll("-----END PUBLIC KEY-----", "");
					}
					oalModel.setPublicKey(data.trim());
					
					
					Header[] rheaders = response.getAllHeaders();
					//System.out.println("=========="+EntityUtils.toString(response.getEntity()));
					if(rheaders!=null && rheaders.length>0) {
						for(Header h:rheaders) {
							if("Set-Cookie".equals(h.getName())) {
								String JSESSIONID = h.getValue().substring("JSESSID=".length(),
										h.getValue().indexOf(";"));
								oalModel.setCookie(JSESSIONID);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return oalModel;
		}

		return oalModel;
	}
	
	public static void main(String[] args) {
		
		String str = "JSESSID=8378e0e1176740cf656e901618ec6d27; expires=Sat, 18-Aug-2018 06:29:46 GMT; Max-Age=691200; path=/";
		String sss = str.substring("JSESSID=".length(),str.indexOf(";"));
		System.out.println(sss);
	}
}
