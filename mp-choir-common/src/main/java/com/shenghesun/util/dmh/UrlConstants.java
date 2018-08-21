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
    
    
    /**
     * 通过输入关键字，进行模糊查询。可以查询单曲、专辑、艺人、歌单、电台、榜单。
     */
    public static final String SEARCH_MERGE = "/SEARCH/merge.json";
    
    /**
     * 多条件组合搜索歌曲/专辑
     * 提供按音乐流派、语言、发行时间、歌曲名、表演者、专辑这些条件去组合搜索歌曲或专辑，
     * 可精准或模糊查询结果。
     * 适用于解析用户语意总结各条件的关键词的搜索场景。如语音搜索听歌。
     * 如：我想听齐秦在90年代唱的英语流行歌。可总结出：
     * 演唱表演者：齐秦；发行时间：1990-1999；歌曲语言：英语；流派：流行。
     * 通过此接口可找出相关的歌曲。
     */
    public static final String SEARCH_INSEARCH = "/SEARCH/inSearch.json";
    
    /**
     * 查询当前用户定制的服务类型。
     */
    public static final String OPENAPI_GETSPSESSIONBIZLIST = "/OPENAPI/getSpSessionBizList.json";
    
    /**
     * 设置当前要启用的服务。
     */
    public static final String OPENAPI_SETSPUSERBIZID = "/OPENAPI/setSpUserBizID.json";
    
    /**
     * 通过资源id(resource id)和TSID获取短音频信息。
     */
    public static final String TRACKSHORT_SELECTSHORTRATE = "/TRACKSHORT/selectShortRate.json";
}
