package com.shenghesun.service;

import com.shenghesun.dao.SyntheticSongsDao;
import com.shenghesun.entity.SyntheticSongs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyntheticSongsService {

    @Autowired
    private SyntheticSongsDao syntheticSongsDao;

    public List<SyntheticSongs> findMySyntheticSongs() {
        //TODO
        Long userId = 1L;
        return syntheticSongsDao.findByUserIdsLike("%," + userId + ",%");
    }

}
