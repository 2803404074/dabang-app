package com.dabangvr.comment.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.dbvr.baselibrary.utils.NetWorkStateReceiver;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.push.EMPushConfig;
import com.hyphenate.push.EMPushHelper;
import com.mob.MobSDK;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.ugc.TXUGCBase;
import com.zego.zegoliveroom.ZegoLiveRoom;

import java.util.Iterator;
import java.util.List;

import static com.dbvr.baselibrary.model.ThirdParty.WECHART_APP_ID;

public class MyApplication extends Application implements NetWorkStateReceiver.NetCallBack {

    private NetWorkStateReceiver netWorkStateReceiver;
    public static IWXAPI api;
    private static MyApplication instance;
    public static Context appContext;
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();
        //网络变化
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver(this);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);

        //圆形图像初始化
        Fresco.initialize(this);

        //分享
        MobSDK.init(this);

        //微信
        api = WXAPIFactory.createWXAPI(this, WECHART_APP_ID, true);
        api.registerApp(WECHART_APP_ID);

        //环信
        initHy();

        initZeGoContext();

    }


    private void initHy() {
        // 第一步
        EMOptions options = initChatOptions();
        // 第二步
        boolean success = initSDK(this, options);
        if (success) {
            EMClient.getInstance().setDebugMode(true);
        }
    }
    private EMOptions initChatOptions() {
        EMOptions options = new EMOptions();
        return options;
    }

    private boolean sdkInited = false;

    public synchronized boolean initSDK(Context context, EMOptions options) {
        if (sdkInited) {
            return true;
        }
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {
            return false;
        }
        if (options == null) {
            EMClient.getInstance().init(context, initChatOptions());
        } else {
            EMClient.getInstance().init(context, options);
        }
        sdkInited = true;
        return true;
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }

    public void initShortVideo(){
        String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/8c1a438e5d22efa5e67c1c0b9f951fa9/TXUgcSDK.licence"; //您从控制台申请的 licence url
        String ugcKey = "3b74ed96b09b26753502311596ef08a5";
        //腾讯云短视频
        TXUGCBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey);
    }



    private void initZeGoContext() {
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
        ZegoLiveRoom.setTestEnv(true); // 为 true 则说明开启测试环境，为 false 则说明使用正式环境
    }

    private ZegoLiveRoom zegoLiveRoom;
    static final public long appID = 432833590L;
    // 需要强转成 byte 类型，原因: 在java中，整数默认是 int 类型
    static final public byte[] appSign  = new byte[]{ (byte)0xba,(byte)0x87,(byte)0x10,(byte)0xce,(byte)0xfa,(byte)0xf4,(byte)0x56,(byte)0xaa,(byte)0xc1,(byte)0x0c,(byte)0xc7,(byte)0x43,(byte)0x55,(byte)0xfb,(byte)0x36,(byte)0xae,(byte)0x7f,(byte)0x81,(byte)0x94,(byte)0x6c,(byte)0x58,(byte)0x55,(byte)0xc0,(byte)0xb1,(byte)0x1d,(byte)0x55,(byte)0x4f,(byte)0xdf,(byte)0xa5,(byte)0x97,(byte)0x62,(byte)0x76 };

    public ZegoLiveRoom initGeGo(){
        zegoLiveRoom = new ZegoLiveRoom();
        zegoLiveRoom.initSDK(appID, appSign, errorCode -> {
            // errorCode 非0 代表初始化sdk失败
            if (errorCode == 0){

            }else {
                ToastUtil.showShort(this,"初始化失败"+errorCode);
            }
        });
        return zegoLiveRoom;
    }
    public void desGeGo(){
        if (zegoLiveRoom!=null){
            zegoLiveRoom.unInitSDK();
        }
    }

    @Override
    public void changeNet(boolean hasNet) {
        if (netCallBack!=null){
            netCallBack.changeNet(hasNet);
        }
    }

    private NetCallBack netCallBack;
    public interface NetCallBack{
        void changeNet(boolean hasNet);
    }

    public void setNetCallBack(NetCallBack netCallBack) {
        this.netCallBack = netCallBack;
    }



    public void onDes(){
        appContext = null;
        api = null;
        instance = null;
    }

}
