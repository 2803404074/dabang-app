package com.dabangvr.play.activity.verticle;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.live.activity.LiveBaseActivity;
import com.dbvr.baselibrary.model.LiveMo;
import com.dbvr.baselibrary.model.MainMo;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback;
import com.zego.zegoliveroom.callback.im.IZegoIMCallback;
import com.zego.zegoliveroom.constants.ZegoIM;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;
import com.zego.zegoliveroom.entity.ZegoBigRoomMessage;
import com.zego.zegoliveroom.entity.ZegoPlayStreamQuality;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoUserState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

import static com.zego.zegoliveroom.constants.ZegoConstants.RoomRole.Audience;

public class PlayActivity extends LiveBaseActivity {

    public static PlayActivity playInstance;

    @BindView(R.id.view_pager)
    VerticalViewPager mViewPager;
    private PlayPageAdapter playPageAdapter;
    private RelativeLayout mRoomContainer;
    private FrameLayout mFragmentContainer;

    private ZegoLiveRoom zegoLiveRoom;
    private TextureView textureView;

    private RoomFragment mRoomFragment;

    private List<LiveMo>mData = new ArrayList<>();

    public String getSteamID() {
        return steamID;
    }

    @Override
    public int setLayout() {
        return R.layout.activity_play;
    }

    private int mCurrentItem;
    private int mRoomId = -1;
    private LiveMo liveMo;
    @Override
    public void initView() {
        playInstance = this;
        userMess = UserHolper.getUserHolper(this).getUserMess();
        MainMo mainMo = (MainMo) getIntent().getSerializableExtra("list");
        //将跳转过来的直播对象放入直播列表中
        liveMo = new LiveMo();
        liveMo.setCoverUrl(mainMo.getCoverUrl());
        liveMo.setFansNumber(mainMo.getFansNumber());
        liveMo.setGoodsNumber(mainMo.getGoodsNumber());
        liveMo.setHeadUrl(mainMo.getHeadUrl());
        liveMo.setId(mainMo.getId());
        liveMo.setJumpNo(mainMo.getJumpNo());
        liveMo.setLookNum(mainMo.getLookNum());
        liveMo.setPermanentResidence(mainMo.getPermanentResidence());
        liveMo.setPraseCount(mainMo.getPraseCount());
        liveMo.setNickName(mainMo.getNickName());
        liveMo.setRoomId(mainMo.getRoomId());
        liveMo.setTitle(mainMo.getTitle());
        liveMo.setUserId(mainMo.getUserId());
        liveMo.setLabelId(mainMo.getLabelId());

        mRoomFragment = RoomFragment.newInstance(liveMo);
        mRoomContainer = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_room_containe_copyr, null);
        mFragmentContainer = mRoomContainer.findViewById(R.id.fragment_container);
        textureView = mRoomContainer.findViewById(R.id.textTureView);

