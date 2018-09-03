package com.shenghesun.choir.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.common.BaseResponse;
import com.shenghesun.dmh.service.DMHService;
import com.shenghesun.entity.Choir;
import com.shenghesun.entity.SongSection;
import com.shenghesun.entity.SplitSongDuration;
import com.shenghesun.service.ChoirService;
import com.shenghesun.service.SongSectionService;
import com.shenghesun.service.SplitSongDurationService;

 /**
  * 歌曲分段
  * @ClassName: SplitSongController 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月21日 下午2:21:13  
  */
@RestController
@RequestMapping("/split")
public class SplitSongController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private SongSectionService songSectionService;
	
	@Autowired
	private ChoirService choirService;
	
	@Autowired
	private SplitSongDurationService splitSongDurationService;
	
	@Autowired
    private DMHService dmhService;
	
	/**
	 * 给原唱分段
	 * @Title: splitSong 
	 * @Description: TODO 
	 * @param choir
	 * @return  Object 
	 * @author yangzp
	 * @date 2018年8月21日下午6:10:11
	 **/ 
	@RequestMapping(value = "/splitSong")
	@ResponseBody
	public Object splitSong(Choir choir) {
		BaseResponse response = new BaseResponse();
		try {
			//修改团：歌名，歌手
			Choir ctemp = choirService.getForUpdate(choir.getId());
			ctemp.setSinger(choir.getSinger());
			ctemp.setSongName(choir.getSongName());
			choirService.save(ctemp);
			//获取以前分段数据并删除
			List<SongSection> songSections = songSectionService.findByChoirId(choir.getId());
			if(!CollectionUtils.isEmpty(songSections)) {
				songSectionService.delete(songSections);
			}
			//根据歌名获取分段时间信息
			//List<SplitSongDuration> ssdList = splitSongDurationService.findBySongName(ctemp.getSongName(), ctemp.getPopulation());
			List<SplitSongDuration> ssdList = splitSongDurationService.findBySongName("Ring Ring Ring", 3);
			if(!CollectionUtils.isEmpty(ssdList)) {
				SplitSongDuration ssd = ssdList.get(0);
				//0(音频起始秒数),10(切割音频的长度);10,12;22,10
				String splitTimes = ssd.getSplitTimes();
				String [] timeArry = splitTimes.split(";");
				for(int i=0; i<timeArry.length; i++) {
					//每一段的起始和切割长度
					String [] splitTemeArry = timeArry[i].split(",");
					//设置当前要启用的服务 29:短音频\/短视频
					dmhService.setSpUserBizID(29);
					//创建短音频
					String result = dmhService.creatShort(choir.getTSID(),Float.parseFloat(splitTemeArry[0]), Float.parseFloat(splitTemeArry[1]));
					JSONObject json = JSONObject.parseObject(result);
					String resourceId = json.getJSONObject("data").getString("resource_id");
					SongSection songSection = new SongSection();
					songSection.setChoir(ctemp);
					songSection.setResourceId(resourceId);
					songSection.setTsID(choir.getTSID());
					songSection.setSort(i);
					songSectionService.save(songSection);
				}
			}
			
		} catch (Exception e) {
			logger.error("Exception {} in {} " , e.getMessage() , "splitSong");
			response.setSuccess(false);
			return response;
		}
		return response;
	}
}
