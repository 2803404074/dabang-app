package com.dbvr.httplibrart.constans;

public class DyUrl {
    public static final String TOKEN_NAME = "DABANG-TOKEN";
    //public static final String BASE = "http://www.vrzbgw.com/dabang/";
    public static final String BASE = "http://192.168.0.112:8085/dabang";
    //public static final String BASE = "http://api.vrzbgw.com:8085/dabang";//远程


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

    // 个人中心菜单接口
    public static final String getChannelMenuList = "/api/config/getChannelMenuList";


    //我的订单列表
    public static String getOrderList = "/api/order/getOrderList";

    //修改订单状态
    public static String updateOrderState = "/api/order/updateOrderState";


    //微信退款
    public static String refundRequest = "/api/payorder/refundRequest";
    //微信支付统一入口，获取订单号
    public static String prepayOrder = "/api/payorder/prepayOrder";
    //重新支付
    public static String prepayOrderAgain = "/api/payorder/prepayOrderAgain";



    //购物车获取确认订单
    public static String confirmGoods2Cart = "/api/buygoods/confirmGoods2Cart";

    //修改购物车数量
    public static String updateNumber2Cart = "/api/buygoods/updateNumber2Cart";
    //删除购物车
    public static String delete2Cart = "/api/buygoods/delete2Cart";

    //购物车列表
    public static String getGoods2CartList = "/api/goods/getGoods2CartList";

}
