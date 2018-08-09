package com.shenghesun.common;

import cn.binarywang.wx.miniapp.util.json.WxMaGsonBuilder;
import lombok.Data;

//群信息
@Data
public class WxMaGroupInfo {

	private String openGId;
	private Watermark watermark;

	public static WxMaGroupInfo fromJson(String json) {
		return WxMaGsonBuilder.create().fromJson(json, WxMaGroupInfo.class);
	}

	@Data
	public static class Watermark {
		private String timestamp;
		private String appid;
	}
}
