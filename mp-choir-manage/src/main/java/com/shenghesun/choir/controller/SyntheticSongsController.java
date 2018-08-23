package com.shenghesun.choir.controller;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.common.BaseResponse;
import com.shenghesun.entity.Choir;
import com.shenghesun.entity.SyntheticSongs;
import com.shenghesun.service.ChoirService;
import com.shenghesun.service.SyntheticSongsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/syn_songs")
public class SyntheticSongsController {

    @Autowired
    private SyntheticSongsService syntheticSongsService;

    @Autowired
    private ChoirService choirService;

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

}
