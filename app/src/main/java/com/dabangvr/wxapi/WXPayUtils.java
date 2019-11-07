package com.dabangvr.wxapi;

import android.content.Context;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 *
 */

public class WXPayUtils {


    private IWXAPI iwxapi; //微信支付api

    private WXPayBuilder builder;

    private WXPayUtils(WXPayBuilder builder) {
        this.builder = builder;
    }

    /**
     * 调起微信支付的方法,不需要在客户端签名
     **/
    public void toWXPayNotSign(Context context) {
        iwxapi = WXAPIFactory.createWXAPI(context, builder.getAppId()); //初始化微信api
        iwxapi.registerApp(builder.getAppId()); //注册appid  appid可以在开发平台获取

        //这里注意要放在子线程
        Runnable payRunnable = () -> {
            PayReq request = new PayReq(); //调起微信APP的对象
            //下面是设置必要的参数，也就是前面说的参数,这几个参数从何而来请看上面说明
            request.appId = builder.getAppId();
            request.partnerId = builder.getPartnerId();
            request.prepayId = builder.getPrepayId();
            request.packageValue = builder.getPackageValue();
            request.nonceStr = builder.getNonceStr();
            request.timeStamp =builder.getTimeStamp();
            request.sign = builder.getSign();
            iwxapi.sendReq(request);//发送调起微信的请求
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public static class WXPayBuilder {
        public String appId;
        public String partnerId;
        public String prepayId;
        public String packageValue;
        public String nonceStr;
        public String timeStamp;
        public String sign;

        public WXPayUtils build() {
            return new WXPayUtils(this);
        }

        public String getAppId() {
            return appId;
        }

        public WXPayBuilder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public String getPartnerId() {
            return partnerId;
        }

        public WXPayBuilder setPartnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public String getPrepayId() {
            return prepayId;
        }

        public WXPayBuilder setPrepayId(String prepayId) {
            this.prepayId = prepayId;
            return this;
        }

        public String getPackageValue() {
            return packageValue;
        }

        public WXPayBuilder setPackageValue(String packageValue) {
            this.packageValue = packageValue;
            return this;
        }

        public String getNonceStr() {
            return nonceStr;
        }

        public WXPayBuilder setNonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public WXPayBuilder setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public String getSign() {
            return sign;
        }

        public WXPayBuilder setSign(String sign) {
            this.sign = sign;
            return this;
        }
    }


}
