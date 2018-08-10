package com.shenghesun.util.dmh;

import lombok.Data;

 /**
  * 登录鉴权后的公钥和cookie
  * @ClassName: OpenApiLoginModel 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月10日 上午10:16:07  
  */
@Data
public class OpenApiLoginModel {
	private String publicKey;
	private String cookie;
}
