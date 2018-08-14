package com.shenghesun.choir.controller;

import com.shenghesun.common.BaseResponse;
import com.shenghesun.entity.SongSection;
import com.shenghesun.service.SongSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("song_section")
public class SongSectionController {

    @Autowired
    private SongSectionService songSectionService;

    @RequestMapping("my_song_section")
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
}
