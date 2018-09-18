package com.shenghesun.choir.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.common.BaseResponse;
import com.shenghesun.dmh.service.DMHService;

 /**
  * 选歌
  * @ClassName: ChoosemusiceController 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月20日 下午2:55:05  
  */
@RestController
@RequestMapping("/choosemusice")
public class ChoosemusiceController {
	@Autowired
    private DMHService dmhService;
	
	/**
	 * 选歌页面歌曲
	 * @Title: getMusice 
	 * @Description: TODO 
	 * @return  Object 
	 * @author yangzp
	 * @date 2018年8月20日下午2:56:50
	 **/ 
	@RequestMapping(value = "/get_musice")
	@ResponseBody
	public Object getMusice() {
		BaseResponse response = new BaseResponse();
		//Girls & Boys (Originally Performed by Good Charlotte) [Karaoke Version],Ring Ring Ring
		//String result = dmhService.searchInSearch(SignatureConstants.SHOW_SONGS, 1,20);
		//String result = dmhService.searchInSearch("Girls & Boys (Originally Performed by Good Charlotte) [Karaoke Version],Ring Ring Ring", 1,20);
		//String result = dmhService.trackInfo("T10022844688");
		List<JSONObject> arry = new ArrayList<>();
//		if(StringUtils.isNotEmpty(result)) {
//			JSONObject json = JSONObject.parseObject(result);
//			JSONObject dataNode = json.getJSONObject("data");
//			JSONArray resultArry = dataNode.getJSONArray("result");
//			if(resultArry.size()>0) {
//				for(int i=0; i < resultArry.size(); i++) {
//					JSONObject jo = resultArry.getJSONObject(i); 
//					String assetId = jo.getString("assetId");
//					if(StringUtils.isNotEmpty(assetId)) {
//						String track = dmhService.trackLink(assetId, 128);
//						if(StringUtils.isNotEmpty(track)) {
//							JSONObject trackJo = JSONObject.parseObject(track);
//							//JSONArray trackArry = trackJo.getJSONArray("data");
//							arry.add(trackJo.getJSONObject("data"));
//						}
//					}
//				}
//			}
//			response.setData(arry);
//			return response;
//		}
		//测试环境
		String [] tsids = {"T10022844688"};//String track = dmhService.trackLink("T10022844688", 128);
		//正式环境 葫芦娃2018(大张伟)  T10044414550 {"T10038980139","T10046189247","T10038887855","T10038887746","T10032761329","T10038944134"}
		//String [] tsids = {"T10038980139","T10046189247","T10038887855","T10038887746","T10032761329","T10038944134"};
		for(int i=0;i<tsids.length;i++) {
			String track = dmhService.trackLink(tsids[i], 128);
			if(StringUtils.isNotEmpty(track)) {
				JSONObject trackJo = JSONObject.parseObject(track);
				//JSONArray trackArry = trackJo.getJSONArray("data");
				arry.add(trackJo.getJSONObject("data"));
			}
		}
		
		if(!CollectionUtils.isEmpty(arry)) {
			response.setData(arry);
			return response;
		}
		
		response.setErrorCode(-1);
		return response;
	}
}
