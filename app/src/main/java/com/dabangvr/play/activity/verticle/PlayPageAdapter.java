package com.dabangvr.play.activity.verticle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.dabangvr.R;
import com.dabangvr.play.model.LiveData;
import com.dbvr.baselibrary.model.LiveMo;
import com.dbvr.baselibrary.model.MainMo;

import java.util.List;

public class PlayPageAdapter extends PagerAdapter {

    private List<LiveMo>mData;

    public PlayPageAdapter(List<LiveMo> mData) {
        this.mData = mData;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (mData.contains((View) object)) {
            // 如果当前 item 未被 remove，则返回 item 的真实 position
            return mData.indexOf((View) object);
        } else {
            // 否则返回状态值 POSITION_NONE
            return POSITION_NONE;
        }
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

    public void notifyData(List<LiveMo>mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    public void addData(List<LiveMo>mData){
        this.mData.addAll(mData);
        notifyDataSetChanged();
    }
}