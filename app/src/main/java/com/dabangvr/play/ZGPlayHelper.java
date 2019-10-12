package com.dabangvr.play;

import android.view.View;

import androidx.annotation.NonNull;

import com.dabangvr.publish.ZGBaseHelper;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.zego.zegoavkit2.ZegoStreamExtraPlayInfo;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoInitSDKCompletionCallback;
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback;

import static com.dbvr.baselibrary.other.ThirdParty.JG_APP_ID;
import static com.dbvr.baselibrary.other.ThirdParty.JG_APP_SIGN;

/**
 * ZGPlayHelper
 * <p>
 * 拉流帮助类
 * 主要简化SDK推流一系列接口
 * 开发者可参考该类的代码, 理解SDK接口
 * <p>
 * 注意!!! 开发者需要先初始化sdk, 登录房间后, 才能进行拉流
 */

public class ZGPlayHelper {

    public static ZGPlayHelper zgPlayHelper;

    public static ZGPlayHelper sharedInstance() {
        if (zgPlayHelper == null) {
            synchronized (ZGPlayHelper.class) {
                if (zgPlayHelper == null) {
                    zgPlayHelper = new ZGPlayHelper();
                }
            }
        }
        return zgPlayHelper;
    }

    /**
     * 是否initSDK
     *
     * @return true 代表initSDK完成, false 代表initSDK失败
     */
    private boolean isInitSDKSuccess() {
        if (ZGBaseHelper.sharedInstance().getZGBaseState() != ZGBaseHelper.ZGBaseState.InitSuccessState) {

            return false;
        }
        return true;
    }

    /**
     * 开始拉流
     *
     * @param streamID 同一房间内对应推流端的streamID, sdk基于streamID进行拉流
     * @param playView 用于渲染视频的view, 推荐使用 TextureView, 支持 SurfaceView 或 Surface
     * @return true 为调用成功, false为调用失败
     */
    public boolean startPlaying(@NonNull String streamID, View playView) {
        if (isInitSDKSuccess()) {
            ZegoLiveRoom zegoLiveRoom = ZGBaseHelper.sharedInstance().getZegoLiveRoom();
            return zegoLiveRoom.startPlayingStream(streamID, playView);
        } else {

            //AppLogger.getInstance().w(ZGPlayHelper.class, "拉流失败! SDK未初始化, 请先初始化SDK");
        }
        return false;
    }


    /**
     * 拉流代理很重要, 开发者可以按自己的需求在回调里实现自己的业务
     * app相关业务。回调介绍请参考文档<a>https://doc.zego.im/CN/217.html</>
     *
     * @param callback
     */
    public String setPlayerCallback(IZegoLivePlayerCallback callback) {
        if (isInitSDKSuccess()) {
            ZGBaseHelper.sharedInstance().getZegoLiveRoom().setZegoLivePlayerCallback(callback);
            return "拉流成功";
        } else {
            return "拉流失败，设置拉流代理失败! SDK未初始化, 请先初始化SDK";
            //AppLogger.getInstance().w(ZGPlayHelper.class, "设置拉流代理失败! SDK未初始化, 请先初始化SDK");
        }
    }

    /**
     * 释放拉流的代理
     * 当不再使用ZegoSDK时，可以释放拉流的代理
     * <p>
     * 调用时机：建议在unInitSDK之前设置。
     *
     */
    public void releasePlayerCallback() {
        ZGBaseHelper.sharedInstance().getZegoLiveRoom().setZegoLivePlayerCallback(null);
    }

    /**
     * 停止拉流
     *
     * @param streamID 不能为null。
     */
    public void stopPlaying(@NonNull String streamID) {
        if (isInitSDKSuccess()) {
            ZGBaseHelper.sharedInstance().getZegoLiveRoom().stopPlayingStream(streamID);
        }
    }
}
