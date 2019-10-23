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
import com.dabangvr.play.widget.MediaController;
import com.dabangvr.play.widget.MyPagerAdapter;
import com.dabangvr.play.widget.PlayUtils;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.model.LiveComment;
import com.dbvr.baselibrary.other.Contents;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class VideoActivityTest extends BaseActivity implements PLOnErrorListener{
    public static final String TAG = "MainActivity";

    //拉留更新
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    //更新评论内容，handle
    private final int handleMessRequestCode = 100;

    private static final String DEFAULT_TEST_URL = "http://pili-clickplay.vrzbgw.com/WeChat_20191003172340.mp4";
    private boolean mIsActivityPaused = true;
    private MediaController mMediaController;
    private PLVideoTextureView mVideoView;
    private String mVideoPath = null;

    @BindView(R.id.view_pager)
    VerticalViewPager mViewPager;

    private RelativeLayout mRoomContainer;
    private MyPagerAdapter mPagerAdapter;
    private int mCurrentItem;

    private FrameLayout mFragmentContainer;
    private Subscription mSubscription = Subscriptions.empty();
    private FragmentManager mFragmentManager;
    private int mRoomId = -1;
    private boolean mInit = false;

    private List<HomeFindMo.TowMo>mData = new ArrayList<>();

    private TextView tvName;

    //消息数量，始终将最新消息显示在recycelview的底部
    private int dataSize;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == handleMessRequestCode) {
                ArrayList data = msg.getData().getParcelableArrayList("data");
                dataSize++;
                recyclerView.smoothScrollToPosition(dataSize);
                commentAdapter.addAll(data);
            }

            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
            if (mIsActivityPaused || !PlayUtils.isLiveStreamingAvailable()) {//直播是否可用
                finish();
                return;
            }
            if (!PlayUtils.isNetworkAvailable(VideoActivityTest.this)) {//网络是否可用
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };

    private RecyclerView recyclerView;
    //列表适配器
    private RecyclerAdapter commentAdapter;
    //评论数据源
    private List<LiveComment> commentData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_room;
    }

    @Override
    public void initView() {
        mViewPager = (VerticalViewPager) findViewById(R.id.view_pager);

        //直播间内容的布局
        mRoomContainer = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_room_container, null);
        mFragmentContainer = mRoomContainer.findViewById(R.id.fragment_container);
        mVideoView = mRoomContainer.findViewById(R.id.texture_view);
        //昵称
        tvName = mRoomContainer.findViewById(R.id.auth_name);
        //评论列表
        recyclerView = mRoomContainer.findViewById(R.id.recycle_comment);
        doTopGradualEffect();
        initCommentUi();

        mVideoView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
        mVideoPath = DEFAULT_TEST_URL;
        mFragmentManager = getSupportFragmentManager();

        mVideoView.setAVOptions(initAVOptions());
        mMediaController = new MediaController(this, false, true);

        mPagerAdapter = new MyPagerAdapter(mData);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e(TAG, "mCurrentId == " + position + ", positionOffset == " + positionOffset +
                        ", positionOffsetPixels == " + positionOffsetPixels);
                mCurrentItem = position;
                mVideoPath = mData.get(position).getFname();
            }
        });

        mViewPager.setPageTransformer(false, (page, position) -> {
            ViewGroup viewGroup = (ViewGroup) page;
            Log.e(TAG, "page.id == " + page.getId() + ", position == " + position);

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
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initCommentUi() {
        commentAdapter = new RecyclerAdapter<LiveComment>(getContext(), commentData, R.layout.item_comment) {
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
            }
        };
        recyclerView.setAdapter(commentAdapter);
    }

    @Override
    public void initData() {
        //获取直播列表
        for (int i = 0; i < 10; i++) {
            HomeFindMo.TowMo aa = new HomeFindMo.TowMo();
            aa.setFname("http://pili-clickplay.vrzbgw.com/WeChat_20191003172340.mp4");
            aa.setNickName("昵称"+i);
            mData.add(aa);
        }
        mPagerAdapter.update(mData);

        getComment();


    }

    private Thread thread;
    private void getComment(){
        //模拟评论
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 50; i++) {
                        setNotifyUi(new LiveComment(Contents.HY_COMMENT, "大邦程序员", "", "成功啦！！！"));
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

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
                break;
            case Contents.HY_DS://打赏消息
                Log.e("HyListener", "收到打赏消息:" + liveComment.toString());

                break;
            case Contents.HY_ORDER://下单消息
                break;
            case Contents.HY_DZ://点赞消息,收到一条累计

                break;
        }
    }


    private AVOptions initAVOptions() {
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        int codec = 0;
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);
        return options;
    }

    /**
     * @param viewGroup
     * @param currentItem
     */
    private void loadVideoAndChatRoom(ViewGroup viewGroup, int currentItem) {
        tvName.setText(mData.get(currentItem).getNickName());
        commentData.clear();
        if (thread!=null){
            if (thread.isAlive()){
                thread.interrupt();
            }
        }
        getComment();
        //聊天室的fragment只加载一次，以后复用
        if (!mInit) {
            mFragmentManager.beginTransaction().add(mFragmentContainer.getId(), RoomFragment.newInstance()).commitAllowingStateLoss();
            mInit = true;
        }
        loadVideo(currentItem);
        viewGroup.addView(mRoomContainer);
        mRoomId = currentItem;
    }

    private void loadVideo(int position) {
        mVideoView.setMediaController(mMediaController);
        mVideoView.setOnErrorListener(this);
        mVideoView.setVideoPath(mData.get(position).getFname());
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
        if (thread!=null){
            if (thread.isAlive()){
                thread.interrupt();
            }
        }
    }

    private void sendReconnectMessage() {
        ToastUtil.showShort(VideoActivityTest.this,"正在重连...");
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    @Override
    public boolean onError(int errorCode) {
        boolean isNeedReconnect = false;
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
                isNeedReconnect = true;
                break;
            case ERROR_CODE_PLAYER_DESTROYED:
                ToastUtil.showShort(VideoActivityTest.this,"播放器已被销毁，需要再次 setVideoURL 或 prepareAsync...");
                break;
            default:
                ToastUtil.showShort(VideoActivityTest.this,"unknown error !...");
                break;
        }
        // Todo pls handle the error status here, reconnect or call finish()
        if (isNeedReconnect) {
            sendReconnectMessage();
        } else {
            finish();
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

}
