package com.dabangvr.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import com.dabangvr.R;
import com.dabangvr.play.widget.MediaController;
import com.dabangvr.ui.VideoPlayAdapter;
import com.dbvr.baselibrary.model.AllTypeMo;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

import java.util.List;


public abstract class SlideAdapter<T> extends VideoPlayAdapter<BaseRecyclerHolder>{

    private Context mContext;

    private List<T> mDatas;

    private int mLayoutId;

    private AdapterInter adapterInter;

    private PLVideoTextureView mVideoView;
    private MediaController mMediaController;
    private AVOptions options;
    private View loadingView;

    private RotateAnimation rotateAnimation;
    private SimpleDraweeView sdvMusic;

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
        setAvOption();

        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(4000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
    }

    public void setAdapterInter(AdapterInter adapterInter) {
        this.adapterInter = adapterInter;
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
        holder.itemView.setOnClickListener(v -> {
            if (adapterInter != null) {
                adapterInter.onItemClick(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private int position;
    @Override
    public void onPageSelected(int itemPosition, View itemView) {
        position = itemPosition;
        //这里处理视频播放逻辑
        if (mVideoView!=null){
            mVideoView.stopPlayback();
            mVideoView = null;
        }
        mVideoView = itemView.findViewById(R.id.video_view);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setLooping(true);
        AllTypeMo.VideoMo videoMo = (AllTypeMo.VideoMo) mDatas.get(itemPosition);
        mVideoView.setVideoPath(videoMo.getVideoUrl());
        mVideoView.setAVOptions(options);
        mVideoView.setBufferingIndicator(loadingView);
        mVideoView.start();

        if (sdvMusic!=null){
            sdvMusic = null;
        }
        sdvMusic = itemView.findViewById(R.id.sdvMusic);
        sdvMusic.setImageURI("http://img3.redocn.com/20120425/20120423_26097b1c1a637f35f136IxLTd9O3lW0a.jpg");
        sdvMusic.startAnimation(rotateAnimation);


    }

    private void setAvOption() {
        options = new AVOptions();
        // 解码方式:
        // codec＝AVOptions.MEDIA_CODEC_HW_DECODE，硬解
        // codec=AVOptions.MEDIA_CODEC_SW_DECODE, 软解
        int codec = AVOptions.MEDIA_CODEC_AUTO; //硬解优先，失败后自动切换到软解
        // 默认值是：MEDIA_CODEC_SW_DECODE
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        // 快开模式，启用后会加快该播放器实例再次打开相同协议的视频流的速度
        options.setInteger(AVOptions.KEY_FAST_OPEN, 1);
    }

    public PLVideoTextureView getmVideoView(){
        return mVideoView;
    }

    public void startPlay(){
        mVideoView.start();
    }
}
