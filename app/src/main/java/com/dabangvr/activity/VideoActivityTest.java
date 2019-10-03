package com.dabangvr.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.adapter.LivePageAdapter;
import com.dabangvr.fragment.RoomFragment;
import com.dabangvr.play.MediaController;
import com.dbvr.baselibrary.model.TestLive;
import com.dbvr.baselibrary.utils.NetWorkUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.ArrayList;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * 上下切换房间直播
 *
 */
public class VideoActivityTest  extends AppCompatActivity implements PLOnErrorListener, PLOnCompletionListener {
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    private boolean mIsActivityPaused = true;
    private MediaController mMediaController;
    private PLVideoTextureView mVideoView;
    private String mVideoPath = null;
    private int mDisplayAspectRatio = PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
            if (mIsActivityPaused || !NetWorkUtils.isLiveStreamingAvailable()) {
                finish();
                return;
            }
            if (!NetWorkUtils.isNetworkAvailable(VideoActivityTest.this)) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };
    private VerticalViewPager mViewPager;
    private RelativeLayout mRoomContainer;
    private LivePageAdapter mPagerAdapter;
    private int mCurrentItem;
    private int isLiveStreaming = 1;
    private AVOptions options;
    private FrameLayout mFragmentContainer;
    private ArrayList<TestLive> mData = new ArrayList<>();
    private Subscription mSubscription = Subscriptions.empty();
    private FragmentManager mFragmentManager;
    private int mRoomId = -1;
    private RoomFragment mRoomFragment;
    private boolean mInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }




        mViewPager = (VerticalViewPager) findViewById(R.id.view_pager);
        mRoomContainer = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_room_container, null);
        mFragmentContainer = (FrameLayout) mRoomContainer.findViewById(R.id.fragment_container);
        mVideoView = (PLVideoTextureView) mRoomContainer.findViewById(R.id.texture_view);
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
       // mVideoPath = DEFAULT_TEST_URL;
        mFragmentManager = getSupportFragmentManager();
        initAVOptions();
        mVideoView.setAVOptions(options);
        mMediaController = new MediaController(this, false, true);
        generateUrls();
        mPagerAdapter = new LivePageAdapter(mData);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurrentItem = position;
            }
        });

        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
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
                    loadVideoAndChatRoom(viewGroup, mCurrentItem,mData.get(mCurrentItem));
                }
            }
        });
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void generateUrls() {
        mData.add(new TestLive("名称1","111","http://pili-clickplay.vrzbgw.com/WeChat_20191003172317.mp4"));
        mData.add(new TestLive("名称2","333","http://pili-clickplay.vrzbgw.com/WeChat_20191003172340.mp4"));
        mData.add(new TestLive("名称3","444","http://pili-clickplay.vrzbgw.com/WeChat_20191003172439.mp4"));
        mData.add(new TestLive("名称4","555","http://pili-clickplay.vrzbgw.com/WeChat_20191003172444.mp4"));
    }

    private void initAVOptions() {
        options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, isLiveStreaming);
        int codec = 0;
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);
    }

    /**
     * @param viewGroup
     * @param currentItem
     */
    private void loadVideoAndChatRoom(ViewGroup viewGroup, int currentItem,TestLive testLive) {
//        mSubscription = AppObservable.bindActivity(this, ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
        //聊天室的fragment只加载一次，以后复用
        Log.e("hahaha",testLive.toString());
        if (!mInit) {
            mRoomFragment = RoomFragment.newInstance();
            mFragmentManager.beginTransaction().add(mFragmentContainer.getId(), mRoomFragment).commitAllowingStateLoss();
            mInit = true;
            mRoomFragment.updata(testLive);
        }
        mRoomFragment.updata(testLive);
        loadVideo(currentItem);
        viewGroup.addView(mRoomContainer);
        mRoomId = currentItem;
    }

    private void loadVideo(int position) {
        mVideoView.setMediaController(mMediaController);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setVideoPath(mData.get(position).getUrl());
        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
        mIsActivityPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActivityPaused = false;
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
        mSubscription.unsubscribe();
    }

    private void sendReconnectMessage() {
        System.out.println("正在重连");
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    @Override
    public boolean onError(int errorCode) {
        boolean isNeedReconnect = false;
        // Todo pls handle the error status here, reconnect or call finish()
        if (isNeedReconnect) {
            sendReconnectMessage();
        } else {
            finish();
        }
        return true;
    }

    @Override
    public void onCompletion() {
        finish();
    }
}
