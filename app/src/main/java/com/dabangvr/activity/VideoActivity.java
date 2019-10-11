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

            }
        };
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void initData() {

    }
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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
