package com.dabangvr.fragment.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dbvr.baselibrary.model.AllTypeMo;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.hyphenate.chat.EMMessage;

import java.util.List;

public abstract class TiaoTiaoAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder>{
    private Context mContext;
    private List<AllTypeMo> mData;

    public TiaoTiaoAdapter(Context mContext, List<AllTypeMo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public void upData(List<AllTypeMo> mData){
        this.mData.clear();
        this.mData = mData;
        notifyDataSetChanged();
    }

    public void addData(List<AllTypeMo> mData){
        this.mData.addAll(mData);
        notifyDataSetChanged();
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i==0){
            View layout = LayoutInflater.from(mContext).
                    inflate(R.layout.item_live_type, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(mContext, layout);
        }else if (i == 1){
            View layout = LayoutInflater.from(mContext).
                    inflate(R.layout.item_video_type, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(mContext,layout);
        }else {
            View layout = LayoutInflater.from(mContext).
                    inflate(R.layout.item_goods_type, viewGroup, false);
            return BaseRecyclerHolder.getRecyclerHolder(mContext,layout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int i) {
        convert(mContext, holder, (T) mData.get(i),i);
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        AllTypeMo allTypeMo = mData.get(position);
        return allTypeMo.getPosition();
    }
    public abstract void convert(Context mContext, BaseRecyclerHolder holder, T t,int position);
}
