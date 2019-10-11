package com.dabangvr.application;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.dbvr.baselibrary.utils.NetWorkStateReceiver;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.dbvr.baselibrary.other.ThirdParty.WECHART_APP_ID;

public class MyApplication extends Application {
    private NetWorkStateReceiver netWorkStateReceiver;
    public static IWXAPI api;
    @Override
    public void onCreate() {
        super.onCreate();

        //网络变化
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);


        //圆形图像初始化
        Fresco.initialize(this);

        //微信
        api = WXAPIFactory.createWXAPI(this,WECHART_APP_ID,true);
        api.registerApp(WECHART_APP_ID);
    }
}
