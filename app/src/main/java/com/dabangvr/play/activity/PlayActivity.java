package com.dabangvr.play.activity;

import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.activity.MyDropActivity;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.application.MyApplication;
import com.dabangvr.im.MyAnimatorUtil;
import com.dabangvr.live.gift.GiftView;
import com.dabangvr.live.gift.danmu.DanmuAdapter;
import com.dabangvr.live.gift.danmu.DanmuEntity;
import com.dabangvr.play.widget.HeartLayout;
import com.dabangvr.play.widget.MediaController;
import com.dbvr.baselibrary.model.GiftMo;
import com.dbvr.baselibrary.model.LiveComment;
import com.dbvr.baselibrary.model.OrderGoodsList;
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
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.orzangleli.xdanmuku.DanmuContainerView;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class PlayActivity extends BaseActivity implements PLOnErrorListener, PLOnInfoListener {
    public static final String TAG = "PlayActivity";

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

    @BindView(R.id.play_follow)
    TextView playFollow;

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

    private List<GiftMo> giftList = new ArrayList<>();

    //消息数量，始终将最新消息显示在recycelview的底部
    private int dataSize;

    //开始直播标志
    private volatile boolean isLiveStart = false;

    //礼物相关控件
    @BindView(R.id.danmuContainerView)
    DanmuContainerView danmuContainerView;
    @BindView(R.id.giftView)
    GiftView giftView;

    @BindView(R.id.llGoodsView)
    LinearLayout llGoodsView;
    private MyAnimatorUtil animatorUtil;

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
            case Contents.HY_DS_MAX://打赏消息
                loadAnimation(liveComment.getMsgDsComment().getGiftId());
                Bundle bundle8 = new Bundle();
                ArrayList arr8 = new ArrayList();
                arr8.add(liveComment);
                bundle8.putStringArrayList("data", arr8);
                Message message8 = new Message();
                message8.what = handleMessRequestCode;
                message8.setData(bundle8);
                mHandler.sendMessage(message8);

                break;
            case Contents.HY_ORDER://下单消息
                break;
            case Contents.HY_COLLECTION://关注消息
                Bundle bundle9 = new Bundle();
                ArrayList arr9 = new ArrayList();
                arr9.add(liveComment);
                bundle9.putStringArrayList("data", arr9);
                Message message9 = new Message();
                message9.what = handleMessRequestCode;
                message9.setData(bundle9);
                mHandler.sendMessage(message9);
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

    private boolean isFollow;//是否已经关注
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
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

        //炫酷礼物初始化
        initGiftMall();

        //如果是购物直播，初始化直播的商品，并且初始化商品弹出的视图
        initGoodsView();

    }

    /**
     * 弹出商品的业务
     * 30,60,600,1200,1800
     */
    @BindView(R.id.ivContent)
    ImageView ivContent;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    private int second;
    private int goodsPosition;
    private boolean isLoad = true;
    private List<OrderGoodsList>goodsLists ;
    private void initGoodsView() {
        goodsLists = new ArrayList<>();
        OrderGoodsList goodsList1 = new OrderGoodsList();
        goodsList1.setChartUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573042072724&di=1f749c977b80643beb33ced4115ab456&imgtype=0&src=http%3A%2F%2Fimg.yzt-tools.com%2F20190903%2Fe9e365a267fd77e2946713a7fb8e5645.jpg");
        goodsList1.setGoodsName("超级无敌鱿鱼买一送一");
        goodsList1.setRetailPrice("100");

        OrderGoodsList goodsList2 = new OrderGoodsList();
        goodsList2.setChartUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573042072723&di=a1aa9886f094fca2aa114be2afaeeaeb&imgtype=0&src=http%3A%2F%2Fimg3.99114.com%2Fgroup1%2FM00%2F0B%2FBA%2FwKgGS1jCVxCAYpkGAAEKQI-Nho4684_600_600.jpg");
        goodsList2.setGoodsName("超级无敌龙虾买一送一");
        goodsList2.setRetailPrice("200");

        OrderGoodsList goodsList3 = new OrderGoodsList();
        goodsList3.setChartUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573042072723&di=d2188544fe068e972b5324ede2fd9d42&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fp%2F20140423%2F20140423140918-202940451.jpg");
        goodsList3.setGoodsName("海鲜粥买一送一");
        goodsList3.setRetailPrice("300");

        OrderGoodsList goodsList4 = new OrderGoodsList();
        goodsList4.setChartUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573042072707&di=98a2f3727340bdd8f5ffe7b8125b1fc7&imgtype=0&src=http%3A%2F%2Fimg011.hc360.cn%2Fhb%2FMTQ3MTg2MzM3OTYyMS0xNzQ5MjE3ODc2.jpg");
        goodsList4.setGoodsName("海鱼买一送一");
        goodsList4.setRetailPrice("400");

        OrderGoodsList goodsList5 = new OrderGoodsList();
        goodsList5.setChartUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573042072706&di=a30e6987b8099473f6ec4323a08434d5&imgtype=0&src=http%3A%2F%2Fbpic.ooopic.com%2F16%2F03%2F38%2F16033804-ab6adc74e98f89ac37a75898ef1e2702.jpg");
        goodsList5.setGoodsName("海螺买一送一");
        goodsList5.setRetailPrice("500");

        goodsLists.add(goodsList1);
        goodsLists.add(goodsList2);
        goodsLists.add(goodsList3);
        goodsLists.add(goodsList4);
        goodsLists.add(goodsList5);

        isLiveStart = true;
        animatorUtil = new MyAnimatorUtil(getContext(), llGoodsView);
        new Thread(()->{
            try {
                while (isLoad){//开始直播后，一直循环弹出商品
                    Thread.sleep(1000);
                    if (second == 30 ||
                            second == 60 ||
                            second == 300||
                            second == 600||
                            second == 1200||
                            second == 1800){
                        if (goodsPosition>goodsLists.size()){
                            goodsPosition = 0;
                        }
                        runOnUiThread(()->{
                            Glide.with(MyApplication.getInstance()).load(goodsLists.get(goodsPosition).getChartUrl()).into(ivContent);
                            });
                        runOnUiThread(()->{ tvContent.setText(goodsLists.get(goodsPosition).getGoodsName());});
                        runOnUiThread(()->{ tvTitle.setText(goodsLists.get(goodsPosition).getGoodsName()); });
                        runOnUiThread(()->{ tvPrice.setText(goodsLists.get(goodsPosition).getRetailPrice()); });

                        runOnUiThread(()->{ animatorUtil.startAnimatorx(280); });
                        Thread.sleep(10000);
                        runOnUiThread(()->{ animatorUtil.stopAnimatorx(280); });
                        goodsPosition++;
                    }
                    if (second==1800){
                        isLoad = false;
                    }
                    second++;

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initGiftMall() {
        animationView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {
                Log.e("aaaaaa","onPause~~~~");
            }

            @Override
            public void onFinished() {
                Log.e("aaaaaa","onFinished~~~~");
            }

            @Override
            public void onRepeat() {
                animationView.stopAnimation();
            }

            @Override
            public void onStep(int frame, double percentage) {

            }
        });
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

        //是否已经关注
        isFollow = getIntent().getBooleanExtra("isFollow",false);
        if (isFollow){
            playFollow.setText("已关注");
            playFollow.setBackgroundResource(R.drawable.shape_gray_w);
            playFollow.setTextColor(getResources().getColor(R.color.textTitle));
        }else {
            playFollow.setText("关注");
            playFollow.setBackgroundResource(R.drawable.shape_style_green_blue);
            playFollow.setTextColor(getResources().getColor(R.color.white));
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            if (resultCode == 99){
                //充值后返回
                int diamond = data.getIntExtra("diamond",0);
                userMess.setDiamond(diamond);
            }
        }
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


    private RecyclerAdapter giftAdapter;
    private String giftName;//选中礼物的名字
    private String giftUrl;//选中礼物的图片地址
    private int giftPrice;//礼物价钱
    private int giftId;//选中礼物的ID

    @OnClick({R.id.btn_barrage,R.id.ivLove,R.id.llComment,R.id.play_follow,R.id.ivGift})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.btn_barrage:
                BottomDialogUtil2.getInstance(PlayActivity.this).showLive(R.layout.dialog_input, view16 -> {
                    EditText editText = view16.findViewById(R.id.et_content_chart);
                    view16.findViewById(R.id.btn_send).setOnClickListener(view161 -> {
                        LiveComment liveComment = new LiveComment();
                        liveComment.setMsgTag(Contents.HY_DM);
                        liveComment.setUserName(userMess.getNickName());
                        liveComment.setHeadUrl(userMess.getHeadUrl());
                        liveComment.setMsgComment(editText.getText().toString());
                        goComment(liveComment);
                        BottomDialogUtil2.getInstance(PlayActivity.this).dess();
                    });
                });
                break;
            case R.id.ivLove:
                heartLayout.post(() -> {//new Runable
                    int rgb = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                    heartLayout.addHeart(rgb);
                });
                clickCount++;
                currentTime = System.currentTimeMillis();
                checkAfter(currentTime);
                break;
            case R.id.llComment:
                BottomDialogUtil2.getInstance(PlayActivity.this).showLive(R.layout.dialog_input, view14 -> {
                    EditText editText = view14.findViewById(R.id.et_content_chart);
                    view14.findViewById(R.id.btn_send).setOnClickListener(view1 -> {
                        LiveComment liveComment1 = new LiveComment();
                        liveComment1.setMsgTag(Contents.HY_COMMENT);
                        liveComment1.setUserName(userMess.getNickName());
                        liveComment1.setMsgComment(editText.getText().toString());
                        goComment(liveComment1);
                        BottomDialogUtil2.getInstance(PlayActivity.this).dess();
                    });
                });
                break;
            case R.id.play_follow://关注点击
                followFunction();
                break;
            case R.id.ivGift://弹出礼物视图
                BottomDialogUtil2.getInstance(PlayActivity.this).showLive(R.layout.dialog_gift, view15 -> {
                    TextView tvTiaoB = view15.findViewById(R.id.tvTiaoB);
                    tvTiaoB.setText(String.valueOf(userMess.getDiamond()));
                    EditText editText = view15.findViewById(R.id.etNumber);
                    view15.findViewById(R.id.tvGoCz).setOnClickListener((view1)->{goTActivityForResult(MyDropActivity.class,null,100);});
                    view15.findViewById(R.id.tvSend).setOnClickListener(view12 -> {
                        if (Integer.parseInt(editText.getText().toString())>99){
                            ToastUtil.showShort(getContext(),"赠送数量不能超过99个~");
                            return;
                        }
                        if (Integer.parseInt(editText.getText().toString())<1){
                            ToastUtil.showShort(getContext(),"赠送数量不能少于1个~");
                            return;
                        }
                        if (StringUtils.isEmpty(giftUrl)){
                            ToastUtil.showShort(getContext(),"选择你要送的礼物");
                            return;
                        }
                        int price = Integer.parseInt(editText.getText().toString())*giftPrice;
                        if (price>userMess.getDiamond()){
                            ToastUtil.showShort(getContext(),"跳币不足");
                            return;
                        }

                        //送礼
                        setLoaddingView(true);
                        rewardGiftFunction(editText.getText().toString());


                        BottomDialogUtil2.getInstance(PlayActivity.this).dess();
                    });
                    RecyclerView recyclerView = view15.findViewById(R.id.recycler_head);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
                    giftAdapter = new RecyclerAdapter<GiftMo>(getContext(),giftList,R.layout.item_gift) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, GiftMo o) {
                                holder.setImageByUrl(R.id.mivIcon,o.getGiftUrl());
                                holder.setText(R.id.tvName,o.getGiftName());
                                holder.setText(R.id.tvPrice,o.getGiftCoins()+"跳币");

                                if (o.isClick()){
                                    holder.itemView.setBackgroundResource(R.drawable.shape_w_stroke);
                                }else {
                                    holder.itemView.setBackgroundResource(R.drawable.shape_white);
                                }
                        }
                    };
                    recyclerView.setAdapter(giftAdapter);
                    giftAdapter.setOnItemClickListener((view13, position) -> {
                        giftId = giftList.get(position).getId();
                        giftName = giftList.get(position).getGiftName();
                        if (giftList.get(position).getGiftCoins()>=300){
                            giftUrl = giftList.get(position).getTag();
                            Log.e("giftaaa",giftUrl);
                        }else {
                            giftUrl = giftList.get(position).getGiftUrl();
                        }

                        giftPrice = giftList.get(position).getGiftCoins();
                        for (int i = 0; i < giftList.size(); i++) {
                            if (i==position){
                                giftList.get(position).setClick(true);
                                continue;
                            }
                            giftList.get(i).setClick(false);
                        }
                        giftAdapter.updateDataa(giftList);
                    });
                });
                break;
                default:break;
        }
    }

    private void followFunction() {
        setLoaddingView(true);
        Map<String,Object>map = new HashMap<>();
        map.put("fansUserId",getIntent().getIntExtra("userId",0));
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.updateFans, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result){
                if (isFollow){
                    playFollow.setText("关注");
                    playFollow.setBackgroundResource(R.drawable.shape_style_green_blue);
                    playFollow.setTextColor(getResources().getColor(R.color.white));
                }else {
                    playFollow.setText("已关注");
                    playFollow.setBackgroundResource(R.drawable.shape_gray_w);
                    playFollow.setTextColor(getResources().getColor(R.color.lsq_color_red));

                    LiveComment liveComment = new LiveComment();
                    liveComment.setMsgTag(Contents.HY_COLLECTION);
                    liveComment.setUserName(userMess.getNickName());
                    liveComment.setMsgComment("关注了主播");
                    goComment(liveComment);
                }
                isFollow = !isFollow;
                setLoaddingView(false);
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),msg);
                setLoaddingView(false);
            }
        });
    }

    /**
     * 打赏礼物的方法（后端）
     *
     */
    private void rewardGiftFunction(String number){

        Map<String,Object>map = new HashMap<>();
        map.put("giftId",giftId);
        map.put("number",number);
        map.put("anchorUserId",getIntent().getIntExtra("userId",0));
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.rewardGift, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result){
                int lastPrice = userMess.getDiamond() - giftPrice*Integer.parseInt(number);
                userMess.setDiamond(lastPrice);
                SPUtils.instance(getContext()).putUser(userMess);
                Log.e("hahaha","成功："+result);
                if (giftPrice>=300){//贵重礼物，全屏展示
                    loadAnimation(giftUrl);
                    LiveComment liveComment = new LiveComment();
                    liveComment.setMsgTag(Contents.HY_DS_MAX);
                    LiveComment.GifMo gifMo = new LiveComment.GifMo();
                    gifMo.setGiftId(giftUrl);//posche.svga
                    gifMo.setGiftName(giftName);
                    liveComment.setMsgDsComment(gifMo);
                    liveComment.setUserName(userMess.getNickName());
                    goComment(liveComment);
                }else {
                    LiveComment liveComment = new LiveComment();
                    liveComment.setUserName(userMess.getNickName());
                    liveComment.setMsgTag(Contents.HY_DS);
                    liveComment.setHeadUrl(userMess.getHeadUrl());
                    liveComment.setMsgDsComment(new LiveComment.GifMo(
                            giftUrl,/*礼物url*/
                            "送出"+number+"个"+giftName,/*礼物说明*/
                            Integer.parseInt(number)));/*礼物数量*/
                    goComment(liveComment);
                }

                setLoaddingView(false);
            }

            @Override
            public void onFailed(String msg) {
                Log.e("hahaha",""+msg);
                ToastUtil.showShort(getContext(),msg);
                setLoaddingView(false);
            }
        });
    }
    /**
     * 资源名称
     */
    private void loadAnimation(String str) {
        SVGAParser parser = new SVGAParser(this);
        parser.decodeFromAssets(str, new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                animationView.setVideoItem(videoItem);
                animationView.stepToFrame(0, true);
            }
            @Override
            public void onError() {

            }
        });
    }
    @BindView(R.id.sVGAImageView)
    SVGAImageView animationView;

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
                }else if (o.getMsgTag() == Contents.HY_DS_MAX){//点赞消息-紫色字体
                    tvMess.setTextColor(getResources().getColor(R.color.colorOrag));
                    tvMess.setText("送给主播-----"+o.getMsgDsComment().getGiftName());
                }else if (o.getMsgTag() == Contents.HY_SERVER){//系统消息-系统字体
                    tvMess.setTextColor(getResources().getColor(R.color.colorDb3));
                    tvMess.setText(o.getMsgComment());
                }else if (o.getMsgTag() == Contents.HY_LEAVE){
                    tvMess.setTextColor(getResources().getColor(R.color.color_e2e2e2));
                    tvMess.setText(o.getMsgComment());
                }else if (o.getMsgTag() == Contents.HY_COLLECTION){
                    tvMess.setTextColor(getResources().getColor(R.color.text4));
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
        new Handler().postDelayed(() -> {
            if (lastTime == currentTime) {
                //更新UI
                LiveComment liveComment = new LiveComment();
                liveComment.setMsgTag(Contents.HY_DZ);
                liveComment.setUserName(userMess.getNickName());
                liveComment.setDzNum(clickCount);
                goComment(liveComment);
                clickCount = 0;
            }
        }, 500);
    }

    @Override
    public void initData() {
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getLiveGiftList, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                Gson gson = new Gson();
                List<GiftMo> list = gson.fromJson(result, new TypeToken<List<GiftMo>>() {}.getType());
                giftList = list;
            }
            @Override
            public void onFailed(String msg) {
            }
        });
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
        message.setChatType(EMMessage.ChatType.ChatRoom);
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
        BottomDialogUtil2.getInstance(this).dess();
    }

    @Override
    public boolean onError(int errorCode) {
        switch (errorCode) {
            case MEDIA_ERROR_UNKNOWN:
                ToastUtil.showShort(PlayActivity.this,"未知错误...");
                break;
            case ERROR_CODE_OPEN_FAILED:
                ToastUtil.showShort(PlayActivity.this,"播放器打开失败...");
                break;
            case ERROR_CODE_IO_ERROR:
                ToastUtil.showShort(PlayActivity.this,"加载直播中...");
                break;
            case ERROR_CODE_CACHE_FAILED:
                ToastUtil.showShort(PlayActivity.this,"预加载失败...");
                break;
            case ERROR_CODE_PLAYER_DESTROYED:
                ToastUtil.showShort(PlayActivity.this,"播放器已被销毁，需要再次 setVideoURL 或 prepareAsync...");
                break;
            default:
                ToastUtil.showShort(PlayActivity.this,"unknown error !...");
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
                Log.e("HyListenerx", "onMessageReceived--111:" + username);

                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                String str = StringUtils.removeStr(txtBody.getMessage());
                Gson gson = new Gson();
                LiveComment liveComment = gson.fromJson(str, LiveComment.class);
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

    @Override
    public void onInfo(int i, int i1) {
        if (i==MEDIA_INFO_CONNECTED){//连接成功
            isLiveStart = true;
        }
    }
}
