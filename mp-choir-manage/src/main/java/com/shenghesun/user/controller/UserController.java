package com.shenghesun.user.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.shenghesun.common.BaseResponse;
import com.shenghesun.core.exception.ApiException;
import com.shenghesun.dmh.service.DMHService;
import com.shenghesun.entity.User;
import com.shenghesun.service.UserService;
import com.shenghesun.util.RedisUtil;
import com.shenghesun.util.dmh.OpenApiLogin;
import com.shenghesun.util.dmh.OpenApiLoginModel;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import me.chanjar.weixin.common.exception.WxErrorException;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;
	@Autowired
	private RedisUtil redisUtil;
    @Autowired
    private WxMaService wxService;
    @Autowired
    private DMHService dmhService;
    @Autowired
    private OpenApiLogin openApiLogin;
    
    @RequestMapping(value="/getPublicKey",method = RequestMethod.GET)
	@ResponseBody
	public String getPublicKey() throws ApiException {
		
		OpenApiLoginModel oal = openApiLogin.getPublicKey();
		
		return "cookie="+oal.getCookie()+"<br>"+"publicKey="+oal.getPublicKey();
	}
	
	/**
	 * 测试接口
	 * @Title: demoShowName 
	 * @Description: TODO 
	 * @param name
	 * @return  String 
	 * @author yangzp
	 * @date 2018年8月9日下午4:47:33
	 **/ 
	@RequestMapping(value="/demo/{name}/{tsid}",method = RequestMethod.GET)
	@ResponseBody
	public String demoShowName(@PathVariable String name,@PathVariable String tsid) {
		logger.debug("访问getUserByName,Name={}", name);
//		String setSpUserBizID = dmhService.setSpUserBizID(29);
//		System.out.println("setSpUserBizID="+setSpUserBizID);
		//查询当前已授权可使用的所有专辑列表。
//		String albumGetAll = dmhService.albumGetAll(1, 10);
//		System.out.println("albumGetAll="+albumGetAll);
		
		//通过此接口，可快速查询可用的所有专辑信息。
//		String getAllAlbumSap = dmhService.getAllAlbumSap(1, 2);
//		System.out.println("getAllAlbumSap="+getAllAlbumSap);
		
		//通过专辑唯一码(albumAssetCode), 获取专辑下的单曲列表。
//		String albumGetSong = dmhService.albumGetSong("P10001438232",1, 10);
//		System.out.println("albumGetSong="+albumGetSong);
		
		//通过单曲的TSID(又名assetId)，查询单曲的详细信息。
		String trackInfo = dmhService.trackInfo(tsid);
//		System.out.println("trackInfo="+trackInfo);
		//通过单曲的TSID，获取单曲的播放链接
		String trackLink = dmhService.trackLink(tsid, 128);//128,320
		
//		String creatShort = dmhService.creatShort("T10036948802",1, 2);
//		System.out.println("creatShort="+creatShort);
		String searchMerge = dmhService.searchMerge(name,1, 1,20);
		System.out.println("searchMerge="+searchMerge);
		//设置当前要启用的服务 29:短音频\/短视频
		//dmhService.setSpUserBizID(24);
		String searchInSearch = dmhService.searchInSearch(name, 1,20);
		System.out.println("searchInSearch="+searchInSearch);
//		String getSpSessionBizList = dmhService.getSpSessionBizList();
//		System.out.println("getSpSessionBizList="+getSpSessionBizList);
		String result ="trackInfo"+ trackInfo +"<br><br>"+"trackLink="+trackLink+"<br><br>"+"searchMerge="+searchMerge + "<br><br>" +"searchInSearch="+searchInSearch;
		//String result = dmhService.selectShortRate("T10033153645", "150",128);
		return result;
	}
	
	@RequestMapping(value="/creatShort/{tsid}/{startOffset}/{duration}",method = RequestMethod.GET)
	@ResponseBody
	public String creatShort(@PathVariable String tsid,@PathVariable String startOffset,@PathVariable String duration) {
		logger.debug("访问getUserByName,Name={}", tsid);
		
		String creatShort = dmhService.creatShort(tsid,Float.parseFloat(startOffset), Float.parseFloat(duration));
		System.out.println("creatShort="+creatShort);
		String result ="creatShort="+ creatShort +"<br><br>";
		//String result = dmhService.selectShortRate("T10033153645", "150",128);
		return result;
	}
	
	/**
	 * 登陆接口
	 * 
	 * @throws WxErrorException
	 */
	@GetMapping("login")
	@ResponseBody
	public Object login(String code, String signature, String rawData, String encryptedData, String iv)
			throws WxErrorException {
		
		BaseResponse response = new BaseResponse();
		WxMaJscode2SessionResult session = this.wxService.getUserService().getSessionInfo(code);
		String sessionKey = session.getSessionKey();
		//通过openId sessionKey 生成3rd session 返回给客户端小程序
		String accessToken = UUID.randomUUID().toString();
		redisUtil.set(accessToken, sessionKey + ":" + session.getOpenid(), BaseResponse.ex);
		// 用户信息校验
        if (!this.wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
        	response.setErrorCode(BaseResponse.user_check_failed_code);
        	response.setMessage("用户信息校验失败");
            return response;
        }
        // 解密用户信息
        WxMaUserInfo userInfo = this.wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        //往mysql 中插入user 信息 插入前判断是否已经存在， 存在则进行更新
        User user = userService.findByOpenId(userInfo.getOpenId());
        if(user == null) {
        	user = new User();
            BeanUtils.copyProperties(userInfo, user);
            User dbUser = userService.save(user);
            data.put("userId", dbUser.getId());
        }else {
        	if(StringUtils.isEmpty(user.getAvatarUrl())) {
        		BeanUtils.copyProperties(userInfo, user);
        		if(hasEmoji(userInfo.getNickName())) {
        			user.setNickName(null);
        		}
                userService.save(user);
        	}
        	data.put("userId", user.getId());
        }
        
        response.setData(data);
		return response;
	}
	
	/**
	 * 无授权登陆
	 * @Title: unauthorizedLogin 
	 * @Description: TODO 
	 * @param code
	 * @return
	 * @throws WxErrorException  Object 
	 * @author yangzp
	 * @date 2018年9月12日下午6:20:43
	 **/ 
	@GetMapping("unauthorizedLogin")
	@ResponseBody
	public Object unauthorizedLogin(String code) throws WxErrorException {
		
		BaseResponse response = new BaseResponse();
		WxMaJscode2SessionResult session = this.wxService.getUserService().getSessionInfo(code);
		//String sessionKey = session.getSessionKey();
		//通过openId sessionKey 生成3rd session 返回给客户端小程序
		String accessToken = UUID.randomUUID().toString();
		//redisUtil.set(accessToken, sessionKey + ":" + session.getOpenid(), BaseResponse.ex);
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        //往mysql 中插入user 信息 插入前判断是否已经存在， 存在则进行更新
        User user = userService.findByOpenId(session.getOpenid());
        if(user == null) {
        	user = new User();
        	user.setOpenId(session.getOpenid());
            User dbUser = userService.save(user);
            data.put("userId", dbUser.getId());
        }else {
        	data.put("userId", user.getId());
        }
        
        response.setData(data);
		return response;
	}
	
	
	@GetMapping("info")
	@ResponseBody
	public Object info(String token) {
		BaseResponse response = new BaseResponse();
		User user = userService.findByOpenId(getOpenId(token));
		Map<String, Object> data = new HashMap<>();
		data.put("myInfo", user);
		response.setData(data);
		return response;
	}
	
	
	@GetMapping("checkToken")
	@ResponseBody
	public Object checkToken(String token){
		BaseResponse response = new BaseResponse();
		//TODO token 没过期 并且有对应的用户
		if(!redisUtil.exists(token)) {
			response.setErrorCode(BaseResponse.invalid_login_code);
			response.setMessage("invalid_login");
			return response;
		}
		User user = userService.findByOpenId(getOpenId(token));
		if(user == null) {
			response.setErrorCode(BaseResponse.invalid_login_code);
			response.setMessage("invalid_login");
			return response;
		}
		Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        response.setData(data);
		return response;
	}
	
	
	
	//设置用户基本信息
	@GetMapping("setupUserInfo")
	@ResponseBody
	public Object setUpUserInfo(String token, String mpGender, String constellation, String age) {
		
		BaseResponse response = new BaseResponse();
		User user = userService.findByOpenId(getOpenId(token));
		user.setMpGender(Integer.valueOf(mpGender));
		user.setAge(Integer.valueOf(age));
		userService.save(user);
		
		return response;
	}
	
	
	
	public String getSessionKey(String token) {
		String tokenValue = redisUtil.get(token);
		if(tokenValue.startsWith("\"")) {
			return tokenValue.substring(1, tokenValue.length()-1).split(":")[0];
		}
		return tokenValue.split(":")[0];
	}
	
	public String getOpenId(String token) {
		String tokenValue = redisUtil.get(token);
		if(tokenValue.startsWith("\"")) {
			return tokenValue.substring(1, tokenValue.length()-1).split(":")[1];
		}
		return tokenValue.split(":")[1];
	}
	
	private boolean hasEmoji(String content){

	    Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]");
	    Matcher matcher = pattern.matcher(content);
	    if(matcher .find()){
	        return true;    
	    }
	        return false;
	}
}
