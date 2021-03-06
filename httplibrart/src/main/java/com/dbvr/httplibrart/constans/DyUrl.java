package com.dbvr.httplibrart.constans;

public class DyUrl {

    public static final String QINIUDOMAN = "http://image.vrzbgw.com/";
    public static final String TOKEN_NAME = "DABANG-TOKEN";

    public static final String BASE = "http://192.168.0.250:8085/dabang/";
    //public static final String BASE = "http://api.vrzbgw.com:8085/dabang/";//远程

    //根据经纬度
    public static final String getAmapReverse = "api/config/getAmapReverse";

    //（开播）获取自己商家下的分类标签
    public static final String getOnlineCategorys = "api/category/getOnlineCategorys";
    //（开播）创建推流
    public static final String createStream = "api/live/createStream";

    //直播间点赞
    public static final String praseOnline = "/api/live/praseOnline";

    //直播间商品列表
    public static final String getRoomGoodsList = "api/live/getRoomGoodsList";


    //获取用户信息
    public static final String getUserInfo = "api/getUserInfo";

    //收获地址列表
    public static final String getAddressList = "api/my/getAddressList";

    //添加/修改地址
    public static final String addAddress ="api/my/addAddress";

    //删除地址
    public static final String deleteAddress = "api/my/deleteAddress";

    //关注主播
    public static final String updateFans = "api/my/updateFans";

    //粉丝列表
    public static final String getFansList = "api/my/getFansList";

    //关注列表
    public static final String getFocusedsList = "/api/my/getFocusedsList";

    //直播礼物列表;
    public static final String getLiveGiftList = "api/live/getLiveGiftList";

    //打赏礼物
    public static final String rewardGift = "api/live/rewardGift";

    //获取七牛token
    public static final String getUploadConfigToken = "api/config/getUploadConfigToken";

    //直播标签列表
    public static final String getLiveCategoryList = "api/live/getLiveCategoryList";

    //商品详情
    public static final String getGoodsDetails = "api/goods/getGoodsDetails";

    //收藏商品
    public static final String getCollectGoods = "/api/goodscollect/getCollectGoods";

    //我的订单列表
    public static String getOrderList = "api/order/getOrderList";

    //我的收藏列表
    public static String getCollectGoodsList = "api/goodscollect/getCollectGoodsList";

    //修改订单状态
    public static String updateOrderState = "api/order/updateOrderState";


    //微信退款
    public static String refundRequest = "api/payorder/refundRequest";

    //微信支付统一入口，获取订单号
    public static String prepayOrder = "api/payorder/prepayOrder";

    //跳币充值
    public static String payDiamond = "api/live/payDiamond";


    //查看商户入驻状态
    public static String getDeptState = "api/dept/getDeptState";

    //购物车获取确认订单
    public static String confirmGoods2Cart = "api/buygoods/confirmGoods2Cart";

    //修改购物车数量
    public static String updateNumber2Cart = "api/buygoods/updateNumber2Cart";
    //删除购物车
    public static String delete2Cart = "api/buygoods/delete2Cart";

    //购物车列表
    public static String getGoods2CartList = "api/goods/getGoods2CartList";

    //--------------------------------------首页---------------------------------------------------

    //根据类型查询直播列表
    public static final String getOnlineList = "api/live/getOnlineList";

    //短视频上传凭证
    public static final String getTencentSign = "api/liveShortVideo/getTencentSign";

    //短视频列表
    public static final String getLiveShortVideoList = "api/liveShortVideo/getLiveShortVideoList";

    //直播短视频列表
    public static final String indexTT ="api/index/indexTT";

    //发布短视频
    public static final String saveVideo = "api/liveShortVideo/saveVideo";

    //短视频列表
    public static final String getShortVideoComment = "api/liveShortVideo/getShortVideoComment";
    //评论短视频
    public static final String commentShortVideo = "api/liveShortVideo/commentShortVideo";
    //点赞短视频
    public static final String praseShortVideo = "api/liveShortVideo/praseShortVideo";
    //点赞评论
    public static final String praseShortVideoComment = "api/liveShortVideo/praseShortVideoComment";

    //根据定位直播列表
    public static final String getCityOnlineList = "api/live/getCityOnlineList";


    //修改用户信息
    public static final String update = "/api/update";
   //根据经纬度获取省市县
   public static final String getLocation = "api/config/getAmapReverse";

   //获取省市县联动
   public static final String getAmapDistrict = "/api/config/getAmapDistrict";

    //搜索用户
    public static final String queryUser = "api/queryUser";
    //获取某用户信息
    public static final String getUserByUserId = "api/getUserByUserId";
}
