package com.shenghesun.util.dmh;

 /**
  * 签名常量
  * @ClassName: SignatureConstants 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月8日 下午2:12:46  
  */
public class UrlConstants {
	/**
     * 第一次接口调用握手登录
     */
    public static final String OPEN_API_LOGIN  = "/OPENAPI/openApiLogin.json";
    
    /**
     * 通过分类 ID 获取歌单列表
     */
    public static final String TRACKLIST_GETTRACKLISTBYCATGORY = "/TRACKLIST/getTrackListByCatgory.json";
    
    /**
     * 查询当前已授权可使用的所有专辑列表。
     */
    public static final String ALBUM_ALBUMGETALL = "/ALBUM/albumGetAll.json";
    
    /**
     * 通过专辑唯一码(albumAssetCode), 获取专辑下的单曲列表。
     */
    public static final String ALBUM_ALBUMGETSONG = "/ALBUM/albumGetSong.json";
    
    /**
     * 通过此接口，选择歌曲进行切割。将音频切割为 1~60 秒的短音频。短音频可用
     * 于直接播放和插入短视频中播放。
     */
    public static final String TRACKSHORT_CREATSHORT = "/TRACKSHORT/creatShort.json";
    
    /**
     * 通过单曲的 TSID，获取单曲的播放链接。
     */
    public static final String SONG_TRACKLINK = "/SONG/trackLink.json";
    
    /**
     * 通过单曲的 TSID(又名 assetId)，查询单曲的详细信息。
     */
    public static final String SONG_TRACKINFO = "/SONG/trackInfo.json";
}
