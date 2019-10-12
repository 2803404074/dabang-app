package com.dabangvr.application;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.dabangvr.publish.ZGBaseHelper;
import com.dbvr.baselibrary.utils.NetWorkStateReceiver;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoInitSDKCompletionCallback;

import java.util.Date;

import static com.dbvr.baselibrary.other.ThirdParty.JG_APP_ID;
import static com.dbvr.baselibrary.other.ThirdParty.JG_APP_SIGN;
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

        //即购
        // 使用Zego sdk前必须先设置SDKContext。
        ZGBaseHelper.sharedInstance().setSDKContextEx(null, null, 10 * 1024 * 1024, this);
        ZegoLiveRoom.setSDKContext(new ZegoLiveRoom.SDKContextEx() {

            @Override
            public long getLogFileSize() {
                return 0;  // 单个日志文件的大小，必须在 [5M, 100M] 之间；当返回 0 时，表示关闭写日志功能，不推荐关闭日志。
            }

            @Override
            public String getSubLogFolder() {
                return null;
            }

            @Override
            public String getSoFullPath() {

                return null; // return null 表示使用默认方式加载 libzegoliveroom.so
                // 此处可以返回 so 的绝对路径，用来指定从这个位置加载 libzegoliveroom.so，确保应用具备存取此路径的权限
            }

            @Override
            public String getLogPath() {
                return null; //  return null 表示日志文件会存储到默认位置，如果返回非空，则将日志文件存储到该路径下，注意应用必须具备存取该目录的权限
            }

            @Override
            public Application getAppContext() {
                return MyApplication.this; // android上下文. 不能为null
            }
        });
        ZGBaseHelper.sharedInstance().initZegoSDK(JG_APP_ID, JG_APP_SIGN, true, errorCode -> {
            // errorCode 非0 代表初始化sdk失败
            if (errorCode == 0) {
                Log.e("zego","初始化成功");
            } else {
                Log.e("zego","初始化失败");
            }
        });
        ZegoLiveRoom.setTestEnv(true);
    }
}
