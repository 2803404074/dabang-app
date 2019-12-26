package com.dabangvr.play.activity.verticle;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dabangvr.live.activity.LiveBaseActivity;
import com.dabangvr.live.gift.GiftMo;
import com.dabangvr.live.gift.GiftViewBackup;
import com.dabangvr.live.gift.danmu.DanmuAdapter;
import com.dabangvr.live.gift.danmu.DanmuEntity;
import com.dabangvr.mall.activity.GoodsActivity;
import com.dabangvr.play.widget.HeartLayout;
import com.dabangvr.util.ShareUtils;
import com.dbvr.baselibrary.model.LiveGiftMo;
import com.dbvr.baselibrary.model.LiveGoods;
import com.dbvr.baselibrary.model.LiveMo;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.orzangleli.xdanmuku.DanmuContainerView;
import com.zego.zegoliveroom.constants.ZegoIM;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiongxingxing on 16/12/3.
 */
public class RoomFragment extends BaseRoomFragment {

    //评论列表
    @BindView(R.id.recycle_comment)
    RecyclerView recyclerView;
    //列表适配器
    private RecyclerAdapter commentAdapter;
    //评论数据源
    private List<ZegoRoomMessage> commentData = new ArrayList<>();

    //弹幕
    @BindView(R.id.danmuContainerView)
    DanmuContainerView danmuContainerView;

    //SVGA动画
    @BindView(R.id.sVGAImageView)
    SVGAImageView animationView;

    //打赏
    @BindView(R.id.giftView)
    GiftViewBackup giftView;

    @BindView(R.id.heart_layout)
    HeartLayout heartLayout;

    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;

    @BindView(R.id.auth_name)
    TextView authName;

    @BindView(R.id.tvHots)
    TextView tvHots;

    @BindView(R.id.tvDzNum)
    TextView tvDzNum;

    @BindView(R.id.play_follow)
    TextView tvFollow;

    //礼物列表
    private List<LiveGiftMo> giftList = new ArrayList<>();

    public LiveMo liveData;//直播对象

    public static RoomFragment newInstance(LiveMo liveMo) {
        Bundle args = new Bundle();
        args.putSerializable("liveMo",liveMo);//传入首次需要播放直播的对象
        RoomFragment fragment = new RoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_room;
    }

    @Override
    public void initView() {
        Bundle bundle = getArguments();
        if (bundle!=null){
            liveData = (LiveMo) bundle.getSerializable("liveMo");
        }
        //
        initCallBack();

        //初始化评论列表
        initCommentUi();

        //弹幕初始化
        intDanmu();

        //礼物初始化
        initGift();

        //svga初始化
        initGiftMall();

        LiveBaseActivity.instance.setNotifyUi(null,liveData);
    }

