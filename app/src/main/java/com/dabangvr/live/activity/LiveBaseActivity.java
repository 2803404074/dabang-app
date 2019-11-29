package com.dabangvr.live.activity;

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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.live.gift.GiftMo;
import com.dabangvr.live.gift.danmu.DanmuEntity;
import com.dabangvr.play.activity.verticle.DanmuMo;
import com.dabangvr.play.activity.verticle.LiveInterFace;
import com.dabangvr.play.activity.verticle.PlayActivityCoPy;
import com.dabangvr.play.model.LiveData;
import com.dbvr.baselibrary.model.MainMo;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.BaseActivity;
import com.google.gson.Gson;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.zego.zegoliveroom.constants.ZegoIM;
import com.zego.zegoliveroom.entity.ZegoBigRoomMessage;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by 黄仕豪 on 2019/7/03
 */

public abstract class LiveBaseActivity<T> extends BaseActivity{

    public UserMess userMess;
    public static LiveBaseActivity instance;

    private  LiveInterFace interFace;

    public void setInterFace(LiveInterFace interFace) {
        this.interFace = interFace;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        instance = this;

    }
    //自定义评论区淡出
    private int layerId;
    public void doTopGradualEffect(RecyclerView recyclerView) {
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

    private int  dataSize = 0;

    public void addAndRemoveUser(int isCom){
        interFace.updateHots(isCom);
    }

    //收到消息
    public void setNotifyUi(ZegoRoomMessage roomMessage, MainMo user) {
        //切换房间
        if (null == roomMessage){
            if (interFace!=null){
                interFace.updateRoom(user);
            }
            return;
        }
        Bundle bundle = new Bundle();
        Message message = new Message();
        switch (roomMessage.messageCategory) {
            case ZegoIM.MessageCategory.System://系统消息,用户进入退出,关注主播
                ArrayList arr = new ArrayList();
                arr.add(roomMessage);
                bundle.putStringArrayList("data", arr);
                message.what = ZegoIM.MessageCategory.System;
                break;
            case ZegoIM.MessageCategory.Chat://评论消息
                ArrayList arr2 = new ArrayList();
                arr2.add(roomMessage);
                bundle.putStringArrayList("data", arr2);
                message.what = ZegoIM.MessageCategory.Chat;
                break;
            case ZegoIM.MessageCategory.Like://点赞消息
                ArrayList arr3 = new ArrayList();
                arr3.add(roomMessage);
                bundle.putStringArrayList("data", arr3);
                bundle.putInt("num", Integer.parseInt(roomMessage.content));
                message.what = ZegoIM.MessageCategory.Like;
                break;
            case ZegoIM.MessageCategory.OtherCategory://弹幕消息
                DanmuMo danmuMo = new Gson().fromJson(roomMessage.content, DanmuMo.class);
                bundle.putString("content", danmuMo.getContent());
                bundle.putString("name", roomMessage.fromUserName);
                bundle.putString("url",danmuMo.getUrl());
                message.what = ZegoIM.MessageCategory.OtherCategory;
                break;
            case ZegoIM.MessageCategory.Gift://打赏消息
                bundle.putString("data",roomMessage.content);
                message.what = ZegoIM.MessageCategory.Gift;
                break;
            default:break;
        }

        message.setData(bundle);
        handler.sendMessage(message);
    }
    //消息处理
    Handler handler = new Handler(msg -> {

        //系统消息和评论消息 (更新评论列表)
        if (msg.what == ZegoIM.MessageCategory.System ||msg.what == ZegoIM.MessageCategory.Chat) {
            ArrayList data = msg.getData().getParcelableArrayList("data");
            dataSize++;
            interFace.updateComment(dataSize,data);
        }
        //点赞（更新评论列表，并且获取点赞数量进行累加处理）
        if (msg.what == ZegoIM.MessageCategory.Like) {
            ArrayList data = msg.getData().getParcelableArrayList("data");
            int num = msg.getData().getInt("num");
            dataSize++;
            interFace.updateComment(dataSize,data);

            interFace.updateDz(num);

        }
        //弹幕
        if (msg.what == ZegoIM.MessageCategory.OtherCategory){
            String message = msg.getData().getString("content");
            String name = msg.getData().getString("name");
            String url = msg.getData().getString("url");
            DanmuEntity danmuEntity = new DanmuEntity();
            danmuEntity.setContent(message);
            danmuEntity.setName(name);
            danmuEntity.setPortrait(url);
            danmuEntity.setType(0);
            interFace.updateDanMu(danmuEntity);
        }
        //打赏
        if (msg.what == ZegoIM.MessageCategory.Gift){
            String data =  msg.getData().getString("data");
            GiftMo giftMo = new Gson().fromJson(data,GiftMo.class);
            if (StringUtils.isEmpty(giftMo.getSvgaName())){
                interFace.updateGift(giftMo);
            }else {
                //更新svga动画
                dataSize++;
                ZegoRoomMessage roomMessage  = new ZegoRoomMessage();
                roomMessage.messageCategory = ZegoIM.MessageCategory.Gift;
                roomMessage.fromUserName  = giftMo.getUserName();
                roomMessage.content = "送了"+giftMo.getGiftNum()+"个"+giftMo.getGiftName();
                ArrayList arrayList = new ArrayList();
                arrayList.add(roomMessage);
                interFace.updateComment(dataSize,arrayList);

                loadAnimation(giftMo.getSvgaName());
            }

        }
        return false;
    });

    private void loadAnimation(String str) {
        SVGAParser parser = new SVGAParser(this);
        parser.decodeFromAssets(str, new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                interFace.updateGiftMall(videoItem,0,true);
            }
            @Override
            public void onError() {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    public void getUserMess(){
        userMess =  SPUtils.instance(this).getUser();
        Log.e("a","a");
    }
}
