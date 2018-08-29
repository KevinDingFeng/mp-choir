package com.shenghesun.choir.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.common.BaseResponse;
import com.shenghesun.dmh.service.DMHService;
import com.shenghesun.entity.Choir;
import com.shenghesun.entity.SongSection;
import com.shenghesun.entity.SongSection.SectionStatusEnum;
import com.shenghesun.service.SongSectionService;
import com.shenghesun.util.DateUtil;
import com.shenghesun.util.PropertyConfigurer;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/song_section")
public class SongSectionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PropertyConfigurer propertyConfigurer;

    @Autowired
    private SongSectionService songSectionService;

    @Autowired
    private DMHService dmhService;

    @RequestMapping("/my_song_section")
    public BaseResponse mySongSection(@RequestParam Long userId) {
        userId = 1L;
        BaseResponse response = new BaseResponse();
        try {
            List<SongSection> mySections = songSectionService.findMySection(userId);
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

    @RequestMapping("/my_writting")
    public BaseResponse getMyWritting(@RequestParam Long userId) {
        userId = 1L;
        BaseResponse response = new BaseResponse();
        try {
            List<Choir> mySections = songSectionService.findMyWritting(userId);
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
     * 根据成团id获取分段歌曲，发起者用于点唱
     *
     * @param choirId
     * @return BaseResponse
     * @Title: getSectionSong
     * @Description: TODO
     * @author yangzp
     * @date 2018年8月21日下午5:08:35
     **/
    @RequestMapping("/get_section_song")
    public BaseResponse getSectionSong(Long choirId) {
        BaseResponse response = new BaseResponse();
        try {
            List<SongSection> ssList = songSectionService.findByChoirId(choirId);
            Choir resultChoir = new Choir();
            if (!CollectionUtils.isEmpty(ssList)) {
                BeanUtils.copyProperties(ssList.get(0).getChoir(), resultChoir);
                for (SongSection ss : ssList) {
                    //给短音频赋值播放链接
                    setSplitShortRate(ss);
                }
                resultChoir.setSongSection(ssList);
            }
            String albumArtPath = resultChoir.getAlbumArtPaht();
            if (StringUtils.isNotEmpty(albumArtPath)) {
                resultChoir.setAlbumArtPaht(propertyConfigurer.getShowFilePath() +
                        resultChoir.getAlbumArtPaht());
            }
            //用于判断是否是发起者进入的分段页面
            response.setExtraMessage("1");
            response.setData(resultChoir);
        } catch (Exception e) {
            logger.error("Exception {} in {} ", e.getMessage(), "getSectionSong");
            response.setSuccess(false);
            return response;
        }
        return response;
    }

    /**
     * 通过资源id(resource id)和TSID获取短音频信息。
     * 并给赋值
     *
     * @param songSection void
     * @Title: setSplitShortRate
     * @Description: TODO
     * @author yangzp
     * @date 2018年8月21日下午5:45:54
     **/
    private void setSplitShortRate(SongSection songSection) {
        String result = dmhService.selectShortRate(songSection.getTsID(), songSection.getResourceId(), 128);
        JSONObject jsonObj = JSONObject.parseObject(result);
        if (jsonObj.getBooleanValue("state")) {
            JSONObject dataObj = jsonObj.getJSONObject("data");
            String path = dataObj.getString("path");
            songSection.setPath(path);
            String duration = dataObj.getString("duration");
            songSection.setDuration(duration);
            songSection.setDuration(DateUtil.formatSecond(songSection.getDuration()));
        }
    }

    @RequestMapping("/{sectionId}/info")
    public BaseResponse info(@PathVariable Long sectionId) {
        BaseResponse response = new BaseResponse();
        try {
            SongSection songSection = songSectionService.findById(sectionId);
            response.setSuccess(true);
            response.setMessage("操作成功");
            response.setData(songSection);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        } finally {
            return response;
        }

    }

    @RequestMapping(value = "/{sectionId}/upload", method = RequestMethod.POST)
    public BaseResponse audioUpload(@PathVariable Long sectionId, @RequestParam("audioFile") MultipartFile audioFile) {
        System.out.println("进入后台上传方法");
        BaseResponse response = new BaseResponse();
        try {
            if (songSectionService.uploadAudioFile(sectionId, audioFile)) {
                response.setSuccess(true);
                response.setMessage("上传成功");
                return response;
            }
            response.setErrorCode(400);
            response.setMessage("上传失败");
            response.setSuccess(false);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            response.setErrorCode(400);
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            return response;
        }
    }
    
    /**
     * 认领歌曲
     * @Title: claim 
     * @Description: TODO 
     * @param id
     * @param userId
     * @return  BaseResponse 
     * @author yangzp
     * @date 2018年8月29日上午11:54:38
     **/ 
    @RequestMapping("/claim")
    public BaseResponse claim(Long id, Long userId) {
        BaseResponse response = new BaseResponse();
        try {
            SongSection songSection = songSectionService.findById(id);
            if(songSection != null) {
            	songSection.setUserId(userId);
            	songSection.setStatus(SectionStatusEnum.NO_RECORDING);
            	songSectionService.save(songSection);
            }
        } catch (Exception e) {
            logger.error("Exception {} in {} ", e.getMessage(), "claim");
            response.setSuccess(false);
            return response;
        }
        return response;
    }
}
