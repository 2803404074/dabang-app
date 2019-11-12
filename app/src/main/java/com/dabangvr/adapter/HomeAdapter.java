package com.dabangvr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dbvr.baselibrary.model.HomeFindMo;

import java.util.List;

/**
 * 播报页多类型适配
 * T 类型指定新闻公共类
 */
public abstract class HomeAdapter extends RecyclerView.Adapter<BaseRecyclerHolder> {

    private Context mContext;
    private List<HomeFindMo> mData;

    public final int mTypeZero = 0;
    public final int mTypeOne = 1;
    public final int mTypeTow = 2;
    public final int mTypeThree = 3;
    public final int mTypeFour = 4;

    public HomeAdapter(Context mContext,List<HomeFindMo> list) {
        this.mContext = mContext;
        this.mData = list;
    }

    public void updateDataa(List<HomeFindMo> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addAll(List<HomeFindMo> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public int getViewTypeForMyTask(int position){
        return mData.get(position).getPosition();
    }

    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = 0;
        if (viewType == mTypeZero){
            layout = R.layout.item_home_find_one;
        }else if (viewType == mTypeOne){
            layout = R.layout.item_home_find_tow;
        }else if (viewType == mTypeTow){
            layout = R.layout.item_home_find_three;
        }else if (viewType == mTypeThree){
            layout = R.layout.item_home_find_one;
        }else if (viewType == mTypeFour){
            layout = R.layout.item_home_find_one;
        }
        if (layout == 0)return null;

        View view = LayoutInflater.from(parent.getContext()).
                inflate(layout, parent, false);

        return BaseRecyclerHolder.getRecyclerHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int position) {
        if (holder == null)return;
        if (null == holder.itemView.getTag()){
            holder.itemView.setTag(position);
            convert(mContext, holder,position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        HomeFindMo homeMo = mData.get(position);
        return homeMo.getPosition();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract void convert(Context mContext, BaseRecyclerHolder holder,int mType);
}
