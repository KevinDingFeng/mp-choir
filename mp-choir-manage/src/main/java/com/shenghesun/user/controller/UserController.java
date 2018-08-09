package com.shenghesun.user.controller;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import com.shenghesun.common.WxMaGroupInfo;
import com.shenghesun.dmh.service.DMHService;
import com.shenghesun.entity.Group;
import com.shenghesun.entity.User;
import com.shenghesun.service.GroupService;
import com.shenghesun.service.UserService;
import com.shenghesun.util.RedisUtil;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;
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
	private GroupService groupService;
    @Autowired
    private WxMaService wxService;
    @Autowired
    private DMHService dmhService;
	
	/**
	 * 测试接口
	 * @Title: demoShowName 
	 * @Description: TODO 
	 * @param name
	 * @return  String 
	 * @author yangzp
	 * @date 2018年8月9日下午4:47:33
	 **/ 
	@RequestMapping(value="/demo/{name}",method = RequestMethod.GET)
	@ResponseBody
	public String demoShowName(@PathVariable String name) {
		logger.debug("访问getUserByName,Name={}", name);
		String result = dmhService.getTrackListByCatgory("1001", 1, 20);
		System.out.println("token====="+result);
		return "name is  " + result;
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
        //往mysql 中插入user 信息 插入前判断是否已经存在， 存在则进行更新
        if(userService.findByOpenId(userInfo.getOpenId()) == null) {
        	User user = new User();
            BeanUtils.copyProperties(userInfo, user);
            userService.save(user);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
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
		
		return response;
	}
	
	@GetMapping("setupGroup")
	@ResponseBody
	//TODO 添加标记是否从分享页进入
	public Object setupGroup(boolean isShare, String token, String encryptedData, String iv){
		BaseResponse response = new BaseResponse();
		//获取sessionkey
		WxMaGroupInfo groupInfo = WxMaGroupInfo.fromJson(WxMaCryptUtils.decrypt(getSessionKey(token), encryptedData, iv));
		User user = userService.findByOpenId(getOpenId(token));
		//添加群信息 判断该群是否已存在
		Group group;
		if(groupService.findByGId(groupInfo.getOpenGId()) == null) {
			group = new Group();
			group.setUsers(new HashSet<User>());
			group.setOpenGid(groupInfo.getOpenGId());
			groupService.save(group);
		}else {
			group = groupService.findByGId(groupInfo.getOpenGId());
		}
		
		group.getUsers().add(user);
		groupService.save(group);
		
		
		Map<String, Object> data = new HashMap<>();
		//TODO 如果当前用户是从群分享过来 1. 群中已经有其他人做过测试，则统计数据返回排行榜页 
		//2. 如果群中有其他人，但是没有做测试则提示，群中好友正在测试 3. 群中只有自己则进入自己测试结果页
		if(isShare) {
			data.put("openGid", group.getOpenGid());
			data.put("myInfo", user);
//			data.put("friendInfo", arg1)
			
		}
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
		user.setConstellation(Integer.valueOf(constellation));
		user.setAge(Integer.valueOf(age));
		userService.save(user);
		
		return response;
	}
	
	//群中有人已经做过测评 则返回我和好友的默契
	@GetMapping("getStatisticsResult")
	@ResponseBody
	public Object getStatisticsResult(String token, String openGid) {
		BaseResponse response = new BaseResponse();
		// 我的信息
		User user = userService.findByOpenId(getOpenId(token));

		Group group = groupService.findByGId(openGid);
		Set<User> users = group.getUsers();
		users.remove(user);

		for (Iterator<User> it = users.iterator(); it.hasNext();) {
			User u = it.next();
			if (u.getTotalPoints() == 0) {
				it.remove();
				continue;
			}
			float percent;
			if (u.getTotalPoints() >= user.getTotalPoints()) {
				percent = (float) user.getTotalPoints() / u.getTotalPoints();
			} else {
				percent = (float) u.getTotalPoints() / user.getTotalPoints();
			}
			NumberFormat nt = NumberFormat.getIntegerInstance();
			u.setMatchingRate(Integer.valueOf(nt.format(percent*100)));
		}
		
		List<User> userL = new ArrayList<>(users);
		//根据MatchingRate 排序users
		Collections.sort(userL, new Comparator<User>() {

			@Override
			public int compare(User u1, User u2) {
				return (u2.getMatchingRate() - u1.getMatchingRate());
			}
		});

		Map<String, Object> userData = new HashMap<>();
		userData.put("myInfo", user);
		userData.put("friendInfo", userL);
		response.setData(userData);
		return response;
	}
	
	//设置用户总得分 答案记录
	@GetMapping("setRecord")
	@ResponseBody
	public Object setRecord(String token, String totalPoints, String record) {
		BaseResponse response = new BaseResponse();
		User user = userService.findByOpenId(getOpenId(token));
		user.setTotalPoints(Integer.valueOf(totalPoints));
		user.setRecord(record);
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
	
}
