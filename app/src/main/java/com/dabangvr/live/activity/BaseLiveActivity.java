package com.dabangvr.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.live.widget.CameraPreviewFrameView;
import com.dabangvr.live.widget.LiveFunctionView;
import com.dbvr.baselibrary.model.LiveComment;
import com.dbvr.baselibrary.other.Contents;
import com.dbvr.baselibrary.ui.LoadingUtils;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.WatermarkSetting;
import com.r0adkll.slidr.Slidr;

import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 黄仕豪 on 2019/7/03
 */

public abstract class BaseLiveActivity extends Activity implements
        StreamingStateChangedListener,
        StreamStatusCallback,
        AudioSourceCallback,
        StreamingSessionListener {

    public final int handleMessRequestCode = 100;
    public int dataSize;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == handleMessRequestCode) {
                ArrayList data = msg.getData().getParcelableArrayList("data");
                dataSize++;
                commentMessCallBack(data,dataSize);
            }
            return false;
        }
    });



    @BindView(R.id.cameraPreview_surfaceView)
    CameraPreviewFrameView cameraPreviewFrameView;
    public MediaStreamingManager mMediaStreamingManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);

        initUser();
        initLiveView();
        initCommentView();
        initGift();
        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    private LoadingUtils mLoaddingUtils;

    public void setLoaddingView(boolean isLoadding) {
        if (mLoaddingUtils == null) {
            mLoaddingUtils = new LoadingUtils(this);
        }
        if (isLoadding) {
            mLoaddingUtils.show();
        } else {
            mLoaddingUtils.dismiss();
        }
    }

    // 设置布局
    public abstract int setLayout();

    public abstract void initUser();

    public abstract void initLiveView();

    public abstract void initCommentView();

    public abstract void initGift();


    /**
     * 收到评论消息
     * @param data
     * @param dataSize
     */
    public abstract void commentMessCallBack(ArrayList data,int dataSize);

    /**
     * 收到礼物消息
     * @param head
     * @param uName
     * @param num
     * @param gName
     * @param money
     */
    public abstract void giftMessCallBack(String head,String uName,int num,String gName,int money);

    /**
     * 收到点赞消息
     * @return
     */
    public abstract void dzMessCallBack();


    /**
     * 收到弹幕消息
     * @return
     */
    public abstract void dmMessCallBack(String head,String name,String content);
    @Override
    protected void onDestroy() {
        setLoaddingView(false);
        LiveFunctionView.getInstance(this).destroy();
        BottomDialogUtil2.getInstance(this).dess();
        DialogUtil.getInstance(this).des();
        mMediaStreamingManager.destroy();
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMediaStreamingManager.pause();
    }
    /**
     * @author:
     * @create at: 2018/11/14  10:57
     * @Description: 防止 Dns 被劫持
     */
    public DnsManager getMyDnsManager() {
        IResolver r0 = null;
        IResolver r1 = new DnspodFree();
        IResolver r2 = AndroidDnsServer.defaultResolver();
        try {
            r0 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }


    public void setNotifyUi(LiveComment liveComment) {
        Log.e("HyListener", "收到消息:" + liveComment.toString());
        switch (liveComment.getMsgTag()) {
            case Contents.HY_JOIN://评论消息
                Bundle bundle3 = new Bundle();
                ArrayList arr3 = new ArrayList();
                arr3.add(liveComment);
                bundle3.putStringArrayList("data", arr3);
                Message message3 = new Message();
                message3.what = handleMessRequestCode;
                message3.setData(bundle3);
                handler.sendMessage(message3);
                break;
            case Contents.HY_SERVER://评论消息
                Bundle bundle2 = new Bundle();
                ArrayList arr2 = new ArrayList();
                arr2.add(liveComment);
                bundle2.putStringArrayList("data", arr2);
                Message message2 = new Message();
                message2.what = handleMessRequestCode;
                message2.setData(bundle2);
                handler.sendMessage(message2);
                break;
            case Contents.HY_COMMENT://评论消息
                Bundle bundle = new Bundle();
                ArrayList arr = new ArrayList();
                arr.add(liveComment);
                bundle.putStringArrayList("data", arr);
                Message message = new Message();
                message.what = handleMessRequestCode;
                message.setData(bundle);
                handler.sendMessage(message);
                break;
            case Contents.HY_DM://弹幕消息
                break;
            case Contents.HY_DS://打赏消息
                Log.e("HyListener", "收到打赏消息:" + liveComment.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        giftMessCallBack(liveComment.getHeadUrl(),liveComment.getUserName(),liveComment.getMsgDsComment().getGiftNum(),liveComment.getMsgDsComment().getGiftName(),liveComment.getMsgDsComment().getGiftTag());
                    }
                });
                break;
            case Contents.HY_ORDER://下单消息
                dmMessCallBack(liveComment.getHeadUrl(),liveComment.getUserName(),liveComment.getMsgComment());
                break;
            case Contents.HY_DZ://点赞消息,收到一条累计
                dzMessCallBack();
                break;
        }
    }


    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        switch (streamingState) {
            case PREPARING:
                break;
            case READY:
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "正在连接..."));
                break;
            case CONNECTING:
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "连接成功..."));
                break;
            case STREAMING:
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "跳跳已万事具备，只欠您的表演,开始吧！"));
                //开始计时
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        chronometer.setBase(SystemClock.elapsedRealtime());
//                        int hour = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000 / 60);
//                        chronometer.setFormat("0" + String.valueOf(hour) + ":%s");
//                        chronometer.start();
//
//                        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//
//                            @Override
//                            public void onChronometerTick(Chronometer ch) {
//                                miss++;
//                            }
//                        });
//                    }
//                });
                break;
            case SHUTDOWN:
                //暂停计时
                //chronometer.stop();
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "跳跳中断..."));
                break;
            case IOERROR:
                //暂停计时
                //chronometer.stop();
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "网络连接失败..."));
                break;
            case OPEN_CAMERA_FAIL:
                break;
            case DISCONNECTED:
                //暂停计时
                //chronometer.stop();
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "已经断开连接..."));
                break;
            case TORCH_INFO://"开启闪光灯
                break;
        }
    }

    public String roomNumber = "123456";
    public String  createHyRoom() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        return roomNumber;
    }

    EMMessageListener msgListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            Log.e("HyListener", "onMessageReceived:" + messages.toString());
            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                Log.e("HyListener", "onMessageReceived:" + username);
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(roomNumber)) {
                    EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                    Gson gson = new Gson();
                    LiveComment liveComment = gson.fromJson(txtBody.getMessage(), LiveComment.class);
                    setNotifyUi(liveComment);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // 收到透传消息
            Log.e("HyListener", "onCmdMessageReceived:" + messages.toString());
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            Log.e("HyListener", "onMessageRead:" + list.toString());
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            Log.e("HyListener", "onMessageDelivered:" + list.toString());
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            Log.e("HyListener", "onMessageRecalled:" + list.toString());
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            Log.e("HyListener", "onMessageChanged:" + o.toString());
        }
    };
    public Context getContext() {
        return this;
    }
}
