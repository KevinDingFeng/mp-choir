package com.shenghesun.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.dao.SyntheticSongsDao;
import com.shenghesun.entity.SyntheticSongs;
import com.shenghesun.util.FileIOUtil;
import com.shenghesun.util.HttpClientService;

@Service
public class SyntheticSongsService {

//    @Autowired
//    private UserService userService;

    @Autowired
    private SyntheticSongsDao syntheticSongsDao;

    @Value("${upload.file.path}")
    private String audioFilePath;

    @Value("${show.file.path}")
    private String showFilePath;
    
    @Value("${server.host.url}")
    private String hostUrl;

    public SyntheticSongs findById(Long id) {
        if (id == null || id < 1) {
            return null;
        }
        return syntheticSongsDao.findById(id).orElse(null);
    }

    public SyntheticSongs findByChoirId(Long choirId) {
        if (choirId == null || choirId < 1) {
            return null;
        }
        return syntheticSongsDao.findByChoirId(choirId);
    }

    public List<SyntheticSongs> findMySyntheticSongs(Long userId) {
        if (userId == null || userId < 1) {
            return null;
        }
        return syntheticSongsDao.findByUserIdsLikeAndRemovedOrderByLastModifiedDesc("%," + userId + ",%", false);
    }

    public String getWxacodePathByChoirId(Long choirId) {
        if (choirId == null || choirId < 1) {
            return null;
        }
        SyntheticSongs syntheticSongs = syntheticSongsDao.findByChoirId(choirId);
        if (syntheticSongs == null) {
            return null;
        }
        return getWxacodePath(syntheticSongs.getId());
    }

    public String getWxacodePath(Long id) {
        if (id == null || id < 1) {
            return null;
        }
        SyntheticSongs syntheticSongs = syntheticSongsDao.findById(id).orElse(null);
        if (syntheticSongs == null) {
            return null;
        }
        String wxacodePath = syntheticSongs.getWxacodePath();
        if (StringUtils.isNotBlank(wxacodePath)) {
            return hostUrl + this.showFilePath + wxacodePath;
        }
        String tokenResult = HttpClientService.httpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=" +
                "client_credential&appid=wxd5d28f91e9c2c730&secret=bd68bcdc797acdd91e05083b4d111286", null);
        if (StringUtils.isBlank(tokenResult)) {
            return null;
        }
        JSONObject tokenObject = JSON.parseObject(tokenResult);
        String accessToken = tokenObject.getString("access_token");
        String url = "https://api.weixin.qq.com/wxa/getwxacode?access_token=" + accessToken;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path", "pages/c_musice/c_musice");
        jsonObject.put("width", 430);
        HttpResponse response = HttpClientService.post(url, null, jsonObject.toJSONString());
        try {
            InputStream inputStream = response.getEntity().getContent();
            String path = FileIOUtil.uploadFile("wxacode" + id + ".jpg", inputStream,
                    audioFilePath, false);
            System.out.println(path);
            syntheticSongs.setWxacodePath(path);
            syntheticSongsDao.save(syntheticSongs);
            return hostUrl + this.showFilePath + path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SyntheticSongs save(SyntheticSongs syntheticSongs) {
        return syntheticSongsDao.save(syntheticSongs);
    }
}
