package com.tencent.liteav.demo.my.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


import androidx.annotation.NonNull;

import com.dbvr.baselibrary.adapter.BaseRecyclerHolder;
import com.dbvr.baselibrary.model.VideoMo;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.tencent.liteav.demo.play.R;

import java.util.ArrayList;
import java.util.List;


public abstract class SlideAdapter<T> extends VideoPlayAdapter<BaseRecyclerHolder>{

    private Context mContext;

    private List<T> mDatas;

    private int mLayoutId;

    private AdapterInter adapterInter;
    private View loadingView;

    private RotateAnimation rotateAnimation;
    private ImageView sdvMusic;

    public List<T> getmDatas() {
        return mDatas;
    }
    public void updatePraseStatus(int position,boolean pre){
        VideoMo videoMo = (VideoMo) mDatas.get(position);
        videoMo.setPraseStatus(pre);
        //notifyDataSetChanged();
    }

    public void updateData(List<T> mDatas){
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }
    private PLVideoTextureView mVideoView;
    private MediaController mMediaController;
    private AVOptions options;

    public SlideAdapter(Context mContext,List<T> mDatas, int mLayoutId) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mLayoutId = mLayoutId;
        mMediaController = new MediaController(mContext);
        setAvOption();

        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(4000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
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


    public void setAdapterInter(AdapterInter adapterInter) {
        this.adapterInter = adapterInter;
    }

    public abstract void convert(Context mContext, BaseRecyclerHolder holder,int position, T t);

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
        convert(mContext, holder, position,mDatas.get(position));

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
        if (sdvMusic!=null){
            sdvMusic = null;
        }
        sdvMusic = itemView.findViewById(R.id.sdvMusic);
        sdvMusic.startAnimation(rotateAnimation);

        if (mVideoView!=null){
            mVideoView.stopPlayback();
            mVideoView = null;
        }
        mVideoView = itemView.findViewById(R.id.video_view);
        VideoMo videoMo = (VideoMo) mDatas.get(itemPosition);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setLooping(true);
        mVideoView.setVideoPath(videoMo.getVideoUrl());
        mVideoView.setAVOptions(options);
        mVideoView.setBufferingIndicator(loadingView);
        itemView.setOnClickListener(view -> {
           if (mVideoView.isPlaying()){
               mVideoView.pause();
               itemView.findViewById(R.id.ivPlay).setVisibility(View.VISIBLE);
           }else {
               itemView.findViewById(R.id.ivPlay).setVisibility(View.GONE);
               mVideoView.start();
           }
        });
        if (select!=null){
            select.videoItemView(mVideoView,itemPosition);
        }
    }

    public void startVideo(){
        if (mVideoView!=null){
            if (mVideoView.isPlaying())return;
            mVideoView.start();
        }
    }

    private Select select;

    public void setSelect(Select select) {
        this.select = select;
    }

    public interface Select{
        void videoItemView(PLVideoTextureView textureView,int position);
    }
}
