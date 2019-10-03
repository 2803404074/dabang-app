package com.dabangvr.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.dabangvr.R;
import com.dabangvr.play.MediaController;
import com.dabangvr.ui.VideoPlayAdapter;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

import java.util.List;


public abstract class SlideAdapter<T> extends VideoPlayAdapter<BaseRecyclerHolder>  implements PLOnErrorListener, PLOnInfoListener {

    private Context mContext;

    private List<T> mDatas;

    private int mLayoutId;

    private AdapterInter adapterInter;

    private PLVideoTextureView mVideoView;
    private MediaController mMediaController;
    private View loadingView;

    public void updateData( List<T> mDatas){
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public SlideAdapter(Context mContext,View view, List<T> mDatas, int mLayoutId) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.loadingView = view;
        this.mLayoutId = mLayoutId;
        mMediaController = new MediaController(mContext);
    }

    public void setAdapterInter(AdapterInter adapterInter) {
        this.adapterInter = adapterInter;
    }

    public PLVideoTextureView getmVideoView() {
        return mVideoView;
    }

    public abstract void convert(Context mContext, BaseRecyclerHolder holder, T t);

    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).
                inflate(mLayoutId, parent, false);
        return BaseRecyclerHolder.getRecyclerHolder(mContext, layout);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int position) {

        //数据绑定在这里处理
        convert(mContext, holder, mDatas.get(position));

        //点击事件在这里处理
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterInter != null) {
                    adapterInter.onItemClick(v, position);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onPageSelected(int itemPosition, View itemView) {
        //这里处理视频播放逻辑
        if (mVideoView!=null){
            if (mVideoView.isPlaying()){
                mVideoView.stopPlayback();
            }
        }
        mVideoView = itemView.findViewById(R.id.video_view);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setMediaController(mMediaController);
        if (loadingView!=null){
            mVideoView.setBufferingIndicator(loadingView);
        }

        setAvOption((String) mDatas.get(itemPosition));
    }

    /**
     * 设置播放参数
     */
    private void setAvOption(String path) {
        AVOptions options = new AVOptions();

// 解码方式:
// codec＝AVOptions.MEDIA_CODEC_HW_DECODE，硬解
// codec=AVOptions.MEDIA_CODEC_SW_DECODE, 软解
        int codec = AVOptions.MEDIA_CODEC_AUTO; //硬解优先，失败后自动切换到软解
// 默认值是：MEDIA_CODEC_SW_DECODE
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

// 准备超时时间，包括创建资源、建立连接、请求码流等，单位是 ms
// 默认值是：无
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);

// 读取视频流超时时间，单位是 ms
// 默认值是：10 * 1000
        //options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);

// 当前播放的是否为在线直播，如果是，则底层会有一些播放优化
// 默认值是：0
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);


// 快开模式，启用后会加快该播放器实例再次打开相同协议的视频流的速度
        options.setInteger(AVOptions.KEY_FAST_OPEN, 1);

// 默认的缓存大小，单位是 ms
// 默认值是：2000
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 2000);

// 最大的缓存大小，单位是 ms
// 默认值是：4000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 4000);

        mVideoView.setAVOptions(options);
        mVideoView.setVideoPath(path);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setLooping(true);//重复播放
        mVideoView.start();
    }

    @Override
    public boolean onError(int i) {
        Log.e("aaaa","参数一："+i);
        return false;
    }

    @Override
    public void onInfo(int i, int i1) {
        Log.e("aaaa","参数一："+i+",参数二"+i1);
    }
}
