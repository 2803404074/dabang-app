package com.dbvr.httplibrart.constans;

public class DyUrl {
    public static final String TOKEN_NAME = "DABANG-TOKEN";
    //public static final String BASE = "http://www.vrzbgw.com/dabang/";//远程
    public static final String BASE = "http://192.168.0.112:443/demo/";


    //我的-服务列表
    public static final String getChannelMenuList = "api/index/getChannelMenuList";

    //首页主播列表
    public static final String indexAnchorList = "api/pili/indexAnchorList";

    //创建推流
    public static final String createStream = "api/live/createStream";

    //直播礼物列表;
    public static final String getLiveGiftList = "api/live/getLiveGiftList";

    //获取七牛token
    public static final String getUploadConfigToken = "api/config/getUploadConfigToken";

    //直播标签列表
    public static final String getLiveCategoryList = "api/live/getLiveCategoryList";
}
