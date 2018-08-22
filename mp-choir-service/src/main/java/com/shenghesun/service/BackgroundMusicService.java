package com.shenghesun.service;

import com.shenghesun.dao.BackgroundMusicDao;
import com.shenghesun.entity.BackgroundMusic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackgroundMusicService {

    @Autowired
    private BackgroundMusicDao backgroundMusicDao;

    public BackgroundMusic getBackgroundMusicInfo(String name, int population, int sort) {
        return backgroundMusicDao.findByNameAndPopulationAndSort(name, population, sort);
    }

}
