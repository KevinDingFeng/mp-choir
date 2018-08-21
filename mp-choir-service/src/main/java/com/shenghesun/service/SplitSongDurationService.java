package com.shenghesun.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shenghesun.dao.SplitSongDurationDao;
import com.shenghesun.entity.SplitSongDuration;

 /**
  * 歌曲分段时长
  * @ClassName: SplitSongDurationService 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月21日 下午3:58:06  
  */
@Service
public class SplitSongDurationService {

    @Autowired
    private SplitSongDurationDao splitSongDurationDao;

    public List<SplitSongDuration> findBySongName(String songName, int population) {
        return splitSongDurationDao.findBySongNameAndPopulation(songName,population);
    }

}
