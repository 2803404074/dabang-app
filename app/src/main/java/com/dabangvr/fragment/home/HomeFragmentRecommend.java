package com.dabangvr.fragment.home;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.activity.MusicActivity;
import com.dabangvr.adapter.AdapterInter;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.SlideAdapter;
import com.dabangvr.ui.VideoPlayRecyclerView;
import com.dbvr.baselibrary.model.AllTypeMo;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.view.LazyFragment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 推荐短视频
 */
public class HomeFragmentRecommend extends LazyFragment{

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    @BindView(R.id.recycler_video)
    VideoPlayRecyclerView recyclerView;

    private SlideAdapter adapter;

    private List<AllTypeMo.VideoMo>mData = new ArrayList<>();

    @Override
    public int layoutId() {
        return R.layout.fragment_home_recommend;
    }

    @Override
    public void initView() {
        UserMess userMess = SPUtils.instance(getContext()).getUser();
        Log.e("eeee","HomeFragmentRecommend----initView");
        adapter = new SlideAdapter<AllTypeMo.VideoMo>(getContext(),progressBar,mData,R.layout.item_video) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, AllTypeMo.VideoMo s) {
                    SimpleDraweeView sdvHead = holder.getView(R.id.sdvHead);
                    holder.setImageByUrl(R.id.rlImg,s.getCoverPath());
                    sdvHead.setImageURI(userMess==null?"":userMess.getHeadUrl());
                    holder.getView(R.id.sdvMusic).setOnClickListener((view)->{
                        goTActivity(MusicActivity.class,null);
                    });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setAdapterInter(new AdapterInter() {
            @Override
            public void onItemClick(View view, int position) {
                if (adapter.getmVideoView().isPlaying()){
                    adapter.getmVideoView().pause();
                }else {
                    adapter.startPlay();
                }
            }

            @Override
            public void onLongItemClick(View view, int postion) {

            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser){
            if (adapter!=null){
                if (adapter.getmVideoView()!=null){
                    adapter.getmVideoView().pause();
                }
            }
        }else {
            if (adapter!=null){
                if (adapter.getmVideoView()!=null){
                    adapter.startPlay();
                }
            }
        }
    }

    @Override
    public void loadData() {
        for (int i = 0; i < 5; i++) {
            AllTypeMo.VideoMo allTypeMo = new AllTypeMo.VideoMo();
            allTypeMo.setCoverPath("http://image.vrzbgw.com/1573486883%281%29.png");
            allTypeMo.setVideoUrl("http://pili-clickplay.vrzbgw.com/edea7ad390363af68691d0fa879058a9.mp4");
            mData.add(allTypeMo);
        }


//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172307.mp4");
//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172317.mp4");
//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172340.mp4");
//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172439.mp4");
//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172444.mp4");
//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172307.mp4");
//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172317.mp4");
//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172340.mp4");
//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172439.mp4");
//        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172444.mp4");
        adapter.updateData(mData);
    }

    @Override
    public void isVisibleToUserFunction(boolean isDataInitiated) { }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter!=null){
            if (adapter.getmVideoView()!=null){
                adapter.getmVideoView().stopPlayback();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isVisibleToUser){
            if (adapter!=null){
                if (adapter.getmVideoView()!=null){
                    adapter.getmVideoView().pause();
                }
            }
        }else {
            if (adapter!=null){
                if (adapter.getmVideoView()!=null){
                    adapter.startPlay();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter!=null){
            if (adapter.getmVideoView()!=null){
                adapter.getmVideoView().pause();
            }
        }
    }
}
