package com.shenghesun.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

 /** 
 * @ClassName: HttpClientService 
 * @Description: 调用http,https接口公共类
 * @author: yangzp
 * @date: 2018年7月4日 上午11:55:45  
 */
public class HttpClientService {
	
	
	private static PoolingHttpClientConnectionManager clientConnectionManager=null;
    private static CloseableHttpClient httpClient=null;
    
  //设置超时时间
    private static final int REQUEST_TIMEOUT = 5 * 1000;
    private static final int REQUEST_SOCKET_TIME = 5 * 1000;
    private static final int CONNECTION_REQUEST_TIME = 5 * 1000;
    
    private static RequestConfig config = RequestConfig.custom().
    		setCookieSpec(CookieSpecs.STANDARD_STRICT)
    		.setSocketTimeout(REQUEST_SOCKET_TIME)
    		.setConnectTimeout(REQUEST_TIMEOUT)
            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIME)
    		.build();
    
    private final static Object syncLock = new Object();
    
	static {
    	init();
	}
    
    /**
     * 创建httpclient连接池并初始化
     */
     //@PostConstruct
    private static void init(){

        try {
            //添加对https的支持，该sslContext没有加载客户端证书
           // 如果需要加载客户端证书，请使用如下sslContext,其中KEYSTORE_FILE和KEYSTORE_PASSWORD分别是你的证书路径和证书密码
            //KeyStore keyStore  =  KeyStore.getInstance(KeyStore.getDefaultType()
            //FileInputStream instream =   new FileInputStream(new File(KEYSTORE_FILE));
            //keyStore.load(instream, KEYSTORE_PASSWORD.toCharArray());
            //SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore,KEYSTORE_PASSWORD.toCharArray())
                   // .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    //.build();
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy() {
                    	@Override
                    	public boolean isTrusted(
                                final X509Certificate[] chain, final String authType) throws CertificateException {
                            return true;
                        }
                    })
                    .build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .build();
            clientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            clientConnectionManager.setMaxTotal(300);
            clientConnectionManager.setDefaultMaxPerRoute(50);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getHttpClient(){
        if(httpClient == null){
            synchronized (syncLock){
                if(httpClient == null){
                    CookieStore cookieStore = new BasicCookieStore();
                    BasicClientCookie cookie = new BasicClientCookie("sessionID", "######");
                    cookie.setDomain("#####");
                    cookie.setPath("/");
                    cookieStore.addCookie(cookie);
                    httpClient =HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultCookieStore(cookieStore).setDefaultRequestConfig(config).build();
                }
            }
        }
        return httpClient;
    }
    
    /**
     * 返回新的CloseableHttpClient
     * @Title: getNewHttpClient 
     * @Description: TODO 
     * @return  CloseableHttpClient 
     * @author yangzp
     * @date 2018年8月10日下午6:04:21
     **/ 
    public static CloseableHttpClient getNewHttpClient(){
        synchronized (syncLock){
                CookieStore cookieStore = new BasicCookieStore();
                BasicClientCookie cookie = new BasicClientCookie("sessionID", "######");
                cookie.setDomain("#####");
                cookie.setPath("/");
                cookieStore.addCookie(cookie);
                httpClient =HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultCookieStore(cookieStore).setDefaultRequestConfig(config).build();
            }
        return httpClient;
    }
    /**
     * get请求 无参数
     * @param url
     * @param headers
     * @return
     */
    public static String httpGet(String url, Map<String,String> headers){
        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.copy(config)
//        	    .setProxy(new HttpHost("https://trade.chinahetong.com/api/getGreaseInfos", 8080))
        	    .build();
        httpGet.setConfig(requestConfig);
        if(headers!=null&&!headers.isEmpty()){
        	for(Map.Entry<String, String> entry : headers.entrySet()) {
        		httpGet.addHeader(entry.getKey(), entry.getValue());
    		}
        }
        CloseableHttpResponse response = null;
        try{
            response =httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if ((status >= 200) && (status < 300)) {
				HttpEntity entity = response.getEntity();
				//System.out.println("=========="+EntityUtils.toString(response.getEntity()));
				return entity != null ? EntityUtils.toString(entity) : null;
			}
            //System.out.println("=====++++++++++++++++====="+EntityUtils.toString(response.getEntity()));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }
    
    
    /**
     * get请求 有参数
     * @author zhanping.yang
     * @version 创建时间：2018年1月25日  上午11:49:27
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static String readContentFromGet(String url, Map<String,String> params, Map<String,String> headers){
        CloseableHttpClient httpClient = getHttpClient();
        if(params!=null) {
        	url = url + "?"+parsParams(params);
		}
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.copy(config)
//        	    .setProxy(new HttpHost("https://trade.chinahetong.com/api/getGreaseInfos", 8080))
        	    .build();
        httpGet.setConfig(requestConfig);
        if(headers!=null&&!headers.isEmpty()){
        	for(Map.Entry<String, String> entry : headers.entrySet()) {
        		httpGet.addHeader(entry.getKey(), entry.getValue());
    		}
        }
        CloseableHttpResponse response = null;
        try{
            response =httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if ((status >= 200) && (status < 300)) {
				HttpEntity entity = response.getEntity();
				//System.out.println("=========="+EntityUtils.toString(response.getEntity()));
				return entity != null ? EntityUtils.toString(entity) : null;
			}
            //System.out.println("=====++++++++++++++++====="+EntityUtils.toString(response.getEntity()));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * post请求,使用json格式传参
     * @param url
     * @param headers
     * @param data
     * @return
     */
    public static String httpPost(String url,Map<String,Object> headers,String data){
        CloseableHttpClient httpClient = getHttpClient();
        HttpRequest request = new HttpPost(url);
        if(headers!=null&&!headers.isEmpty()){
            request = setHeaders(headers,request);
        }
        CloseableHttpResponse response = null;

        try {
            HttpPost httpPost = (HttpPost) request;
            httpPost.setEntity(new StringEntity(data, ContentType.create("application/json", "UTF-8")));
            response=httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if ((status >= 200) && (status < 300)) {
				HttpEntity entity = response.getEntity();
				//System.out.println("=========="+EntityUtils.toString(response.getEntity()));
				return entity != null ? EntityUtils.toString(entity) : null;
			}
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    
    public static void main(String[] args) {
		String url = "https://dev-openapi.dmhmusic.com/OPENAPI/openApiLogin.json";
		String data = "{\"q_source\"=\"DKmGQMKBFdaAK\"}";
		
		String result = httpPost(url,null,data);
		System.out.println("===="+result);
		NameValue nv = new NameValue();
		nv.setName("q_source");
		nv.setValue("DKmGQMKBFdaAK");
		List<NameValuePair> datas = new ArrayList<>();
		datas.add(nv);
		String result2 = postForm(url,null,datas);
		System.out.println("---------"+result2);
		
	}
    
    /**
     * 获取响应头信息
     * @Title: httpPostHeaders 
     * @Description: TODO 
     * @param url
     * @param headers
     * @param data
     * @return String
     * @author yangzp
     * @date 2018年8月1日下午2:06:26
     **/ 
    public static String httpPostHeaders(String url,Map<String,Object> headers,String data){
        CloseableHttpClient httpClient = getHttpClient();
        HttpRequest request = new HttpPost(url);
        if(headers!=null&&!headers.isEmpty()){
            request = setHeaders(headers,request);
        }
        CloseableHttpResponse response = null;

        try {
            HttpPost httpPost = (HttpPost) request;
            httpPost.setEntity(new StringEntity(data, ContentType.create("application/json", "UTF-8")));
            response=httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if ((status >= 200) && (status < 300)) {
            	Header[] rheaders = response.getAllHeaders();
				//System.out.println("=========="+EntityUtils.toString(response.getEntity()));
				if(rheaders!=null && rheaders.length>0) {
					Map<String, String> rheaderMap = new HashMap<>();
					for(Header h:rheaders) {
						rheaderMap.put(h.getName(), h.getValue());
					}
					return JSONObject.toJSONString(rheaderMap);
				}
			}
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    
    /**
     * 重新设置cookie
     * @Title: getHttpClientDMH 
     * @Description: TODO 
     * @param cookieStore
     * @return  CloseableHttpClient 
     * @author yangzp
     * @date 2018年8月10日下午4:18:05
     **/ 
    public static CloseableHttpClient getHttpClientDMH(CookieStore cookieStore){
            synchronized (syncLock){
            	httpClient =HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultCookieStore(cookieStore).setDefaultRequestConfig(config).build();
            }
        return httpClient;
    }
    
    /**
    使用表单键值对传参
    */
    public static String postForm(String url,CookieStore cookieStore,List<NameValuePair> data){
    	
        CloseableHttpClient httpClient = null;
        if(cookieStore != null) {
        	httpClient = getHttpClientDMH(cookieStore);
    	}else {
    		httpClient = getHttpClient();
    	}
        HttpRequest request = new HttpPost(url);
        CloseableHttpResponse response = null;
        UrlEncodedFormEntity uefEntity;
        try {
            HttpPost httpPost = (HttpPost) request;
            uefEntity = new UrlEncodedFormEntity(data,"UTF-8");
            httpPost.setEntity(uefEntity);
           // httpPost.setEntity(new StringEntity(data, ContentType.create("application/json", "UTF-8")));
            response=httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if ((status >= 200) && (status < 300)) {
				HttpEntity entity = response.getEntity();
				//System.out.println("=========="+EntityUtils.toString(response.getEntity()));
				return entity != null ? EntityUtils.toString(entity) : null;
			}
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    /**
     * 设置请求头信息
     * @param headers
     * @param request
     * @return
     */
    
    private static HttpRequest setHeaders(Map<String,Object> headers, HttpRequest request) {
        for (Map.Entry<String,Object> entry : headers.entrySet()) {
            if (!entry.getKey().equals("Cookie")) {
                request.addHeader((String) entry.getKey(), (String) entry.getValue());
            } else {
                @SuppressWarnings("unchecked")
				Map<String, Object> Cookies = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String,Object> entry1 : Cookies.entrySet()) {
                    request.addHeader(new BasicHeader("Cookie", (String) entry1.getValue()));
                }
            }
        }
        return request;
    }

    public static Map<String,String> getCookie(String url){
        CloseableHttpClient httpClient = getHttpClient();
        HttpRequest httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try{
            response =httpClient.execute((HttpGet)httpGet);
            Header[] headers = response.getAllHeaders();
            Map<String,String> cookies=new HashMap<String, String>();
            for(Header header:headers){
                cookies.put(header.getName(),header.getValue());
            }
            return cookies;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    /** 
     * 把Map按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
	private static String parsParams(Map<String, String> params) {
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first) {
				first = false;
			} else {
				buffer.append("&");
			}
			try {
				buffer.append(entry.getKey()+"="+URLEncoder.encode(entry.getValue(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return buffer.toString();
	}

}
