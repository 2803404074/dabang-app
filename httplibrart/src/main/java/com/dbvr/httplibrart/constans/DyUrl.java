package com.dbvr.httplibrart.constans;

public class DyUrl {

    public static final String QINIUDOMAN = "http://image.vrzbgw.com/";

    public static final String TOKEN_NAME = "DABANG-TOKEN";
    //public static final String BASE = "http://www.vrzbgw.com/dabang/";
    public static final String BASE = "http://192.168.0.112:8085/dabang/";
    //public static final String BASE = "http://api.vrzbgw.com:8085/dabang/";//远程

    //获取用户信息
    public static final String getUserInfo = "api/getUserInfo";

    //首页主播列表
    public static final String indexAnchorList = "api/pili/indexAnchorList";

    //首页关注的主播正在直播的列表
    public static final String indexFollowList = "api/index/indexFollowList";

    //关注主播
    public static final String updateFans = "api/my/updateFans";

    //创建推流
    public static final String createStream = "api/live/createStream";

    //粉丝列表
    public static final String getFansList = "api/my/getFansList";

    //直播礼物列表;
    public static final String getLiveGiftList = "api/live/getLiveGiftList";

    //打赏礼物
    public static final String rewardGift = "api/live/rewardGift";

    //获取七牛token
    public static final String getUploadConfigToken = "api/config/getUploadConfigToken";

    //直播标签列表
    public static final String getLiveCategoryList = "api/live/getLiveCategoryList";

    // 个人中心菜单接口
    public static final String getChannelMenuList = "api/config/getChannelMenuList";


    //我的订单列表
    public static String getOrderList = "api/order/getOrderList";

    //修改订单状态
    public static String updateOrderState = "api/order/updateOrderState";


    //微信退款
    public static String refundRequest = "api/payorder/refundRequest";
    //微信支付统一入口，获取订单号
    public static String prepayOrder = "api/payorder/prepayOrder";




    //购物车获取确认订单
    public static String confirmGoods2Cart = "api/buygoods/confirmGoods2Cart";

    //修改购物车数量
    public static String updateNumber2Cart = "api/buygoods/updateNumber2Cart";
    //删除购物车
    public static String delete2Cart = "api/buygoods/delete2Cart";

    //购物车列表
    public static String getGoods2CartList = "api/goods/getGoods2CartList";



    //--------------------------------------首页---------------------------------------------------
    //发现(大头像以及大头像下面的列表)
    public static final String indexFind = "api/index/indexFind";

    //根据类型查询直播列表
    public static final String getOnlineList = "api/live/getOnlineList";

    //跳跳列表
    public static final String indexTT = "api/index/indexTT";

   //根据经纬度获取省市县
   public static final String getLocation = "api/config/getAmapReverse";

   //发动态
    public static final String sendSay = "api/my/sendSay";
    //获取动态列表
    public static final String getSayList = "api/my/getSayList";
    //获取赞列表
    public static final String praisedList = "api/my/praisedList";
    //获取评论列表
    public static final String getCommentList = "api/comment/getCommentList";
    //搜索用户
    public static final String queryUser = "api/queryUser";

    //获取高德省市县三级联动json
    public static final String getAmapDistrict = "api/config/getAmapDistrict";
    public static String praisedSay="api/my/praisedSay";
    public static String getPraisedList="api/my/getPraisedList";

    public static String commentSay="api/my/commentSay";
}