    private void initCallBack() {
        LiveBaseActivity.instance.setInterFace(new LiveInterFace() {
            @Override
            public void updateHots(int isCom) {
                tvHots.setText(String.valueOf(isCom));
            }

            @Override
            public void updateComment(int size, ArrayList data) {
                recyclerView.smoothScrollToPosition(size);
                commentAdapter.addAll(data);
            }

            @Override
            public void updateDanMu(DanmuEntity danmuEntity) {
                danmuContainerView.addDanmu(danmuEntity);
            }

            @Override
            public void updateGift(GiftMo giftMo) {
                giftView.addGift(giftMo);
            }

            @Override
            public void updateGiftMall(SVGAVideoEntity giftMo, int arg0, boolean arg1) {
                animationView.setVideoItem(giftMo);
                animationView.stepToFrame(arg0, arg1);
            }

            @Override
            public void updateDz(int num) {
                int a = Integer.parseInt(tvDzNum.getText().toString());
                int b = a+num;
                tvDzNum.setText(String.valueOf(b));
            }

            @Override
            public void updateRoom(LiveMo c) {
                //切换房间的回调
                //清空评论
                liveData = c;
//                if (c.isFollow()){
//                    tvFollow.setVisibility(View.GONE);
//                }else {
//                    tvFollow.setVisibility(View.VISIBLE);
//                }
                commentData.clear();
                commentAdapter.updateDataa(commentData);
                //设置用户信息
                authName.setText(c.getNickName());
                sdvHead.setImageURI(c.getHeadUrl());
                tvHots.setText(c.getLookNum());
                tvDzNum.setText(c.getPraseCount());

                //重新获取主播的商品列表
                getLiveGoods();
            }
        });
    }
    private List<LiveGoods>liveGoods;
    @BindView(R.id.tvGoodsNum)
    TextView tvGoodsNum;
    private void getLiveGoods() {
        Map<String,Object>map = new HashMap<>();
        map.put("roomId",liveData.getRoomId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getRoomGoodsList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                liveGoods = new Gson().fromJson(result, new TypeToken<List<LiveGoods>>() {
                }.getType());
                if (liveGoods!=null){
                    tvGoodsNum.setText(String.valueOf(liveGoods.size()));
                }
            }
            @Override
            public void onFailed(String msg) {

            }
        });
    }

    //初始化评论列表
    private void initCommentUi() {
        doTopGradualEffect(recyclerView);
        commentAdapter = new RecyclerAdapter<ZegoRoomMessage>(getContext(), commentData, R.layout.item_comment) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, ZegoRoomMessage o) {
                TextView tvMess = holder.getView(R.id.tvMess);
                holder.setText(R.id.tvUser, o.fromUserName);
                tvMess.setText(o.content);
                int mColor = R.color.colorDb3;
                //系统
                if (o.messageCategory == ZegoIM.MessageCategory.System) { mColor = R.color.colorDb3; }
                //聊天
                if (o.messageCategory == ZegoIM.MessageCategory.Chat){ mColor = R.color.colorWhite; }
                //点赞
                if (o.messageCategory == ZegoIM.MessageCategory.Like){
                    mColor = R.color.colorZi;
                    tvMess.setText("送给主播"+o.content+"个赞");
                }
                //礼物
                if (o.messageCategory == ZegoIM.MessageCategory.Gift){
                    mColor = R.color.colorOrag;
                }


                tvMess.setTextColor(getResources().getColor(mColor));
            }
        };
        recyclerView.setAdapter(commentAdapter);


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
        giftView.setViewCount(2);
        giftView.init();
    }

    //礼物相关
    private LiveGiftMo clickGift;//选中的礼物
    private RecyclerView recyclerGift;
    private RecyclerAdapter giftAdapter;
    private Random random = new Random();

    //商品相关
    private RecyclerView recyclerGoods;
    private RecyclerAdapter goodsAdapter;

    @OnClick({R.id.llComment,R.id.ivGift,R.id.ivGoods,R.id.ivLove,R.id.ivShare,R.id.sdvHead,R.id.play_follow})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.llComment:
                BottomDialogUtil2.getInstance(getActivity()).showLive(R.layout.live_input, view14 -> {
                    EditText editText = view14.findViewById(R.id.et_content_chart);
                    Switch aSwitch = view14.findViewById(R.id.switch_mirror);
                    view14.findViewById(R.id.btn_send).setOnClickListener(view1 -> {
                        if (StringUtils.isEmpty(editText.getText().toString()))return;
                        ZegoRoomMessage message = new ZegoRoomMessage();
                        message.fromUserName = userMess.getNickName();
                        if (aSwitch.isChecked()){
                            //发送弹幕消息
                            message.messageCategory = ZegoIM.MessageCategory.OtherCategory;
                            DanmuMo danmuMo = new DanmuMo();
                            danmuMo.setContent(editText.getText().toString());
                            danmuMo.setUrl(userMess.getHeadUrl());
                            String str = new Gson().toJson(danmuMo);
                            message.content = str;
                            PlayActivity.playInstance.sendMessage(message.messageCategory,str);
                        }else {
                            //发送普通消息
                            message.messageCategory = ZegoIM.MessageCategory.Chat;
                            message.content = editText.getText().toString();
                            PlayActivity.playInstance.sendMessage(message.messageCategory,message.content);
                        }
                        LiveBaseActivity.instance.setNotifyUi(message,null);
                        BottomDialogUtil2.getInstance(getActivity()).dess();
                    });
                });
                break;
            case R.id.ivGift:
                BottomDialogUtil2.getInstance(getActivity()).showLive(R.layout.dialog_gift, view15 -> {
                    TextView tvTiaoB = view15.findViewById(R.id.tvTiaoB);
                    EditText etNum = view15.findViewById(R.id.etNum);
                    tvTiaoB.setText(String.valueOf(userMess.getDiamond()));
                    recyclerGift = view15.findViewById(R.id.recycler_head);
                    recyclerGift.setNestedScrollingEnabled(false);
                    recyclerGift.setLayoutManager(new GridLayoutManager(getContext(), 4));
                    giftAdapter = new RecyclerAdapter<LiveGiftMo>(getContext(), giftList, R.layout.item_gift) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, LiveGiftMo o) {
                            holder.setImageByUrl(R.id.mivIcon, o.getGiftUrl());
                            TextView tvName = holder.getView(R.id.tvName);
                            TextView tvPrice = holder.getView(R.id.tvPrice);
                            tvName.setText(o.getGiftName());
                            tvPrice.setText(o.getGiftCoins() + "跳币");
                            if (o.isClick()) {
                                holder.itemView.setBackgroundResource(R.drawable.shape_db_stroke);
                                tvName.setTextColor(getResources().getColor(R.color.colorDb5));
                            } else {
                                holder.itemView.setBackgroundResource(R.drawable.shape_w_stroke);
                                tvName.setTextColor(getResources().getColor(R.color.textTitle));
                            }
                        }
                    };
                    giftAdapter.setOnItemClickListener((view13, position) -> {
                        clickGift = giftList.get(position);
                        for (int i = 0; i < giftList.size(); i++) {
                            if (i == position) {
                                giftList.get(i).setClick(true);
                            } else {
                                giftList.get(i).setClick(false);
                            }
                        }
                        System.out.println(""+giftList);
                        giftAdapter.updateDataa(giftList);
                    });
                    //发送打赏消息
                    view15.findViewById(R.id.tvSend).setOnClickListener(view12 -> {
                        //判断跳币是否足够
                        userMess.setDiamond(10000);
                        if (userMess.getDiamond()<clickGift.getGiftCoins() ){
                            view15.findViewById(R.id.tvTipsT).setVisibility(View.VISIBLE);
                        }else {
                            view15.findViewById(R.id.tvTipsT).setVisibility(View.GONE);
                            ZegoRoomMessage message = new ZegoRoomMessage();
                            message.fromUserName = userMess.getNickName();
                            message.messageCategory = ZegoIM.MessageCategory.Gift;
                            GiftMo giftMo = new GiftMo();

                            giftMo.setGiftNum(Integer.parseInt(etNum.getText().toString()));
                            giftMo.setUserName(userMess.getNickName());
                            giftMo.setGiftName(clickGift.getGiftName());
                            giftMo.setGiftUrl(clickGift.getGiftUrl());
                            giftMo.setUserHead(userMess.getHeadUrl());
                            //svga动画
                            if (clickGift.getGiftCoins()>=300){
                                giftMo.setSvgaName(clickGift.getTag());//svga名称
                            }
                            String str = new Gson().toJson(giftMo);
                            message.content = str;
                            //更新自己的UI
                            LiveBaseActivity.instance.setNotifyUi(message,null);
                            //发送消息
                            PlayActivity.playInstance.sendMessage(message.messageCategory,str);
                            //后端送礼逻辑
                            rewardGift(clickGift.getId(),etNum.getText().toString());
                            //销毁
                            BottomDialogUtil2.getInstance(getActivity()).dess();
                        }
                    });
                    recyclerGift.setAdapter(giftAdapter);
                });
                break;
            case R.id.ivGoods:
                if (liveGoods== null || liveGoods.size() == 0)return;
                BottomDialogUtil2.getInstance(getActivity()).show2(R.layout.recy_no_bg,2, view16 -> {
                    recyclerGoods = view16.findViewById(R.id.recycler_head);
                    recyclerGoods.setBackgroundResource(R.color.colorWhite);
                    recyclerGoods.setLayoutManager(new LinearLayoutManager(getContext()));
                    goodsAdapter = new RecyclerAdapter<LiveGoods>(getContext(),liveGoods,R.layout.item_goods) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, LiveGoods o) {
                            holder.setImageByUrl(R.id.ivContent,o.getListUrl());
                            holder.setText(R.id.tvContent,o.getName());
                            holder.setText(R.id.tvTitle,o.getTitle());
                            holder.setText(R.id.tvPrice,o.getRetailPrice());
                        }
                    };
                    recyclerGoods.setAdapter(goodsAdapter);
                    goodsAdapter.setOnItemClickListener((view17, position) -> {
                        Map<String,Object>map = new HashMap<>();
                        map.put("goodsId",liveGoods.get(position).getId());
                        map.put("roomId",liveData.getRoomId());
                        goTActivity(GoodsActivity.class,map);
                        BottomDialogUtil2.getInstance(getActivity()).dess();
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
            case R.id.ivShare:
                ShareUtils.getInstance(getContext()).startShare("跳跳传媒",liveData.getTitle(),liveData.getCoverUrl(),"www.baidu.com");
                break;
            case R.id.sdvHead:
                BottomDialogUtil2.getInstance(getActivity()).showLive(R.layout.dialog_auth_mess,view16 -> {
                    SimpleDraweeView sdvHead = view16.findViewById(R.id.sdvHead);
                    sdvHead.setImageURI(liveData.getHeadUrl());
                    TextView tvName = view16.findViewById(R.id.tvAuthName);
                    TextView dTvFollow = view16.findViewById(R.id.dTvFollow);
                    tvName.setText(liveData.getNickName());
//                    if (liveData.isFollow()){
//                        dTvFollow.setVisibility(View.GONE);
//                    }else {
//                        dTvFollow.setVisibility(View.VISIBLE);
//                    }
                    TextView tvDropNum = view16.findViewById(R.id.tvDropNum);
                    tvDropNum.setText(liveData.getJumpNo());
                    TextView tvAdd = view16.findViewById(R.id.tvAdd);
                    if (StringUtils.isEmpty(liveData.getPermanentResidence())){
                        tvAdd.setText("主播未设置常住地");
                    }else {
                        tvAdd.setText(liveData.getPermanentResidence());
                    }
                    TextView tvFollow = view16.findViewById(R.id.tvFollow);
                    TextView tvFans = view16.findViewById(R.id.tvFans);
                    TextView tvGoodsNum = view16.findViewById(R.id.tvGoodsNum);
                    tvFollow.setText(liveData.getFansNumber());
                    tvFans.setText(liveData.getFansNumber());
                    tvGoodsNum.setText(liveData.getGoodsNumber());
                });
                break;
            case R.id.play_follow:
                tvFollow.setVisibility(View.GONE);
                break;
                default:break;
        }
    }

    private void intDanmu() {
        danmuContainerView.setAdapter(new DanmuAdapter(getContext()));
    }


    /**
     * 打赏礼物
     */
    private void rewardGift(int giftId,String number) {
        Map<String,Object>map = new HashMap<>();
        map.put("giftId",giftId);
        map.put("number",number);
        map.put("anchorUserId",liveData.getUserId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.rewardGift, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {

            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),""+msg);
            }
        });
    }


    long currentTime = 0;
    int clickCount = 0;
    //500毫秒后做检查，如果没有继续点击了，发点赞消息
    public void checkAfter(final long lastTime) {
        new Handler().postDelayed(() -> {
            if (lastTime == currentTime) {
                //发送点赞消息
                ZegoRoomMessage message = new ZegoRoomMessage();
                message.fromUserName = userMess.getNickName();
                message.messageCategory = ZegoIM.MessageCategory.Like;
                message.content = String.valueOf(clickCount);
                LiveBaseActivity.instance.setNotifyUi(message,null);
                PlayActivity.playInstance.sendMessage(message.messageCategory,message.content);
                sendDz2Server(clickCount);
                clickCount = 0;
            }
        }, 1000);
    }

    /**
     * 点赞后端逻辑
     */
    private void sendDz2Server(int number){
        Map<String,Object>map = new HashMap<>();
        map.put("roomId",liveData.getRoomId());
        map.put("number",number);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.praseOnline, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
            }
            @Override
            public void onFailed(String msg) {
            }
        });
    }

    @Override
    public void initData() {
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getLiveGiftList, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                Gson gson = new Gson();
                List<LiveGiftMo> list = gson.fromJson(result, new TypeToken<List<LiveGiftMo>>() {}.getType());
                giftList = list;
            }
            @Override
            public void onFailed(String msg) {
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setUserMess();
    }
}

