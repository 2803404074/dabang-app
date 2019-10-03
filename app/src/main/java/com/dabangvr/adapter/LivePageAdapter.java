package com.dabangvr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;
import com.dbvr.baselibrary.model.TestLive;

import java.util.List;

public class LivePageAdapter extends androidx.viewpager.widget.PagerAdapter {

    private List<TestLive> mVideoUrls;

    public LivePageAdapter(List<TestLive> mVideoUrls) {
        this.mVideoUrls = mVideoUrls;
    }

    public void update(List<TestLive> mVideoUrls){
        this.mVideoUrls = mVideoUrls;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mVideoUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.view_room_item, null);
        view.setId(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(container.findViewById(position));
    }
}