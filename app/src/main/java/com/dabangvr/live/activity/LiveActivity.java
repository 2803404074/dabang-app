package com.dabangvr.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.live.widget.CameraPreviewFrameView;
import com.dabangvr.live.widget.LiveFunctionView;
import com.dbvr.baselibrary.model.GiftMo;
import com.dbvr.baselibrary.model.LiveComment;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.other.Contents;
import com.dbvr.baselibrary.utils.BottomDialogUtil;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.DataUtil;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
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

import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.qiniu.pili.droid.streaming.AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC;

public class LiveActivity extends Activity implements
        StreamingStateChangedListener,
        StreamStatusCallback,
        AudioSourceCallback,
        StreamingSessionListener {
    @BindView(R.id.cameraPreview_surfaceView)
    CameraPreviewFrameView cameraPreviewFrameView;

    private MediaStreamingManager mMediaStreamingManager;
    private StreamingProfile mProfile;
    private String TAG = "StreamingByCameraActivity";
    //更新评论内容，handle
    private final int handleMessRequestCode = 100;

    private Context mContext;

    @BindView(R.id.ll_notice)
    LinearLayout llNotice;//预览提示

    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;

    @BindView(R.id.tvNickName)
    TextView tvNickName;

    @BindView(R.id.tvOnLine)
    TextView tvOnLine;//在线人数
    private int onLineNumber;//在线人数

    @BindView(R.id.tvRoomNumber)
    TextView tvRoomNumber;//房间号

    @BindView(R.id.tvDzNum)
    TextView tvDzNum;//点赞数量

    @BindView(R.id.tvTbSy)
    TextView tvTbSy;//收到礼物的时候，弹出收益数据

    @BindView(R.id.charCounter)
    Chronometer chronometer;//计时器
    private int miss = 0;//总秒数


    private boolean isStart = false;//未开始直播或正在直播标志
    @BindView(R.id.tvFinishLive)
    TextView tvFinishLive;//结束直播按钮

    @BindView(R.id.ll_goods)
    LinearLayout llGoods;//商品点击区域

    @BindView(R.id.tvGoodsNum)
    TextView tvGoodsNum;//直播商品的数量

    @BindView(R.id.llDsView)
    LinearLayout llDsView;//礼物打赏弹出的视图
    @BindView(R.id.sdvDsHead)
    SimpleDraweeView sdvDsHead;//打赏人头像
    @BindView(R.id.tv_ds_name)
    TextView tvDsName;//打赏人昵称
    @BindView(R.id.ivDsContent)
    ImageView ivDsContent;//打赏礼物图片

    @BindView(R.id.ivFunction)
    ImageView ivFunction;//点击展开功能列表

    //评论列表
    @BindView(R.id.recycle_comment)
    RecyclerView recyclerView;
    //列表适配器
    private RecyclerAdapter commentAdapter;
    //评论数据源
    private List<LiveComment> commentData = new ArrayList<>();

    //礼物数据源
    private List<GiftMo> giftList = new ArrayList<>();

    //消息数量，始终将最新消息显示在recycelview的底部
    private int dataSize;

    private UserMess userMess;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == handleMessRequestCode) {
                ArrayList data = msg.getData().getParcelableArrayList("data");
                dataSize++;
                recyclerView.smoothScrollToPosition(dataSize);
                commentAdapter.addAll(data);
            }
            return false;
        }
    });


    private void setNotifyUi(LiveComment liveComment) {
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
                        //打赏人头像
                        sdvDsHead.setImageURI(liveComment.getHeadUrl());
                        tvDsName.setText(liveComment.getUserName());
                        for (int i = 0; i < giftList.size(); i++) {
                            if (giftList.get(i).getId() == liveComment.getMsgDsComment().getGiftTag()) {
                                Glide.with(getApplicationContext()).load(giftList.get(i).getGiftUrl()).into(ivDsContent);
                                tvTbSy.setText("收益+" + giftList.get(i).getGiftCoins());
                                giftNum += Double.valueOf(giftList.get(i).getGiftCoins());
                                break;
                            }
                        }
                        List<View> list = new ArrayList<>();
                        list.add(llDsView);
                        list.add(tvTbSy);
                    }
                });
                break;
            case Contents.HY_ORDER://下单消息
                break;
            case Contents.HY_DZ://点赞消息,收到一条累计
                tvDzNum.setText("赞 "+dzNum);
                break;
        }
    }
    private int dzNum = 0;//点赞累加
    private double giftNum = 0;//礼物收益累加

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        mContext = this;
        ButterKnife.bind(this);

        //初始化用户信息
        initUserDate();

        //初始化直播控件
        initLive();

        //初始化评论框
        initCommentUi();

        //初始化礼物列表
        initGift();

    }

    private CameraStreamingSetting camerasetting;
    private WatermarkSetting watermarkSetting;

    private void initLive() {
        String publishURLFromServer = getIntent().getStringExtra("streamUrl");
        mProfile = new StreamingProfile();
        try {
            mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_MEDIUM1)/*视频质量*/
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_HIGH1)/*音频质量*/
                    .setQuicEnable(false)//RMPT or QUIC
                    .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)//横竖屏   ENCODING_ORIENTATION.PORT 之后，播放端会观看竖屏的画面；反之
                    .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_720)
                    .setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto)//自适应码率
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
                    .setDnsManager(getMyDnsManager())
                    .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                    .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000))
                    .setPublishUrl(publishURLFromServer);//设置推流地址

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//当前竖屏；横屏SCREEN_ORIENTATION_LANDSCAPE

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        camerasetting = new CameraStreamingSetting();
        camerasetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT) // 摄像头切换,默认前置，后置是BACK
                .setContinuousFocusModeEnabled(true)//开启对焦
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)//自动对焦
                .setBuiltInFaceBeautyEnabled(true)//开启美颜
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.0f, 0.0f, 0.0f))// 磨皮，美白，红润 取值范围为[0.0f, 1.0f]
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_4_3);
        mMediaStreamingManager = new MediaStreamingManager(this, cameraPreviewFrameView, SW_VIDEO_WITH_SW_AUDIO_CODEC);

        //质量与性能
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 48 * 1024);

        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(20, 2000 * 1024, 60, StreamingProfile.H264Profile.HIGH);

        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);

        mProfile.setAVProfile(avProfile);

        mMediaStreamingManager.prepare(camerasetting, mProfile);
        mMediaStreamingManager.setStreamingStateListener(this);
        mMediaStreamingManager.setStreamingSessionListener(this);
        mMediaStreamingManager.setStreamStatusCallback(this);
        mMediaStreamingManager.setAudioSourceCallback(this);

        watermarkSetting = new WatermarkSetting(this);
        watermarkSetting.setInJustDecodeBoundsEnabled(false);
        mMediaStreamingManager.updateWatermarkSetting(watermarkSetting);
        doTopGradualEffect();
    }

    private void initGift() {
        OkHttp3Utils.getInstance(mContext).doPostJson("", null, new ObjectCallback<String>(mContext) {
            @Override
            public void onUi(String result) throws JSONException {
                Gson gson = new Gson();
                List<GiftMo> list = gson.fromJson(result, new TypeToken<List<GiftMo>>() {}.getType());
                giftList = list;
            }
            @Override
            public void onFailed(String msg) {

            }
        });
    }

    private void initCommentUi() {
        commentAdapter = new RecyclerAdapter<LiveComment>(mContext, commentData, R.layout.item_comment) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, LiveComment o) {
                TextView tvMess = holder.getView(R.id.tvMess);
                tvMess.setText(o.getMsgComment());
                holder.setText(R.id.tvUser, o.getUserName());
                //进入直播间
                if (o.getMsgTag() == Contents.HY_JOIN) {
                    tvMess.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    tvMess.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                SimpleDraweeView sdvHead = holder.getView(R.id.sdvItemHead);
                if (StringUtils.isEmpty(o.getHeadUrl())) {
                    sdvHead.setVisibility(View.GONE);
                } else {
                    sdvHead.setImageURI(o.getHeadUrl());
                    sdvHead.setVisibility(View.VISIBLE);
                }
            }
        };
        recyclerView.setAdapter(commentAdapter);
    }

    private void initUserDate() {

        llNotice.setVisibility(View.VISIBLE);

        userMess = SPUtils.instance(this).getUser();
        tvNickName.setText(userMess.getNickName());
        sdvHead.setImageURI(userMess.getHeadUrl());
        tvOnLine.setText("在线  " + 0);
        tvDzNum.setText("赞  " + userMess.getPraisedNumber());

        tvFinishLive.setText("开始直播");

    }

    /**
     * 推流成功后执行的方法
     * 1):联系环信，创建房间，
     * 2):倒计时即使，重连情况下获取上次最后断开连接的时间，继续计时
     */
    private void startFunction() {

        //开始推流
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mMediaStreamingManager != null) {
                    mMediaStreamingManager.startStreaming();

                }
            }
        }).start();

        //创建环信房间
        createHyRoom();

        initJoin();

        llNotice.setVisibility(View.GONE);//隐藏提示
        tvFinishLive.setText("结束直播");
        tvFinishLive.setBackgroundResource(R.drawable.shape_style_db_green);

    }

    private void initJoin() {
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                Log.e("HyListener", "onChatRoomDestroyed:roomId=" + roomId + ",roomName=" + roomName);

            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                if (participant.equals("系统管理员")) return;
                //有人进来
                onLineNumber++;
                tvOnLine.setText("在线 " + onLineNumber);
                setNotifyUi(new LiveComment(Contents.HY_JOIN, participant, "", "进入直播间"));
                Log.e("HyListener", "onMemberJoined:roomId=" + roomId + ",participant=" + participant);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                //用户退出
                onLineNumber--;
                tvOnLine.setText("在线 " + onLineNumber);
                Log.e("HyListener", "onMemberExited:roomId=" + roomId + ",roomName=" + roomName + ",participant=" + participant);
                setNotifyUi(new LiveComment(Contents.HY_JOIN, participant, "", "离开直播间"));
            }

            @Override
            public void onRemovedFromChatRoom(int i, String s, String s1, String s2) {
                Log.e("HyListener", "onRemovedFromChatRoom:");
            }


            @Override
            public void onMuteListAdded(final String chatRoomId, final List<String> mutes, final long expireTime) {
                Log.e("HyListener", "onMuteListAdded:");
            }

            @Override
            public void onMuteListRemoved(final String chatRoomId, final List<String> mutes) {
                Log.e("HyListener", "onMuteListRemoved:");
            }

            @Override
            public void onAdminAdded(final String chatRoomId, final String admin) {
                Log.e("HyListener", "onAdminAdded:");
            }

            @Override
            public void onAdminRemoved(final String chatRoomId, final String admin) {
                Log.e("HyListener", "onAdminRemoved:");
            }

            @Override
            public void onOwnerChanged(final String chatRoomId, final String newOwner, final String oldOwner) {
                Log.e("HyListener", "onOwnerChanged:");
            }

            @Override
            public void onAnnouncementChanged(String chatRoomId, final String announcement) {
                Log.e("HyListener", "onAnnouncementChanged:");
            }
        });
    }

    /**
     * 推流结束（主播主动结束）执行的方法
     * 大邦这无敌牛逼的需求
     * 1）：结束流
     * 2）：释放计时资源
     * 3）：释放弹幕资源
     * 4）：释放静态引用资源
     * 5）：获取总直播时间（计时器）
     * 6）：获取本次获赞数
     * 7）：获取本次关注数
     * 8）：获取本次观看数
     * 9）：获取本次礼物收益
     * 10）：获取本次成交的订单数
     */
    public void stopFunction() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().chatroomManager().destroyChatRoom(roomNumber);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Intent intent = new Intent(mContext, LiveFinishActivity.class);
        intent.putExtra("liveTime", DataUtil.formatMiss(miss));
        intent.putExtra("liveSeeNum",String.valueOf(onLineNumber));
        intent.putExtra("liveAddFansNum","0");//没做
        intent.putExtra("liveDzNum",String.valueOf(dzNum));
        intent.putExtra("liveGiftNum",String.valueOf(giftNum));
        startActivity(intent);
        finish();
    }

    /**
     * 创建环信房间
     * 后端创建
     */
    private String roomNumber;

    private void createHyRoom() {
        OkHttp3Utils.getInstance(mContext).doPostJson("", null, new ObjectCallback<String>(mContext) {
            @Override
            public void onUi(String result) throws JSONException {
                roomNumber = result;
                tvRoomNumber.setText("房间号:" + roomNumber);
                //注册消息监听
                EMClient.getInstance().chatManager().addMessageListener(msgListener);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    @OnClick({R.id.tvFinishLive, R.id.ll_goods, R.id.ivFunction, R.id.ivChangeCame})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvFinishLive:
                if (isStart) {
                    showTips();
                } else {
                    startFunction();
                }
                break;
            case R.id.ll_goods:
                List<String> test = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    test.add("数据");
                }
                BottomDialogUtil2.getInstance(this).show(R.layout.recy_no_bg, 1.5, new Conver() {
                    @Override
                    public void setView(View view) {
                        RecyclerView recycleGoods = view.findViewById(R.id.recycler_head);
                        recycleGoods.setLayoutManager(new LinearLayoutManager(mContext));
                        RecyclerAdapter adapter = new RecyclerAdapter<String>(mContext, test, R.layout.item_live_goods) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {

                            }
                        };
                        recycleGoods.setAdapter(adapter);
                    }
                });
                break;
            case R.id.ivFunction:
                LiveFunctionView.getInstance(this).showWindow(ivFunction);
                LiveFunctionView.getInstance(this).setOnclickCallBack(new LiveFunctionView.OnclickCallBack() {
                    @Override
                    public void click(View view1, int id) {
                        switch (id) {
                            //关闭美颜
                            case R.id.llCancelMy:
                                CameraStreamingSetting.FaceBeautySetting fbSetting = camerasetting.getFaceBeautySetting();
                                //磨皮
                                fbSetting.beautyLevel = 0 / 100.0f;
                                //美白
                                fbSetting.whiten = 0 / 100.0f;
                                //红润
                                fbSetting.redden = 0 / 100.0f;
                                mp = 0;
                                mb = 0;
                                hr = 0;
                                mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
                                break;
                            //设置美颜
                            case R.id.llSetMy:
                                setMy();
                                break;
                            //开启闪光
                            case R.id.llOpenLight:
                                TextView textView = view1.findViewById(R.id.tvOnOffLine);
                                if (isLight) {
                                    mMediaStreamingManager.turnLightOff();
                                    isLight = false;
                                    textView.setText("打开闪光灯");
                                } else {
                                    mMediaStreamingManager.turnLightOn();
                                    isLight = true;
                                    textView.setText("关闭闪光灯");
                                }
                                break;
                            //录屏
                            case R.id.llScreen:

                                break;
                        }
                        LiveFunctionView.getInstance(LiveActivity.this).destroy();
                    }
                });
                break;
            case R.id.ivChangeCame:
                changeCamera();
                break;
            default:
                break;
        }
    }

    private boolean isLight = false;//闪光灯打开标志

    private int mp = 0;//磨皮
    private int mb = 0;//美白
    private int hr = 0;//红润

    //设置美颜，弹窗
    private void setMy() {
        BottomDialogUtil2.getInstance(this).show(R.layout.buttom_view_seebar, 0, (Conver) view -> {
            SeekBar sbMp = view.findViewById(R.id.sbMp);
            SeekBar sbMb = view.findViewById(R.id.sbMb);
            SeekBar sbHr = view.findViewById(R.id.sbHr);
            sbMp.setProgress(mp);
            sbMb.setProgress(mb);
            sbHr.setProgress(hr);
            sbMp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    CameraStreamingSetting.FaceBeautySetting fbSetting = camerasetting.getFaceBeautySetting();
                    //磨皮
                    fbSetting.beautyLevel = progress / 100.0f;
                    mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
                    mp = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            sbMb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    CameraStreamingSetting.FaceBeautySetting fbSetting = camerasetting.getFaceBeautySetting();
                    //美白
                    fbSetting.whiten = progress / 100.0f;
                    mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
                    mb = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            sbHr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    CameraStreamingSetting.FaceBeautySetting fbSetting = camerasetting.getFaceBeautySetting();
                    //红润
                    fbSetting.redden = progress / 100.0f;
                    mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
                    hr = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        });
    }

    //弹窗提示直播间信息
    private void showTips() {
        DialogUtil.getInstance(this).show(R.layout.dialog_tip, holder -> {
            holder.findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopFunction();
                }
            });
            holder.findViewById(R.id.tvKeepLive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogUtil.getInstance(LiveActivity.this).des();
                }
            });
            TextView tvDate = holder.findViewById(R.id.tv_tips);
            tvDate.setText("你已直播时长："+DataUtil.formatMiss(miss));
            TextView tvSeeNum = holder.findViewById(R.id.tvPersonNum);
            tvSeeNum.setText("当前人数已到达："+dzNum+"人");

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaStreamingManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveFunctionView.getInstance(LiveActivity.this).destroy();
        BottomDialogUtil2.getInstance(this).dess();
        DialogUtil.getInstance(this).des();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // You must invoke pause here.
        mMediaStreamingManager.pause();
        //mediaPlayer.pause();
    }


    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        switch (streamingState) {
            case PREPARING:
                Log.e(TAG, "PREPARING");
                break;
            case READY:
                Log.e(TAG, "READY");
                if (!isStart) {
                    setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "正在连接..."));
                }
                break;
            case CONNECTING:
                Log.e(TAG, "连接中");
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "连接成功..."));
                break;
            case STREAMING:
                Log.e(TAG, "推流中");
                if (!isStart) {
                    setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "跳跳已万事具备，只欠您的表演,开始吧！"));
                    //开始计时
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            int hour = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000 / 60);
                            chronometer.setFormat("0" + String.valueOf(hour) + ":%s");
                            chronometer.start();

                            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

                                @Override
                                public void onChronometerTick(Chronometer ch) {
                                    miss++;
                                }
                            });
                        }
                    });
                    isStart = true;
                }
                break;
            case SHUTDOWN:
                Log.e(TAG, "直播中断");
                chronometer.stop();
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "跳跳中断..."));
                break;
            case IOERROR:
                Log.e(TAG, "网络连接失败");
                chronometer.stop();
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "网络连接失败..."));
                break;
            case OPEN_CAMERA_FAIL:
                Log.e(TAG, "摄像头打开失败");
                break;
            case DISCONNECTED:
                Log.e(TAG, "已经断开连接");
                chronometer.stop();
                setNotifyUi(new LiveComment(Contents.HY_SERVER, "跳跳直播", Contents.appLogo, "已经断开连接..."));
                break;
            case TORCH_INFO:
                Log.e(TAG, "开启闪光灯");
                break;
        }
    }

    @Override
    public void notifyStreamStatusChanged(StreamingProfile.StreamStatus status) {
        Log.e(TAG, "StreamStatus = " + status);
    }

    @Override
    public void onAudioSourceAvailable(ByteBuffer srcBuffer, int size, long tsInNanoTime, boolean isEof) {
    }

    @Override
    public boolean onRecordAudioFailedHandled(int code) {
        Log.i(TAG, "onRecordAudioFailedHandled");
        return false;
    }

    @Override
    public boolean onRestartStreamingHandled(int code) {
        Log.i(TAG, "onRestartStreamingHandled");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mMediaStreamingManager != null) {
                    mMediaStreamingManager.startStreaming();
                }
            }
        }).start();
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getAppManager().finishActivity(LiveActivity.class);
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        return null;
    }

    @Override
    public int onPreviewFpsSelected(List<int[]> list) {
        return -1;
    }

    //自定义评论区淡出
    private int layerId;

    public void doTopGradualEffect() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Paint mPaint = new Paint();
        // 融合器
        final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaint.setXfermode(xfermode);
        // 创造一个颜色渐变，作为聊天区顶部效果
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 100.0f, new int[]{0, Color.BLACK}, null, Shader.TileMode.CLAMP);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            // 滑动RecyclerView，渲染之后每次都会回调这个方法，就在这里进行融合
            @Override
            public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(canvas, parent, state);
                mPaint.setXfermode(xfermode);
                mPaint.setShader(linearGradient);
                canvas.drawRect(0.0f, 0.0f, parent.getRight(), 200.0f, mPaint);
                mPaint.setXfermode(null);
                canvas.restoreToCount(layerId);
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                layerId = c.saveLayer(0.0f, 0.0f, (float) parent.getWidth(), (float) parent.getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isStart) { //已经开始了
                if (DialogUtil.getInstance(this).isShow()) {
                    DialogUtil.getInstance(this).des();
                } else {
                    showTips();
                }
            } else {
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    private int mCurrentCamFacingIndex;

    private void changeCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //mCurrentCamFacingIndex = (mCurrentCamFacingIndex + 1) % CameraStreamingSetting.getNumberOfCameras();
                CameraStreamingSetting.CAMERA_FACING_ID facingId;
                if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK.ordinal()) {
                    facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
                    mCurrentCamFacingIndex = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal();
                } else if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal()) {
                    facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
                    mCurrentCamFacingIndex = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK.ordinal();
                } else {
                    facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
                }
                Log.i(TAG, "switchCamera:" + facingId);
                mMediaStreamingManager.switchCamera(facingId);
            }
        }).start();
    }

    /**
     * @author:
     * @create at: 2018/11/14  10:57
     * @Description: 防止 Dns 被劫持
     */
    private static DnsManager getMyDnsManager() {
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

}
