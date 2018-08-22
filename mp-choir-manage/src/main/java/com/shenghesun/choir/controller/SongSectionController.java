package com.shenghesun.choir.controller;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.common.BaseResponse;
import com.shenghesun.dmh.service.DMHService;
import com.shenghesun.entity.SongSection;
import com.shenghesun.service.SongSectionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/song_section")
public class SongSectionController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SongSectionService songSectionService;
    
    @Autowired
    private DMHService dmhService;

    @RequestMapping("/my_song_section")
    public BaseResponse mySongSection() {
        BaseResponse response = new BaseResponse();
        try {
            List<SongSection> mySections = songSectionService.findMySection();
            response.setData(mySections);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setErrorCode(400);
            response.setMessage("操作失败");
            response.setExtraMessage(e.getMessage());
        } finally {
            return response;
        }
    }
    
    /**
     * 根据成团id获取分段歌曲，用于点唱
     * @Title: getSectionSong 
     * @Description: TODO 
     * @param choirId
     * @return  BaseResponse 
     * @author yangzp
     * @date 2018年8月21日下午5:08:35
     **/ 
    @RequestMapping("/get_section_song")
    public BaseResponse getSectionSong(Long choirId) {
    	BaseResponse response = new BaseResponse();
    	try {
			List<SongSection> ssList = songSectionService.findByChoirId(choirId);
			if(!CollectionUtils.isEmpty(ssList)) {
				for(SongSection ss:ssList) {
					//给短音频赋值播放链接
					setSplitShortRate(ss);
				}
			}
			response.setData(ssList);
		} catch (Exception e) {
			logger.error("Exception {} in {} " , e.getMessage() , "getSectionSong");
			response.setSuccess(false);
			return response;
		}
    	return response;
    }
    
    /**
     * 通过资源id(resource id)和TSID获取短音频信息。
     * 并给赋值
     * @Title: setSplitShortRate 
     * @Description: TODO 
     * @param songSection  void 
     * @author yangzp
     * @date 2018年8月21日下午5:45:54
     **/ 
    private void setSplitShortRate(SongSection songSection) {
    	String result = dmhService.selectShortRate(songSection.getTsID(), songSection.getResourceId(),128);
    	JSONObject jsonObj = JSONObject.parseObject(result);
    	if(jsonObj.getBooleanValue("state")) {
    		JSONObject dataObj = jsonObj.getJSONObject("data");
    		String path = dataObj.getString("path");
        	songSection.setPath(path);
        	String duration = dataObj.getString("duration");
        	songSection.setDuration(duration);
    	}
    }

    @RequestMapping("/{sectionId}/info")
    public BaseResponse info(@PathVariable Long sectionId){
        BaseResponse response = new BaseResponse();
        try{
            SongSection songSection = songSectionService.findById(sectionId);
            response.setSuccess(true);
            response.setMessage("操作成功");
            response.setData(songSection);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }finally {
            return response;
        }

    }
}
