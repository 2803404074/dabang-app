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
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.adapter.SlideAdapter;
import com.dabangvr.ui.VideoPlayRecyclerView;
import com.dbvr.baselibrary.model.LiveComment;
import com.dbvr.baselibrary.other.Contents;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

public class VideoActivity extends BaseActivity{

    @BindView(R.id.recycler_video)
    VideoPlayRecyclerView recyclerView;
    @BindView(R.id.loading_view)
    LinearLayout loadingView;

    private SlideAdapter adapter;

    private List<String>mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_video;
    }


    private RecyclerView recyclerComment; //评论列表
    private RecyclerAdapter commentAdapter;//列表适配器
    private List<LiveComment> commentData = new ArrayList<>();//评论数据源
    @Override
    public void initView() {
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194126.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194114.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194126.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194114.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194126.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194114.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194126.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194114.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194126.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194114.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194126.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194114.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194126.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194114.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191001194114.mp4");

        adapter = new SlideAdapter<String>(getContext(),loadingView,mData,R.layout.item_live) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String s) {
                recyclerComment = holder.getView(R.id.recycle_comment);
                doTopGradualEffect();//淡出效果列表
                //注册
                //注册消息监听
                EMClient.getInstance().chatManager().addMessageListener(msgListener);
                commentAdapter = new RecyclerAdapter<LiveComment>(getContext(),commentData,R.layout.item_live_comment) {
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
                recyclerComment.setAdapter(commentAdapter);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            try {
                                setNotifyUi(new LiveComment(Contents.HY_COMMENT, "测试", "测试头像", "测试内容"));
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();


                //初始化房间人员信息
                TextView tvHotsSize = holder.getView(R.id.auth_fanse);
                initJoin(tvHotsSize);
            }
        };
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void initData() {

    }

    //房间状态消息
    private int onLineNumber;//在线人数
    private String roomNumber;//房间号，通过房间接收到的消息，哪个房间的消息就更新哪个房间的UI
    private void initJoin(TextView tvHotsSize) {
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
                tvHotsSize.setText("在线 " + onLineNumber);
                setNotifyUi(new LiveComment(Contents.HY_JOIN, participant, "", "进入直播间"));
                Log.e("HyListener", "onMemberJoined:roomId=" + roomId + ",participant=" + participant);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                //用户退出
                onLineNumber--;
                tvHotsSize.setText("在线 " + onLineNumber);
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

    //评论弹幕礼物消息
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
                if (username.equals(roomNumber)){

                }
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


    private final int handleMessRequestCode = 100; //更新评论内容，handle
    private int dataSize;//消息数量，始终将最新消息显示在recycelview的底部
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == handleMessRequestCode) {
                ArrayList data = msg.getData().getParcelableArrayList("data");
                dataSize++;
                recyclerComment.smoothScrollToPosition(dataSize);
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
                    }
                });
                break;
            case Contents.HY_ORDER://下单消息
                break;
            case Contents.HY_DZ://点赞消息
                //动画展示
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(VideoActivity.class);
        if (null!=adapter.getmVideoView()){
            adapter.getmVideoView().stopPlayback();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null!=adapter.getmVideoView()){
            adapter.getmVideoView().stopPlayback();
        }
    }

    //自定义评论区淡出
    private int layerId;
    public void doTopGradualEffect() {
        recyclerComment.setLayoutManager(new LinearLayoutManager(this));
        Paint mPaint = new Paint();
        // 融合器
        final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaint.setXfermode(xfermode);
        // 创造一个颜色渐变，作为聊天区顶部效果
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 100.0f, new int[]{0, Color.BLACK}, null, Shader.TileMode.CLAMP);

        recyclerComment.addItemDecoration(new RecyclerView.ItemDecoration() {
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
}
