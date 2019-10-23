package com.dabangvr.play.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.dabangvr.R;
import com.dbvr.baselibrary.model.HomeFindMo;

import java.util.List;

public class MyPagerAdapter  extends PagerAdapter {

    private List<HomeFindMo.TowMo> mData;

    public MyPagerAdapter(List<HomeFindMo.TowMo> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
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

    public void update(List<HomeFindMo.TowMo> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }
}