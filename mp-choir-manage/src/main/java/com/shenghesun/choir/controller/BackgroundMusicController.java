package com.shenghesun.choir.controller;

import com.shenghesun.common.BaseResponse;
import com.shenghesun.entity.BackgroundMusic;
import com.shenghesun.service.BackgroundMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/background_music")
public class BackgroundMusicController {

    @Autowired
    private BackgroundMusicService backgroundMusicService;

    @RequestMapping("/get_background_music")
    public BaseResponse getBackgroundMusicInfo(@RequestParam String name, @RequestParam Integer population, @RequestParam Integer sort) {
        BaseResponse response = new BaseResponse();
        try {
            //BackgroundMusic backgroundMusic = backgroundMusicService.getBackgroundMusicInfo(name, population, sort);
            BackgroundMusic backgroundMusic = backgroundMusicService.getBackgroundMusicInfo("葫芦娃", population, sort);
            response.setSuccess(true);
            response.setData(backgroundMusic);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return response;
        }
        return response;
    }

}