        //mData.add(liveMo);
        playPageAdapter = new PlayPageAdapter(mData);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurrentItem = position;
            }
        });
        mViewPager.setPageTransformer(false, (page, position) -> {
            ViewGroup viewGroup = (ViewGroup) page;
            if ((position < 0 && viewGroup.getId() != mCurrentItem)) {
                View roomContainer = viewGroup.findViewById(R.id.room_container);
                if (roomContainer != null && roomContainer.getParent() != null && roomContainer.getParent() instanceof ViewGroup) {
                    ((ViewGroup) (roomContainer.getParent())).removeView(roomContainer);
                }
            }
            // 满足此种条件，表明需要加载直播视频，以及聊天室了
            if (viewGroup.getId() == mCurrentItem && position == 0 && mCurrentItem != mRoomId) {
                if (mRoomContainer.getParent() != null && mRoomContainer.getParent() instanceof ViewGroup) {
                    ((ViewGroup) (mRoomContainer.getParent())).removeView(mRoomContainer);
                }
                loadVideoAndChatRoom(viewGroup, mCurrentItem);
            }
        });
        mViewPager.setAdapter(playPageAdapter);
        mViewPager.setCurrentItem(getIntent().getIntExtra("position",0));

        initZegpLive();
    }

    private void initZegpLive() {
        zegoLiveRoom = MyApplication.getInstance().initGeGo();
        zegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill);

        //设置用户信息
        ZegoLiveRoom.setUser(String.valueOf(userMess.getId()), userMess.getNickName());

        //设置拉流回调监听
        zegoLiveRoom.setZegoLivePlayerCallback(new IZegoLivePlayerCallback() {
            @Override
            public void onPlayStateUpdate(int i, String s) {
                if (i != 0){
                    ToastUtil.showShort(getContext(),"主播已离开房间");
                }else {                                         //ScaleToFill（填充），ScaleAspectFill（默认）
                    zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, steamID);
                }
            }

            @Override
            public void onPlayQualityUpdate(String s, ZegoPlayStreamQuality zegoPlayStreamQuality) {

            }

            @Override
            public void onInviteJoinLiveRequest(int i, String s, String s1, String s2) {

            }

            @Override
            public void onRecvEndJoinLiveCommand(String s, String s1, String s2) {

            }

            @Override
            public void onVideoSizeChangedTo(String s, int i, int i1) {

            }
        });

        //设置房间消息监听
        zegoLiveRoom.setZegoIMCallback(new IZegoIMCallback() {
            @Override
            public void onUserUpdate(ZegoUserState[] zegoUserStates, int i) {
                ZegoRoomMessage message = new ZegoRoomMessage();
                message.messageCategory = ZegoIM.MessageCategory.System;
                message.fromUserName = zegoUserStates[0].userName;
                int flag = zegoUserStates[0].updateFlag;//1 进入房间  2离开房间
                if (flag == 1){
                    //客户进入直播间
                    message.content = "进入房间";
                }
                if (flag == 2){
                    //客户离开直播间
                    message.content = "离开房间";
                }

                setNotifyUi(message,null);
            }

            @Override
            public void onRecvRoomMessage(String s, ZegoRoomMessage[] zegoRoomMessages) {
                setNotifyUi(zegoRoomMessages[0],null);
            }

            @Override
            public void onUpdateOnlineCount(String s, int i) {
                addAndRemoveUser(i);
            }
            @Override
            public void onRecvBigRoomMessage(String s, ZegoBigRoomMessage[] zegoBigRoomMessages) {

            }
        });

        //设置房间配置信息
        zegoLiveRoom.setRoomConfig(false,true);

    }

    //登陆房间
    private void loginRoom(String roomId,int currentItem){
        zegoLiveRoom.loginRoom(roomId, Audience, (stateCode, zegoStreamInfos) -> {
            if (stateCode == 0) {
                //开始拉流
                steamID = zegoStreamInfos[0].streamID;
                zegoLiveRoom.startPlayingStream(zegoStreamInfos[0].streamID, textureView);
                setNotifyUi(null,mData.get(currentItem));
            }
        });
    }

    //退出房间
    private void logOutRoom(){
        zegoLiveRoom.logoutRoom();
    }

    private int page = 1;
    @Override
    public void initData() {
        //获取直播列表
        Map<String, Object> map = new HashMap<>();
        map.put("liveTag", liveMo.getLabelId());
        map.put("page", page);
        map.put("userId", SPUtils.instance(getContext()).getUser().getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getOnlineList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                List<LiveMo> list = new Gson().fromJson(result,
                        new TypeToken<List<LiveMo>>() {}.getType());
                if (list != null && list.size()>0){
                    mData.addAll(list);
                    if (page == 1){
                        mData.add(0,liveMo);
                        playPageAdapter.notifyData(mData);
                    }else {
                        playPageAdapter.addData(mData);
                    }
                }
            }
            @Override
            public void onFailed(String msg) {

            }
        });
    }

    private boolean mInit = false;
    private FragmentManager mFragmentManager;
    private void loadVideoAndChatRoom(ViewGroup viewGroup, int currentItem) {
        if (!mInit) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.beginTransaction().add(mFragmentContainer.getId(), mRoomFragment).commitAllowingStateLoss();
            mInit = true;
        }
        loadLive(currentItem);
        viewGroup.addView(mRoomContainer);
        mRoomId = currentItem;
    }

    /**
     * 加载直播间
     * 切换流
     *
     * @param currentItem
     */
    private void loadLive(int currentItem) {
        //停止礼物动画
        //停止svga动画
        //清空评论列表,并设置用户信息

        //清空弹幕列表

        //退出上一个房间
        logOutRoom();

        //停止上一个流
        if (currentItem>0){
            zegoLiveRoom.stopPlayingStream(this.steamID);
        }

        loginRoom(mData.get(currentItem).getRoomId(),currentItem);

    }

    private String steamID;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        zegoLiveRoom.stopPlayingStream(this.steamID);
        logOutRoom();
        MyApplication.getInstance().desGeGo();
    }

    public void sendMessage(int type,String mess){
        zegoLiveRoom.sendRoomMessage(1, type, mess, (i, s, l) -> { });
    }
}
