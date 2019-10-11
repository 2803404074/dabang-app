package com.dabangvr.fragment.home;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.SlideAdapter;
import com.dabangvr.ui.VideoPlayRecyclerView;
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

    private List<String>mData = new ArrayList<>();

    @Override
    public int layoutId() {
        return R.layout.fragment_home_recommend;
    }

    @Override
    public void initView() {
        UserMess userMess = SPUtils.instance(getContext()).getUser();
        Log.e("eeee","HomeFragmentRecommend----initView");
        adapter = new SlideAdapter<String>(getContext(),progressBar,mData,R.layout.item_video) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String s) {
                    SimpleDraweeView sdvHead = holder.getView(R.id.sdvHead);
                    sdvHead.setImageURI(userMess==null?"":userMess.getHeadUrl());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    @Override
    public void loadData() {
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172307.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172317.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172340.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172439.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172444.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172307.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172317.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172340.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172439.mp4");
        mData.add("http://pili-clickplay.vrzbgw.com/WeChat_20191003172444.mp4");
        adapter.updateData(mData);
    }

    @Override
    public void isVisibleToUserFunction(boolean isDataInitiated) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
