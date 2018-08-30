package com.shenghesun.choir.controller;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.common.BaseResponse;
import com.shenghesun.entity.Choir;
import com.shenghesun.entity.SongSection;
import com.shenghesun.entity.SyntheticSongs;
import com.shenghesun.service.ChoirService;
import com.shenghesun.service.SongSectionService;
import com.shenghesun.service.SyntheticSongsService;
import com.shenghesun.util.FileIOUtil;
import com.shenghesun.util.PropertyConfigurer;
import com.shenghesun.util.audio.CutMusic;

@RestController
@RequestMapping("/syn_songs")
public class SyntheticSongsController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SyntheticSongsService syntheticSongsService;

    @Autowired
    private ChoirService choirService;
    
    @Autowired
    private PropertyConfigurer propertyConfigurer;

    @Autowired
    private SongSectionService songSectionService;

    @RequestMapping("/my_songs")
    public BaseResponse mySongs(@RequestParam String openId) {
        BaseResponse response = new BaseResponse();
        try {
            List<SyntheticSongs> mySyntheticSongs = syntheticSongsService.findMySyntheticSongs(openId);
            response.setData(mySyntheticSongs);
        } catch (Exception e) {
            response.setExtraMessage(e.getMessage());
            response.setErrorCode(400);
            response.setMessage("操作失败");
        } finally {
            return response;
        }
    }

    @RequestMapping("/{id}/detail")
    public BaseResponse detail(@PathVariable Long id) {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonObject = new JSONObject();
            SyntheticSongs syntheticSong = syntheticSongsService.findById(id);
            jsonObject.put("syntheticSong", syntheticSong);
            if (syntheticSong != null) {
                Choir choir = choirService.getForUpdate(syntheticSong.getChoirId());
                jsonObject.put("choir", choir);
            }
            response.setData(jsonObject);
        } catch (Exception e) {
            response.setExtraMessage(e.getMessage());
            response.setErrorCode(400);
            response.setMessage("操作失败");
        } finally {
            return response;
        }
    }

    @RequestMapping("/{choirId}/detail_by_choir")
    public BaseResponse detailChoirId(@PathVariable Long choirId) {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonObject = new JSONObject();
            SyntheticSongs syntheticSong = syntheticSongsService.findByChoirId(choirId);
            jsonObject.put("syntheticSong", syntheticSong);
            if (syntheticSong != null) {
                Choir choir = choirService.getForUpdate(choirId);
                jsonObject.put("choir", choir);
            }
            response.setData(jsonObject);
        } catch (Exception e) {
            response.setExtraMessage(e.getMessage());
            response.setErrorCode(400);
            response.setMessage("操作失败");
        } finally {
            return response;
        }
    }

    @RequestMapping("{id}/wxacode")
    public BaseResponse getWxacode(@PathVariable Long id) {
        BaseResponse response = new BaseResponse();
        try {
            String wxacodePath = syntheticSongsService.getWxacodePath(id);
            response.setSuccess(true);
            response.setData(wxacodePath);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @RequestMapping("{choirId}/wxacode_choir_id")
    public BaseResponse getWxacodeByChoirId(@PathVariable Long choirId) {
        BaseResponse response = new BaseResponse();
        try {
            String wxacodePath = syntheticSongsService.getWxacodePathByChoirId(choirId);
            response.setSuccess(true);
            response.setData(wxacodePath);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    /**
     * 合成
     * @Title: compound 
     * @Description: TODO 
     * @param choirId
     * @return  BaseResponse 
     * @author yangzp
     * @date 2018年8月29日下午4:31:03
     **/
    @RequestMapping("/compound")
    public BaseResponse compound(Long choirId) {
        BaseResponse response = new BaseResponse();
        try {
        	List<SongSection> ssList = songSectionService.findByChoirId(choirId);
        	if(!CollectionUtils.isEmpty(ssList)) {
        		//根路径
        		String basePath = propertyConfigurer.getShowFilePath();
        		//子路径
        		String subPath = FileIOUtil.generateSubPathStr();
        		//合成后的文件
        		File songFile = CutMusic.createFile(basePath + subPath,choirId+".mp3");
        		StringBuffer userIdBff = new StringBuffer(",");
        		for(SongSection ss : ssList) {
        			userIdBff.append(ss.getUserId() + ",");
        			if(StringUtils.isNotEmpty(ss.getAudioPath())) {
        				File audioFile = new File(basePath + ss.getAudioPath());
        				CutMusic.compoundTargetMp3File(songFile, audioFile);
        			}
        		}
        		
        		SyntheticSongs syntheticSongs = new SyntheticSongs();
        		syntheticSongs.setChoirId(choirId);
        		syntheticSongs.setSongPath(subPath + choirId +".mp3");
        		syntheticSongs.setUserIds(userIdBff.toString());
        		
        		syntheticSongsService.save(syntheticSongs);
        	}
        	
        } catch (Exception e) {
            logger.error("Exception {} in {} ", e.getMessage(), "claim");
            response.setSuccess(false);
            return response;
        }
        return response;
    }
}
