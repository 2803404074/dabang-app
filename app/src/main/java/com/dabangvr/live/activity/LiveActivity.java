package com.dabangvr.live.activity;

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
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.live.widget.LiveFunctionView;
import com.dbvr.baselibrary.model.GiftMo;
import com.dbvr.baselibrary.model.LiveComment;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.other.Contents;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.DataUtil;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
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
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.WatermarkSetting;

import org.json.JSONException;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.qiniu.pili.droid.streaming.AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC;

public class LiveActivity extends BaseLiveActivity {

    private CameraStreamingSetting camerasetting;
    private WatermarkSetting watermarkSetting;

    private StreamingProfile mProfile;
    private String TAG = "StreamingByCameraActivity";

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

    private int dzNum = 0;//点赞累加
    private double giftNum = 0;//礼物收益累加

    private UserMess userMess;

    @Override
    public int setLayout() {
        return R.layout.activity_live;
    }

    @Override
    public void initUser() {
        llNotice.setVisibility(View.VISIBLE);
        userMess = SPUtils.instance(this).getUser();
        tvNickName.setText(userMess.getNickName());
        sdvHead.setImageURI(userMess.getHeadUrl());
        tvOnLine.setText("在线  " + 0);
        tvDzNum.setText("赞  " + userMess.getPraisedNumber());
        tvFinishLive.setText("开始直播");
    }

    @Override
    public void initLiveView() {
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
    }

    @Override
    public void initCommentView() {
        doTopGradualEffect();
        commentAdapter = new RecyclerAdapter<LiveComment>(getContext(), commentData, R.layout.item_comment) {
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
            }
        };
        recyclerView.setAdapter(commentAdapter);
    }

    @Override
    public void initGift() {
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getLiveGiftList, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                Gson gson = new Gson();
                List<GiftMo> list = gson.fromJson(result, new TypeToken<List<GiftMo>>() {
                }.getType());
                giftList = list;
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    /**
     * 普通评论消息回调
     * @param data
     * @param dataSize
     */
    @Override
    public void commentMessCallBack(ArrayList data, int dataSize) {
        recyclerView.smoothScrollToPosition(dataSize);
        commentAdapter.addAll(data);
    }

    /**
     * 礼物消息回调
     * @param head
     * @param uName
     * @param num
     * @param gName
     * @param money
     */
    @Override
    public void giftMessCallBack(String head, String uName, int num, String gName, int money) {

    }

    /**
     * 点赞消息回调
     */
    @Override
    public void dzMessCallBack() {

    }

    /**
     * 弹幕消息回调
     * @param head
     * @param name
     * @param content
     */
    @Override
    public void dmMessCallBack(String head, String name, String content) {

    }


    /**
     * 推流方法
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
        tvFinishLive.setBackgroundResource(R.drawable.shape_red);

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

    public void stopFunction() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().chatroomManager().destroyChatRoom(roomNumber);
                    Intent intent = new Intent(getContext(), LiveFinishActivity.class);
                    intent.putExtra("liveTime", DataUtil.formatMiss(miss));
                    intent.putExtra("liveSeeNum", String.valueOf(onLineNumber));
                    intent.putExtra("liveAddFansNum", "0");//没做
                    intent.putExtra("liveDzNum", String.valueOf(dzNum));
                    intent.putExtra("liveGiftNum", String.valueOf(giftNum));
                    startActivity(intent);
                    finish();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                        recycleGoods.setLayoutManager(new LinearLayoutManager(getContext()));
                        RecyclerAdapter adapter = new RecyclerAdapter<String>(getContext(), test, R.layout.item_live_goods) {
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
        DialogUtil.getInstance(this).show(R.layout.dialog_live_tip, holder -> {
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
            tvDate.setText("你已直播时长：" + DataUtil.formatMiss(miss));
            TextView tvSeeNum = holder.findViewById(R.id.tvPersonNum);
            tvSeeNum.setText("当前人数已到达：" + dzNum + "人");

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaStreamingManager.resume();
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

}
