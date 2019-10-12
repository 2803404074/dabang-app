package com.dabangvr.publish.activity;

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
import android.widget.Chronometer;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.publish.ZGBaseHelper;
import com.dabangvr.publish.ZGConfigHelper;
import com.dabangvr.publish.ZGPublishHelper;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.callback.IZegoResponseCallback;
import com.zego.zegoliveroom.callback.im.IZegoIMCallback;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoIM;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoBigRoomMessage;
import com.zego.zegoliveroom.entity.ZegoPublishStreamQuality;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoUserState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 即构测试推流
 */
public class PublishActivity extends BaseActivity {

    @BindView(R.id.preview)
    TextureView preview;

    //主播头像
    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;
    //主播昵称
    @BindView(R.id.tvNickName)
    TextView tvNickName;
    //房间号
    @BindView(R.id.tvRoomNumber)
    TextView tvRoomNumber;
    //直播时间计时
    @BindView(R.id.charCounter)
    Chronometer chronometer;
    //点赞数量
    @BindView(R.id.tvDzNum)
    TextView tvDzNum;
    //在线人数
    @BindView(R.id.tvOnLine)
    TextView tvOnLine;
    //卖的商品数
    @BindView(R.id.tvGoodsNum)
    TextView tvGoodsNum;

    //评论列表
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
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_publish;
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


    @Override
    public void initView() {
        //评论列表淡出淡入
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

    @OnClick({R.id.ivFunction,R.id.ivChangeCame,R.id.ivGoods,R.id.tvFinishLive})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivFunction:

                break;
            case R.id.ivChangeCame:

                break;
            case R.id.ivGoods:

                break;
            case R.id.tvFinishLive:

                break;
        }
    }
    // 开始推流
    private void startPublish() {
        ZGPublishHelper.sharedInstance().startPublishing("test-stream100", "test-标题", ZegoConstants.PublishFlag.JoinPublish);
    }

    @Override
    public void initData() {
        //(1)初始化SDK（application已初始化）
        loginRoom();
    }

    private void initCallBack() {
        //IM回调
        ZGPublishHelper.sharedInstance().setIMCallback(new IZegoIMCallback() {
            @Override
            public void onUserUpdate(ZegoUserState[] zegoUserStates, int i) {

            }

            @Override
            public void onRecvRoomMessage(String s, ZegoRoomMessage[] zegoRoomMessages) {
                //接收消息回掉
                setUiNotify(zegoRoomMessages[0]);
            }

            @Override
            public void onUpdateOnlineCount(String s, int i) {
                tvOnLine.setText(String.valueOf(i));
            }

            @Override
            public void onRecvBigRoomMessage(String s, ZegoBigRoomMessage[] zegoBigRoomMessages) {

            }
        });

        // 设置推流回调
        ZGPublishHelper.sharedInstance().setPublisherCallback(new IZegoLivePublisherCallback() {
            // 推流回调文档说明: <a>https://doc.zego.im/API/ZegoLiveRoom/Android/html/index.html</a>
            @Override
            public void onPublishStateUpdate(int errorCode, String streamID, HashMap<String, Object> hashMap) {
                // 推流状态更新，errorCode 非0 则说明推流失败
                // 推流常见错误码请看文档: <a>https://doc.zego.im/CN/308.html</a>
                if (errorCode == 0) {
                   // tvThree.setText("预览监听,推流成功");
                } else {
                    //tvThree.setText("推流失败" + errorCode);
                }
            }

            @Override
            public void onJoinLiveRequest(int i, String s, String s1, String s2) {
                /**
                 * 房间内有人申请加入连麦时会回调该方法
                 * 观众端可通过 {@link com.zego.zegoliveroom.ZegoLiveRoom#requestJoinLive(IZegoResponseCallback)}
                 *  方法申请加入连麦
                 * **/
            }

            @Override
            public void onPublishQualityUpdate(String s, ZegoPublishStreamQuality zegoPublishStreamQuality) {
                /**
                 * 推流质量更新, 回调频率默认3秒一次
                 * 可通过 {@link com.zego.zegoliveroom.ZegoLiveRoom#setPublishQualityMonitorCycle(long)} 修改回调频率
                 */
            }

            @Override
            public AuxData onAuxCallback(int i) {
                // aux混音，可以将外部音乐混进推流中。类似于直播中添加伴奏，掌声等音效
                // 另外还能用于ktv场景中的伴奏播放
                // 想深入了解可以进入进阶功能中的-mixing。
                // <a>https://doc.zego.im/CN/253.html</a> 文档中有说明
                return null;
            }

            @Override
            public void onCaptureVideoSizeChangedTo(int width, int height) {
                // 当采集时分辨率有变化时，sdk会回调该方法
            }

            @Override
            public void onMixStreamConfigUpdate(int i, String s, HashMap<String, Object> hashMap) {
                // 混流配置更新时会回调该方法。
            }

            @Override
            public void onCaptureVideoFirstFrame() {
                // 当SDK采集摄像头捕获到第一帧时会回调该方法

            }

            @Override
            public void onCaptureAudioFirstFrame() {
                // 当SDK音频采集设备捕获到第一帧时会回调该方法
            }
        });

        //开始推流
        startPublish();
    }

    /**
     * 第一步登陆房间
     */
    public void loginRoom() {
        ZGBaseHelper.sharedInstance().loginRoom("test-room100", ZegoConstants.RoomRole.Anchor, new IZegoLoginCompletionCallback() {
            @Override
            public void onLoginCompletion(int errorCode, ZegoStreamInfo[] zegoStreamInfos) {
                if (errorCode == 0) {
                    //tvTow.setText("登陆房间成功");
                    //第二部开始预览
                    startPreView();
                } else {
                    //tvTow.setText("登陆房间失败" + errorCode);
                }
            }
        });
    }

    /**
     * 预览
     */
    private void startPreView() {
        ZGConfigHelper.sharedInstance().enableCamera(true);
        // 调用sdk 开始预览接口 设置view 启用预览
        ZGPublishHelper.sharedInstance().startPreview(preview);

        //第三步，设置相关回掉
        initCallBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        ZGPublishHelper.sharedInstance().stopPreviewView();
        ZGPublishHelper.sharedInstance().stopPublishing();
        // 当用户退出界面时退出登录房间
        ZGBaseHelper.sharedInstance().loginOutRoom();
        super.onDestroy();
    }
}
