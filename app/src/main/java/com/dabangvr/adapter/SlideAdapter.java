package com.dabangvr.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.dabangvr.R;
import com.dabangvr.ui.VideoPlayAdapter;

import java.util.List;


public abstract class SlideAdapter<T> extends VideoPlayAdapter<BaseRecyclerHolder>{

    private Context mContext;

    private List<T> mDatas;

    private int mLayoutId;

    private AdapterInter adapterInter;

//    private PLVideoTextureView mVideoView;
//    private MediaController mMediaController;
    private View loadingView;

    public void updateData( List<T> mDatas){
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public SlideAdapter(Context mContext,View view, List<T> mDatas, int mLayoutId) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.loadingView = view;
        this.mLayoutId = mLayoutId;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterInter != null) {
                    adapterInter.onItemClick(v, position);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onPageSelected(int itemPosition, View itemView) {
        //这里处理视频播放逻辑

    }
}
