package com.dabangvr.play.activity;

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
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.play.ZGPlayHelper;
import com.dabangvr.publish.ZGBaseHelper;
import com.dabangvr.publish.ZGPublishHelper;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.callback.im.IZegoIMCallback;
import com.zego.zegoliveroom.callback.im.IZegoRoomMessageCallback;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoIM;
import com.zego.zegoliveroom.entity.ZegoBigRoomMessage;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoUserState;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class VideoActivity extends BaseActivity {

    @BindView(R.id.etContent)
    EditText etContent;

    @BindView(R.id.play_view)
    TextureView playView;

    //在线人数
    @BindView(R.id.tvOnLine)
    TextView tvOnLine;

    @BindView(R.id.recycle_comment)
    RecyclerView recyclerView;
    //列表适配器
    private RecyclerAdapter commentAdapter;
    //评论数据源
    private List<ZegoRoomMessage> commentData = new ArrayList<>();
    //消息数量，始终将最新消息显示在recycelview的底部
    private int dataSize;
    //更新评论内容，handle
    private final int handleMessRequestCode = 100;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_video_jg;
    }


    @OnClick({R.id.llComment,R.id.ivGift,R.id.ivGoods,R.id.ivShare,R.id.ivClose,R.id.tvSend})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.tvSend:
                sendMessage(ZegoIM.MessageCategory.Chat,etContent.getText().toString());
                break;
        }
    }


    @Override
    public void initView() {
        doTopGradualEffect();
        commentAdapter = new RecyclerAdapter<ZegoRoomMessage>(getContext(), commentData, R.layout.item_comment) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, ZegoRoomMessage o) {

                TextView tvMess = holder.getView(R.id.tvMess);
                if (o.content == null||o.content.equals("")){
                    tvMess.setText("进入房间");
                }else {
                    tvMess.setText(o.content);
                }
                holder.setText(R.id.tvUser, o.fromUserName);

                //进入直播间
                if (o.messageCategory == ZegoIM.MessageCategory.System) {
                    tvMess.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    tvMess.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }
        };
        recyclerView.setAdapter(commentAdapter);
    }


    @Override
    public void initData() {
        loginRoom();
    }

    private String streamId = "";

    public void startPlay() {
        // 开始拉流
        boolean isPlaySuccess = ZGPlayHelper.sharedInstance().startPlaying(streamId, playView);
        if (!isPlaySuccess) {
//            tvStart.setText("拉流失败, streamID : %s" + streamId);
        }
    }

    /**
     * 第一步
     * 登陆房间
     */
    public void loginRoom() {
        ZGBaseHelper.sharedInstance().loginRoom("test-room100", ZegoConstants.RoomRole.Audience, new IZegoLoginCompletionCallback() {
            @Override
            public void onLoginCompletion(int errorCode, ZegoStreamInfo[] zegoStreamInfos) {
                if (errorCode == 0) {
                    //tvTow.setText("登陆房间成功" + zegoStreamInfos[0].streamID);
                    streamId = zegoStreamInfos[0].streamID;

                    //登陆成功后
                    //注册IM消息回调
                    initMessageCallBack();
                    //开始拉流
                    startPlay();
                } else {
                    //tvTow.setText("登陆房间失败" + errorCode);
                }
            }
        });
    }



    /**
     *  接收到消息，在此处理方法
     * @param zegoRoomMessage  消息体
     */
    private void setUiNotify(ZegoRoomMessage zegoRoomMessage){
        switch (zegoRoomMessage.messageCategory){
            //聊天
            case ZegoIM.MessageCategory.Chat:
                Bundle bundle = new Bundle();
                ArrayList arr = new ArrayList();
                arr.add(zegoRoomMessage);
                bundle.putStringArrayList("data", arr);
                Message message = new Message();
                message.what = handleMessRequestCode;
                message.setData(bundle);
                handler.sendMessage(message);
                break;
            //系统(进出入房间)
            case ZegoIM.MessageCategory.System:
                Bundle bundle2 = new Bundle();
                ArrayList arr2 = new ArrayList();
                arr2.add(zegoRoomMessage);
                bundle2.putStringArrayList("data", arr2);
                Message message2 = new Message();
                message2.what = handleMessRequestCode;
                message2.setData(bundle2);
                handler.sendMessage(message2);
                break;
            //点赞
            case ZegoIM.MessageCategory.Like:
                break;
            //礼物
            case ZegoIM.MessageCategory.Gift:
                break;
            //其他
            case ZegoIM.MessageCategory.OtherCategory:
                break;
            default:break;
        }
    }

    /**
     * @param messageType
     */
    private void sendMessage(int messageType,String mess){
        //                                            1文本
        ZGPublishHelper.sharedInstance().sendRoomMessage(1, messageType, mess, new IZegoRoomMessageCallback() {
            @Override
            public void onSendRoomMessage(int i, String s, long l) {
            }
        });
    }

    private void initMessageCallBack() {
        //IM回调
        ZGPublishHelper.sharedInstance().setIMCallback(new IZegoIMCallback() {
            @Override
            public void onUserUpdate(ZegoUserState[] zegoUserStates, int i) {

            }

            @Override
            public void onRecvRoomMessage(String s, ZegoRoomMessage[] zegoRoomMessages) {
                //接收消息回掉
                setUiNotify(zegoRoomMessages[0]);
//                ToastUtil.showShort(getContext(),"收到消息："+zegoRoomMessages[0].content);
            }

            @Override
            public void onUpdateOnlineCount(String s, int i) {
                tvOnLine.setText(String.valueOf(i));
            }

            @Override
            public void onRecvBigRoomMessage(String s, ZegoBigRoomMessage[] zegoBigRoomMessages) {

            }
        });
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
    protected void onDestroy() {

        // 页面退出时释放sdk (这里开发者无需参考，这是根据自己业务需求来决定什么时候释放)
        //ZGBaseHelper.sharedInstance().unInitZegoSDK();

        // 停止所有的推流和拉流后，才能执行 logoutRoom
        ZGPlayHelper.sharedInstance().stopPlaying("test-room100");

        // 当用户退出界面时退出登录房间
        ZGBaseHelper.sharedInstance().loginOutRoom();
        super.onDestroy();
    }
}
