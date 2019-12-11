package com.tencent.liteav.demo.my.fragment;

import android.content.Context;
import com.dbvr.baselibrary.adapter.BaseRecyclerHolder;
import com.dbvr.baselibrary.model.VideoMo;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.tencent.liteav.demo.my.view.LazyFragment;
import com.tencent.liteav.demo.my.view.SlideAdapter;
import com.tencent.liteav.demo.my.view.VideoPlayRecyclerView;
import com.tencent.liteav.demo.play.R;
import java.util.ArrayList;

public class ShortVideoFragment extends LazyFragment {

    private PLVideoTextureView superPlayerView;
    private VideoPlayRecyclerView recyclerView;
    private ArrayList<VideoMo> mDatas = new ArrayList<>();
    private SlideAdapter adapter;

    public ShortVideoFragment() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (superPlayerView!=null){
            superPlayerView.stopPlayback();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (superPlayerView!=null){
            superPlayerView.pause();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (superPlayerView!=null){
            superPlayerView.pause();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser){
            if (adapter!=null){
                if (superPlayerView!=null){
                    superPlayerView.pause();
                }
            }
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_video;
    }

    @Override
    protected void lazyLoad() {
        recyclerView = findViewById(R.id.videoPlayRecyclerView);
        mDatas.add(new VideoMo("http://pili-clickplay.vrzbgw.com/WeChat_20191003172307.mp4"));
        mDatas.add(new VideoMo("http://pili-clickplay.vrzbgw.com/WeChat_20191003172439.mp4"));
        mDatas.add(new VideoMo("http://pili-clickplay.vrzbgw.com/WeChat_20191003172307.mp4"));
        mDatas.add(new VideoMo("http://pili-clickplay.vrzbgw.com/WeChat_20191003172439.mp4"));
        mDatas.add(new VideoMo("http://pili-clickplay.vrzbgw.com/WeChat_20191003172307.mp4"));
        mDatas.add(new VideoMo("http://pili-clickplay.vrzbgw.com/WeChat_20191003172439.mp4"));
        mDatas.add(new VideoMo("http://pili-clickplay.vrzbgw.com/WeChat_20191003172307.mp4"));
        mDatas.add(new VideoMo("http://pili-clickplay.vrzbgw.com/WeChat_20191003172439.mp4"));

        adapter = new SlideAdapter<VideoMo>(getContext(),mDatas,R.layout.item_video) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, VideoMo s) {
                SimpleDraweeView sdvHead = holder.getView(R.id.sdvHead);
                holder.setImageByUrl(R.id.rlImg,s.getUrl());
            }
        };
        adapter.setSelect((textureView) -> {
            this.superPlayerView = textureView;
            superPlayerView.start();
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void stopLoad() {
        super.stopLoad();
        if (superPlayerView!=null){
            superPlayerView.stopPlayback();
        }
    }
}
