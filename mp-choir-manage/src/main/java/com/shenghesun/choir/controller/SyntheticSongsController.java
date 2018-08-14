package com.shenghesun.choir.controller;

import com.shenghesun.common.BaseResponse;
import com.shenghesun.entity.SyntheticSongs;
import com.shenghesun.service.SyntheticSongsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/syn_songs")
public class SyntheticSongsController {

    @Autowired
    private SyntheticSongsService syntheticSongsService;

    @RequestMapping("/my_songs")
    public BaseResponse mySongs() {
        BaseResponse response = new BaseResponse();
        try {
            List<SyntheticSongs> mySyntheticSongs = syntheticSongsService.findMySyntheticSongs();
            response.setData(mySyntheticSongs);
        } catch (Exception e) {
            response.setExtraMessage(e.getMessage());
            response.setErrorCode(400);
            response.setMessage("操作失败");
        } finally {
            return response;
        }
    }

}
