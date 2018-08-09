package com.shenghesun.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.core.exception.ApiException;
import com.shenghesun.util.codec.Base64;

/** 
 * @ClassName: Signature 
 * @Description: TODO
 * @author: yangzp
 * @date: 2018年7月31日 上午11:15:19  
 */
public class Signature {
	
	/** RSA最大加密明文大小  */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** RSA最大解密密文大小  密钥长度为1024 */
    //private static final int MAX_DECRYPT_BLOCK = 128;
    /** RSA最大解密密文大小  密钥长度为2048 */
    private static final int MAX_DECRYPT_BLOCK = 256;
    
    /** 
    * @Title: rsaSign 
    * @Description: TODO 内容签名
    * @param content
    * @param privateKey
    * @param charset
    * @param signType
    * @return
    * @throws ApiException String
    * @author yangzp
    * @date 2018年7月31日上午11:15:59
    */ 
    public static String rsaSign(String content, String privateKey, String charset,
			String signType) throws ApiException {

		if (SignatureConstants.SIGN_TYPE_RSA2.equals(signType)) {

			return rsa256Sign(content, privateKey, charset);
		} else {

			throw new ApiException("Sign Type is Not Support : signType=" + signType);
		}

	}
    
    /** 
    * @Title: rsa256Sign 
    * @Description: TODO sha256WithRsa 加签
    * @param content
    * @param privateKey
    * @param charset
    * @return
    * @throws ApiException String
    * @author yangzp
    * @date 2018年7月31日上午11:48:53
    */ 
    public static String rsa256Sign(String content, String privateKey, String charset) throws ApiException {

		try {
			PrivateKey priKey = getPrivateKeyFromPKCS8(SignatureConstants.SIGN_TYPE_RSA,
					new ByteArrayInputStream(privateKey.getBytes()));

			java.security.Signature signature = java.security.Signature
					.getInstance(SignatureConstants.SIGN_SHA256RSA_ALGORITHMS);

			signature.initSign(priKey);

			if (StringUtils.isEmpty(charset)) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			byte[] signed = signature.sign();

			return new String(Base64.encodeBase64(signed));
		} catch (Exception e) {
			throw new ApiException("RSAcontent = " + content + "; charset = " + charset, e);
		}

	}
    
    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
			InputStream ins) throws Exception {
		if (ins == null || StringUtils.isEmpty(algorithm)) {
			return null;
		}

		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

		byte[] encodedKey = StreamUtil.readText(ins).getBytes();

		encodedKey = Base64.decodeBase64(encodedKey);

		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
	}
    
    
	/** 
	 * 加密并签名
	* @Title: encryptAndSign 
	* @Description: TODO 加密并签名
	* @param bizContent
	* @param publicKey
	* @param cusPrivateKey
	* @param charset 字符集，如UTF-8, GBK, GB2312
	* @param isEncrypt 是否加密，true-加密  false-不加密
	* @param isSign 是否签名，true-签名  false-不签名
	* @return 加密、签名后xml内容字符串
	* @throws ApiException String
	* @author yangzp
	* @date 2018年7月31日下午1:26:46
	*/ 
	public static String encryptAndSign(String bizContent, String publicKey, String cusPrivateKey, String charset,
			boolean isEncrypt, boolean isSign) throws ApiException {
		JSONObject jsonData = new JSONObject();
		if (StringUtils.isEmpty(charset)) {
			charset = SignatureConstants.CHARSET_GBK;
		}
		
		jsonData.put("charset", charset);
		//sb.append("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>");
		if (isEncrypt) {// 加密
			String encrypted = rsaEncrypt(bizContent, publicKey, charset);
			jsonData.put("bizContent", encrypted);
			jsonData.put("encryption_type", "RSA");
			if (isSign) {
				String sign = rsaSign(encrypted, cusPrivateKey, charset);
				jsonData.put("sign", sign);
				jsonData.put("sign_type", "RSA");
			}
		} else if (isSign) {// 不加密，但需要签名
			jsonData.put("bizContent", bizContent);
			String sign = rsaSign(bizContent, cusPrivateKey, charset);
			jsonData.put("sign", sign);
			jsonData.put("sign_type", "RSA");
		} else {// 不加密，不加签
			jsonData.put("bizContent", bizContent);
		}
		return jsonData.toString();
	}
	
	public static void main(String[] args) throws ApiException {
		String clentId = "baidulab456582";
		String content = "content={\"auth\": { \"identity\": {\"methods\": [ \"password\"], \"password\": {\"user\": { \"domain\": {\"name\": \"Default\"},\"name\": \"Yunzhixueyuan\", \"password\": \"7QPEiT6m\"}}}, \"scope\": {\"domain\": {\"id\": \"default\"}}}}";
//		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC9Yfd7ktntxn3ffxHPkYM4JQHdAv+oslGXUh1rzAjuCgPhdMDsFDKjFqbpDgac1Izefl82wGolGfHsF8AHsI9g6e1frHgZ/qheZwtK85kWE2dS9jxAkiSVGqfuWAZG5DGMMXNxyi+gqS/TXtekCLD3P7qjHfoPsVmHPw2K9qQj3wIDAQAB";
//		String cusPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL1h93uS2e3Gfd9/Ec+RgzglAd0C/6iyUZdSHWvMCO4KA+F0wOwUMqMWpukOBpzUjN5+XzbAaiUZ8ewXwAewj2Dp7V+seBn+qF5nC0rzmRYTZ1L2PECSJJUap+5YBkbkMYwxc3HKL6CpL9Ne16QIsPc/uqMd+g+xWYc/DYr2pCPfAgMBAAECgYEAmGIEnYUbH6zQSY5YSXlfaUS1zB0Gk7nv1t921u8U3FxopPep6AuUdw/Z+7qSbYzFUJ3EhSyr+/B1ba/qjwVtR357IlMYwpUwyzN/hbpjDEaJq8FXDUb/RJ9fc85/1kl+9hCk9G9mC14cFsu4sXgsv3icMRdPckiF3CpTVYYmRakCQQDcfLVF+NeqwDorE38kVZjjUYu1ex1gwYQiN3iu79Sel5JEBPtoXkcl85SY77fUdP4c9cJZpzYuznAEEyD8M5tTAkEA2+K8dWLJxIreCh3wH3BuG0EJYgO5w6yYuj3AgTpY9ocePL3iUE9Biiam9uHQib4w6LqCime1CYGxk/xVESVPxQJAQkAGh9vZkajo0YCuN1Cw+7zI6eLwVN5QwadBT+8oR+7rOBz3HckWP8UM5VdCOUheAn2lxCKGGVJWPiMxShaHmQJALBYl2xNXiQSeyECAaI1ORkCf9PryP/1XEeBXvJFKTQOCWINp4NhDN8PQLifQfA2aGM9sZPuuHJ3YUOeC1tE4UQJABz6JCx1TStTp3xIOXuV08S4g9si2jmJxUesJ7MLGIeyaAMGuIGs4F++RCGZeBKTfh4nsEG9E0Tb9p/0s93x9ZQ==";
		
		 String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApchilEYFo49w6OqieHuAYUjBrr4yPgSxI4aMh0ilQxruVi1pzCMrKupA5s2pGrssStN5SRUbxgRHoDYoJjlz4tFOUeRxt1WGjHX1GFmK1dUQOn8KMsV2ZJD0O1+fDHfXuL0/YgGf68XIp2V1dPbuJXNgSmbYVGXnKhQcgXStsVT8YYsLZbxdpXr1wNJsHQZ5JxXxGkw1WlbCyHCBJDy4pSh16Fq+nO+Li1rBxQWxogGhWB/zoU5ts8U1nGuGGIjnOvnwXcC548A3yRRWsJUQqCAX6nf6YI+dJgdzBljHgCwZbh/DIvgSDLdsdKh8pBPwh+BUBjuTfWSUFhdzApIXKwIDAQAB";
		 String cusPrivateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQClyGKURgWjj3Do6qJ4e4BhSMGuvjI+BLEjhoyHSKVDGu5WLWnMIysq6kDmzakauyxK03lJFRvGBEegNigmOXPi0U5R5HG3VYaMdfUYWYrV1RA6fwoyxXZkkPQ7X58Md9e4vT9iAZ/rxcinZXV09u4lc2BKZthUZecqFByBdK2xVPxhiwtlvF2levXA0mwdBnknFfEaTDVaVsLIcIEkPLilKHXoWr6c74uLWsHFBbGiAaFYH/OhTm2zxTWca4YYiOc6+fBdwLnjwDfJFFawlRCoIBfqd/pgj50mB3MGWMeALBluH8Mi+BIMt2x0qHykE/CH4FQGO5N9ZJQWF3MCkhcrAgMBAAECggEBAJv6Xm29N/78rbInWLfdExv1FW3wu1rIlVWkeUGrovnxM79XOQx9JldZUbkWVP22j2TTdXDCcIAtdKqTadEVCyij33mDY06oJFCxhunjDZJExeETuCeuy8aw72mRfRaBBzFktWbjunBdhX2iIxHqZoRL+pPmEbHOsuFPTMasXePSVGejCOGMElxbk+Yu6P8OZ/k5v5V4Is9NHtL1t2jgG7qRz0EJdxxU17/QdOxGgqdqfVKX0YQ783gjU4iQtQ+ROE/y8QGLdqyYIYYXQWQTVuebam2IUgklBjYMUFyEU2wtUDMxY+bfOG8KRQJUUfcfTDrv3YjZhNXWpC3F7G2fnwECgYEA+Vo41254/19baMVEEEcbHTRm8fyzvgZ88NGfi9II7m75kJ6/Klmo5YIGXNO/eagob8iKVz4AWfgJSIqFXQ+fllbgmbo98sj2io1CC6pN4uEhXy+QOYDpVQgXHwpHSGZ9+H3CQWhZQaFvY45mdZl1Z8YOt54F+iG2HHGnUNgb/4ECgYEAqjPRSy8hCsfhuvfdsYX8ANs5KbDTUvcAdbdoottfl6Hm3SNop0QglLzTJ/aDERZFRLiFGVenPa2MaBxylw35r8+2gbG+xPZMjSCdtqk1szoItvUgRuKvKBk2QQY9y7S8bgjVR5qH90QIOgDvgrYicyzof1zbulE3mEkjB+IUbKsCgYBYqYsClyZw73AqTdJI02/Br6tXKhqjW907J95KMjG4zj7w/HFO1rimethWU3iHApL2RGFkaghn9Tkf0RlgoLiHFXchUkbKuEhptZahXTn6jUB3fwsSV1MgSBMol1gHV2I9ZEIZTchG/OcFiFHISPulHZiIE6leanQ9ou/yYBV8gQKBgQCJMlPXYxojcpQG/p555yMIHa6qaJswGWMJgNqbzHH17NAIGe7WRhhtTPh5EkD8mRMlHghMMVfiY+72TVghY2aNzcW0Le81HcT/TfdTsnpTCsjTkix+d2Px0sDlEX7mob3yhLRHj81iCDkJaI+oseB7LwwK/nMwcR97ZhkS1a7ZGQKBgQC2TmFsbKOa/gkNhy0IYwwraXpJjThsEuTWZ0eMBtZXTr11Nf+fUu7Woar+v9hZZfuV8Wt6g6vVQL2cUDJZg2xZB1HatyF0/WrcOtQm1yYPb0c3T8MFli08llnR7+sfUoihOQ7A9w61YrZWkMiptZ4bg9UupYix2LwyXs9DT2ottA==";
		
		
		String charset = SignatureConstants.CHARSET_UTF8;
		
		String bizContent = content +"&clentId="+clentId+"&charset="+charset
				            +"&sign_type=RSA2&timestamp=2014-07-24 03:07:50";
		
		String rusult = encryptAndSign( bizContent,  publicKey,  cusPrivateKey,  charset,
				 true, true);
		System.out.println("rusult="+rusult);
		
		JSONObject jsonData = JSONObject.parseObject(rusult); 
		Map<String, String> params = new HashMap<>();
		params.put("content", jsonData.getString("bizContent"));
		params.put("charset", charset);
		params.put("sign", jsonData.getString("sign"));
		
		String str = checkSignAndDecrypt(params, publicKey,  cusPrivateKey, true, true);
		System.out.println("str==="+str);
		
		boolean check = rsaCheckContent(jsonData.getString("bizContent"), jsonData.getString("sign"), publicKey, charset);
		System.out.println("check="+check);
		
		String str333 = rsaDecrypt(jsonData.getString("bizContent"), cusPrivateKey, charset);
		
		System.out.println("str333="+str333);
		
//		String str =  checkSignAndDecrypt( params,  publicKey,  cusPrivateKey,
//				true, true);
//		System.out.println(str);
	}
	
	public static String rsaSign(String content, String privateKey, String charset) throws ApiException {
		try {
			PrivateKey priKey = getPrivateKeyFromPKCS8(SignatureConstants.SIGN_TYPE_RSA,
					new ByteArrayInputStream(privateKey.getBytes()));

			java.security.Signature signature = java.security.Signature.getInstance(SignatureConstants.SIGN_ALGORITHMS);

			signature.initSign(priKey);

			if (StringUtils.isEmpty(charset)) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			byte[] signed = signature.sign();

			return new String(Base64.encodeBase64(signed));
		} catch (InvalidKeySpecException ie) {
			throw new ApiException("RSA私钥格式不正确，请检查是否正确配置了PKCS8格式的私钥", ie);
		} catch (Exception e) {
			throw new ApiException("RSAcontent = " + content + "; charset = " + charset, e);
		}
	}
    
	/** 
	* @Title: checkSignAndDecrypt 
	* @Description: TODO 验签并解密
	* @param params
	* @param publicKey
	* @param cusPrivateKey
	* @param isCheckSign     是否验签
    * @param isDecrypt       是否解密
    * @return 解密后明文，验签失败则异常抛出
	* @throws ApiException String
	* @author yangzp
	* @date 2018年7月31日下午12:05:46
	*/ 
	public static String checkSignAndDecrypt(Map<String, String> params, String publicKey, String cusPrivateKey,
			boolean isCheckSign, boolean isDecrypt) throws ApiException {
		String charset = params.get("charset");
		String content = params.get("content");
		if (isCheckSign) {
			if (!rsaCheckV2(params, publicKey, charset)) {
				throw new ApiException("rsaCheck failure:rsaParams=" + params);
			}
		}

		if (isDecrypt) {
			return rsaDecrypt(content, cusPrivateKey, charset);
		}

		return content;
	}
	
    
	public static boolean rsaCheckV2(Map<String, String> params, String publicKey, String charset)
			throws ApiException {
		String sign = params.get("sign");
		String content = params.get("content");

		return rsaCheckContent(content, sign, publicKey, charset);
	}
	
	public static boolean rsaCheckContent(String content, String sign, String publicKey, String charset)
			throws ApiException {
		try {
			PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));

			java.security.Signature signature = java.security.Signature.getInstance(SignatureConstants.SIGN_ALGORITHMS);

			signature.initVerify(pubKey);

			if (StringUtils.isEmpty(charset)) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			return signature.verify(Base64.decodeBase64(sign.getBytes()));
		} catch (Exception e) {
			throw new ApiException("RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
		}
	}
	
	
    /** 
    * @Title: getSignCheckContentV2 
    * @Description: TODO 将排序后的参数与其对应值，组合成“参数=参数值”的格式，
    *                    并且把这些参数用&字符连接起来，此时生成的字符串为待签名字符串
    * @param params
    * @return String
    * @author yangzp
    * @date 2018年7月31日下午1:46:57
    */ 
    public static String getSignCheckContentV2(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        params.remove("sign");

        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }

        return content.toString();
    }
	
    
	/** 
	* @Title: rsaEncrypt 
	* @Description: TODO 公钥加密
	* @param content
	* @param publicKey
	* @param charset
	* @return
	* @throws ApiException String
	* @author yangzp
	* @date 2018年7月31日上午11:51:07
	*/ 
	public static String rsaEncrypt(String content, String publicKey, String charset) throws ApiException {
		try {
			PublicKey pubKey = getPublicKeyFromX509(SignatureConstants.SIGN_TYPE_RSA,
					new ByteArrayInputStream(publicKey.getBytes()));
			Cipher cipher = Cipher.getInstance(SignatureConstants.SIGN_TYPE_RSA);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] data = StringUtils.isEmpty(charset) ? content.getBytes() : content.getBytes(charset);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}
			byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
			out.close();

			return StringUtils.isEmpty(charset) ? new String(encryptedData) : new String(encryptedData, charset);
		} catch (Exception e) {
			throw new ApiException("EncryptContent = " + content + ",charset = " + charset, e);
		}
	}
	
	public static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

		StringWriter writer = new StringWriter();
		StreamUtil.io(new InputStreamReader(ins), writer);

		byte[] encodedKey = writer.toString().getBytes();

		encodedKey = Base64.decodeBase64(encodedKey);

		return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
	}
	
	/** 
	* @Title: rsaDecrypt 
	* @Description: TODO 私钥解密
	* @param content
	* @param privateKey
	* @param charset
	* @return
	* @throws ApiException String
	* @author yangzp
	* @date 2018年7月31日上午11:53:52
	*/ 
	public static String rsaDecrypt(String content, String privateKey, String charset) throws ApiException {
		try {
			PrivateKey priKey = getPrivateKeyFromPKCS8(SignatureConstants.SIGN_TYPE_RSA,
					new ByteArrayInputStream(privateKey.getBytes()));
			Cipher cipher = Cipher.getInstance(SignatureConstants.SIGN_TYPE_RSA);
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			byte[] encryptedData = StringUtils.isEmpty(charset) ? Base64.decodeBase64(content.getBytes())
					: Base64.decodeBase64(content.getBytes(charset));
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();

			return StringUtils.isEmpty(charset) ? new String(decryptedData) : new String(decryptedData, charset);
		} catch (Exception e) {
			throw new ApiException("EncodeContent = " + content + ",charset = " + charset, e);
		}
	}

}
