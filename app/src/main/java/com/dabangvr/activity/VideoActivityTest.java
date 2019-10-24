package com.dabangvr.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.fragment.RoomFragment;
import com.dabangvr.im.Constant;
import com.dabangvr.live.gift.GiftSendModel;
import com.dabangvr.live.gift.GiftView;
import com.dabangvr.live.gift.danmu.DanmuAdapter;
import com.dabangvr.live.gift.danmu.DanmuEntity;
import com.dabangvr.live.gift.util.DataInterface;
import com.dabangvr.play.widget.HeartLayout;
import com.dabangvr.play.widget.MediaController;
import com.dabangvr.play.widget.MyPagerAdapter;
import com.dabangvr.play.widget.PlayUtils;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.model.LiveComment;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.other.Contents;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.orzangleli.xdanmuku.DanmuContainerView;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class VideoActivityTest extends BaseActivity implements PLOnErrorListener{
    public static final String TAG = "VideoActivityTest";

    private UserMess userMess;

    private String roomId;

    //更新评论内容，handle
    private final int handleMessRequestCode = 100;
    //更新打赏内容，handle
    private final int handleDsRequestCode = 200;
    //更新弹幕内容，handle
    private final int handleDmRequestCode = 300;

    @BindView(R.id.texture_view)
    PLVideoTextureView mVideoView;

    @BindView(R.id.auth_name)
    TextView tvNickName;

    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;

    @BindView(R.id.tvHots)
    TextView tvHots;

    @BindView(R.id.heart_layout)
    HeartLayout heartLayout;

    @BindView(R.id.recycle_comment)
    RecyclerView recyclerView;
    //列表适配器
    private RecyclerAdapter commentAdapter;
    //评论数据源
    private List<LiveComment> commentData = new ArrayList<>();

    //消息数量，始终将最新消息显示在recycelview的底部
    private int dataSize;

    //礼物相关控件
    @BindView(R.id.danmuContainerView)
    DanmuContainerView danmuContainerView;
    @BindView(R.id.giftView)
    GiftView giftView;


    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == handleMessRequestCode) {
                ArrayList data = msg.getData().getParcelableArrayList("data");
                dataSize++;
                recyclerView.smoothScrollToPosition(dataSize);
                commentAdapter.addAll(data);
            }

            //弹幕
            if (msg.what == handleDmRequestCode){
                LiveComment LiveComment = (LiveComment) msg.getData().getSerializable("liveComment");
                DanmuEntity danmuEntity = new DanmuEntity();
                danmuEntity.setContent(LiveComment.getMsgComment());
                danmuEntity.setPortrait(LiveComment.getHeadUrl());
                danmuEntity.setName(LiveComment.getUserName());
                danmuEntity.setType(0);
                danmuContainerView.addDanmu(danmuEntity);
            }
            //打赏
            if (msg.what == handleDsRequestCode){
                LiveComment model = (LiveComment) msg.getData().getSerializable("liveComment");
                giftView.addGift(model);
            }
        }
    };
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
                mHandler.sendMessage(message3);
                break;
            case Contents.HY_LEAVE://退出房间消息
                Bundle bundle4 = new Bundle();
                ArrayList arr4 = new ArrayList();
                arr4.add(liveComment);
                bundle4.putStringArrayList("data", arr4);
                Message message4 = new Message();
                message4.what = handleMessRequestCode;
                message4.setData(bundle4);
                mHandler.sendMessage(message4);
                break;
            case Contents.HY_SERVER://评论消息
                Bundle bundle2 = new Bundle();
                ArrayList arr2 = new ArrayList();
                arr2.add(liveComment);
                bundle2.putStringArrayList("data", arr2);
                Message message2 = new Message();
                message2.what = handleMessRequestCode;
                message2.setData(bundle2);
                mHandler.sendMessage(message2);
                break;
            case Contents.HY_COMMENT://评论消息
                Bundle bundle = new Bundle();
                ArrayList arr = new ArrayList();
                arr.add(liveComment);
                bundle.putStringArrayList("data", arr);
                Message message = new Message();
                message.what = handleMessRequestCode;
                message.setData(bundle);
                mHandler.sendMessage(message);
                break;
            case Contents.HY_DM://弹幕消息
                Bundle bundle7 = new Bundle();
                bundle7.putSerializable("liveComment", liveComment);
                Message message7 = new Message();
                message7.what = handleDmRequestCode;
                message7.setData(bundle7);
                mHandler.sendMessage(message7);
                break;
            case Contents.HY_DS://打赏消息
                Bundle bundle6 = new Bundle();
                bundle6.putSerializable("liveComment", liveComment);
                Message message6 = new Message();
                message6.what = handleDsRequestCode;
                message6.setData(bundle6);
                mHandler.sendMessage(message6);
                break;
            case Contents.HY_ORDER://下单消息
                break;
            case Contents.HY_DZ://点赞消息,收到一条累计
                Bundle bundle5 = new Bundle();
                ArrayList arr5 = new ArrayList();
                arr5.add(liveComment);
                bundle5.putStringArrayList("data", arr5);
                Message message5 = new Message();
                message5.what = handleMessRequestCode;
                message5.setData(bundle5);
                mHandler.sendMessage(message5);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        roomId = getIntent().getStringExtra("roomId");
    }

    @Override
    public int setLayout() {
        return R.layout.view_room_container;
    }

    private Random random = new Random();
    @Override
    public void initView() {
        userMess = SPUtils.instance(this).getUser();

        //初始化直播间主播信息
        initLiveMess();

        //评论列表
        doTopGradualEffect();

        //初始化评论列表
        initCommentUi();

        //初始化播放器
        initPlay();

        //初始化聊天室
        initRoom();

        //初始化礼物
        initGift();

    }

    private void initGift() {
        danmuContainerView.setAdapter(new DanmuAdapter(this));
        giftView.setViewCount(2);
        giftView.init();
    }

    private void initLiveMess() {
        sdvHead.setImageURI(getIntent().getStringExtra("headUrl"));
        tvNickName.setText(getIntent().getStringExtra("nickName"));
        tvHots.setText(getIntent().getStringExtra("lookNum"));
    }

    private void initRoom() {
        //加入聊天室
        EMClient.getInstance().chatroomManager().joinChatRoom(getIntent().getStringExtra("roomId"), new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(EMChatRoom value) {
                //加入聊天室成功
                LiveComment liveComment = new LiveComment();
                liveComment.setMsgTag(Contents.HY_JOIN);
                liveComment.setUserName(userMess.getNickName());
                liveComment.setMsgComment("进入房间");
                setNotifyUi(liveComment);
                String str = new Gson().toJson(liveComment);
                EMMessage message = EMMessage.createTxtSendMessage(str, roomId);
                //如果是群聊，设置chattype，默认是单聊
                message.setChatType(EMMessage.ChatType.GroupChat);
                //发送消息
                EMClient.getInstance().chatManager().sendMessage(message);

                //房间信息监听
                initJoin();
                //注册消息监听
                EMClient.getInstance().chatManager().addMessageListener(msgListener);
            }

            @Override
            public void onError(final int error, String errorMsg) {
                //加入聊天室失败
            }
        });
    }

    private void initPlay() {
        mVideoView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setAVOptions(initAVOptions());
        MediaController mMediaController = new MediaController(this, false, true);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setOnErrorListener(this);
        mVideoView.setVideoPath(getIntent().getStringExtra("url"));
        mVideoView.start();
    }

    @OnClick({R.id.tvTest,R.id.btn_barrage,R.id.ivLove,R.id.llComment,R.id.play_follow})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.tvTest:
                LiveComment liveComment = new LiveComment();
                liveComment.setUserName(userMess.getNickName());
                liveComment.setMsgTag(Contents.HY_DS);
                liveComment.setHeadUrl(userMess.getHeadUrl());
                liveComment.setMsgDsComment(new LiveComment.GifMo("101",R.mipmap.test,"送出10个测试",10));
                setNotifyUi(liveComment);
                break;
            case R.id.btn_barrage:
                BottomDialogUtil2.getInstance(VideoActivityTest.this).showLive(R.layout.dialog_input,new Conver() {
                    @Override
                    public void setView(View view) {
                        EditText editText = view.findViewById(R.id.et_content_chart);
                        view.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LiveComment liveComment = new LiveComment();
                                liveComment.setMsgTag(Contents.HY_DM);
                                liveComment.setUserName(userMess.getNickName());
                                liveComment.setHeadUrl(userMess.getHeadUrl());
                                liveComment.setMsgComment(editText.getText().toString());
                                goComment(liveComment);
                                BottomDialogUtil2.getInstance(VideoActivityTest.this).dess();
                            }
                        });
                    }
                });
                break;
            case R.id.ivLove:
                heartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        int rgb = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                        heartLayout.addHeart(rgb);
                    }
                });
                clickCount++;
                currentTime = System.currentTimeMillis();
                checkAfter(currentTime);
                break;
            case R.id.llComment:
                BottomDialogUtil2.getInstance(VideoActivityTest.this).showLive(R.layout.dialog_input,new Conver() {
                    @Override
                    public void setView(View view) {
                        EditText editText = view.findViewById(R.id.et_content_chart);
                        view.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LiveComment liveComment = new LiveComment();
                                liveComment.setMsgTag(Contents.HY_COMMENT);
                                liveComment.setUserName(userMess.getNickName());
                                liveComment.setMsgComment(editText.getText().toString());
                                goComment(liveComment);
                                BottomDialogUtil2.getInstance(VideoActivityTest.this).dess();
                            }
                        });
                    }
                });
                break;
            case R.id.play_follow://关注点击
                break;
                default:break;
        }
    }

    private void initCommentUi() {
        commentAdapter = new RecyclerAdapter<LiveComment>(getContext(), commentData, R.layout.item_comment) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, LiveComment o) {
                TextView tvMess = holder.getView(R.id.tvMess);
                holder.setText(R.id.tvUser, o.getUserName());
                //进入直播间
                if (o.getMsgTag() == Contents.HY_JOIN) {
                    tvMess.setTextColor(getResources().getColor(R.color.colorAccent));
                    tvMess.setText(o.getMsgComment());
                } else if (o.getMsgTag() == Contents.HY_COMMENT){//评论消息-白体字
                    tvMess.setTextColor(getResources().getColor(R.color.colorWhite));
                    tvMess.setText(o.getMsgComment());
                }else if (o.getMsgTag() == Contents.HY_DZ){//点赞消息-紫色字体
                    tvMess.setTextColor(getResources().getColor(R.color.colorZi));
                    tvMess.setText("送给主播"+o.getDzNum()+"个赞");
                }else if (o.getMsgTag() == Contents.HY_SERVER){//系统消息-系统字体
                    tvMess.setTextColor(getResources().getColor(R.color.colorDb3));
                    tvMess.setText(o.getMsgComment());
                }
            }
        };
        recyclerView.setAdapter(commentAdapter);
    }

    long currentTime = 0;
    int clickCount = 0;
    //500毫秒后做检查，如果没有继续点击了，发点赞消息
    public void checkAfter(final long lastTime) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lastTime == currentTime) {
                    //更新UI
                    LiveComment liveComment = new LiveComment();
                    liveComment.setMsgTag(Contents.HY_DZ);
                    liveComment.setUserName(userMess.getNickName());
                    liveComment.setDzNum(clickCount);
                    goComment(liveComment);
                    clickCount = 0;
                }
            }
        }, 500);
    }

    @Override
    public void initData() {

    }

    private AVOptions initAVOptions() {
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        int codec = 0;
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);
        return options;
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
                //setNotifyUi(new LiveComment(Contents.HY_JOIN, participant, "", "进入直播间"));
                Log.e("HyListener", "onMemberJoined:roomId=" + roomId + ",participant=" + participant);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                //用户退出
                Log.e("HyListener", "onMemberExited:roomId=" + roomId + ",roomName=" + roomName + ",participant=" + participant);
                //setNotifyUi(new LiveComment(Contents.HY_JOIN, participant, "", "离开直播间"));
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

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    /**
     * 开始发消息
     * 处理了自己的UI展示
     * 以及发送消息功能
     * @param liveComment
     */
    private void goComment(LiveComment liveComment){
        setNotifyUi(liveComment);
        String str = new Gson().toJson(liveComment);
        EMMessage message = EMMessage.createTxtSendMessage(str, roomId);
        //如果是群聊，设置chattype，默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
        //发送退出房间消息
        LiveComment liveComment = new LiveComment();
        liveComment.setMsgTag(Contents.HY_LEAVE);
        liveComment.setUserName(userMess.getNickName());
        liveComment.setMsgComment("离开房间");
        goComment(liveComment);
        //退出房间
        EMClient.getInstance().chatroomManager().leaveChatRoom(getIntent().getStringExtra("roomId"));
    }

    @Override
    public boolean onError(int errorCode) {
        switch (errorCode) {
            case MEDIA_ERROR_UNKNOWN:
                ToastUtil.showShort(VideoActivityTest.this,"未知错误...");
                break;
            case ERROR_CODE_OPEN_FAILED:
                ToastUtil.showShort(VideoActivityTest.this,"播放器打开失败...");
                break;
            case ERROR_CODE_IO_ERROR:
                ToastUtil.showShort(VideoActivityTest.this,"网络异常...");
                break;
            case ERROR_CODE_CACHE_FAILED:
                ToastUtil.showShort(VideoActivityTest.this,"预加载失败...");
                break;
            case ERROR_CODE_PLAYER_DESTROYED:
                ToastUtil.showShort(VideoActivityTest.this,"播放器已被销毁，需要再次 setVideoURL 或 prepareAsync...");
                break;
            default:
                ToastUtil.showShort(VideoActivityTest.this,"unknown error !...");
                break;
        }
        return true;
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
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            Log.e("HyListener", "onMessageReceived--222:" + messages.toString());
            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                Log.e("HyListener", "onMessageReceived--111:" + username);

                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                Gson gson = new Gson();
                LiveComment liveComment = gson.fromJson(txtBody.getMessage(), LiveComment.class);
                setNotifyUi(liveComment);
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

}
