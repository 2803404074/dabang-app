package com.dbvr.baselibrary.base;

public interface ParameterContens {

//首页
    String CLIENT_WDDD = "client_wddd";//我的订单
    String CLIENT_YHQ = "client_yhq";//优惠券
    String CLIENT_WDPJ = "client_wdpj";//我的评价
    String CLIENT_GWC = "client_gwc";//购物车
    String CLIENT_GRZL = "client_grzl";//个人资料
    String CLIENT_SJRZ = "client_sjrz";//商家入驻
    String CLIENT_ZBSQ = "client_zbsq";//主播申请
    String CLIENT_SZ = "client_sz";//设置
    String CLIENT_FK = "client_fk";//反馈
    String CLIENT_GFKF = "client_gfkf";//官方客服
    String CLIENT_GYWM = "client_gywm";//关于我们


    long clickTime=2000l; //点击事件防止过度点击

    String depVo = "depVo";//商家入驻传递数据标识
    String AnchorVo = "AnchorVo";//主播入驻传递数据标识
    String idcartFacial = "idcartFacial";//身份证正面
    String idcartBehind = "idcartBehind";//身份证反面
    String threeCertificates = "threeCertificates";//三证合一
}
