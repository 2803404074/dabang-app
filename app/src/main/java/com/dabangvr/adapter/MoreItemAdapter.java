package com.dabangvr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dbvr.baselibrary.model.MoreItemType;

import java.util.List;

/**
 * 播报页多类型适配
 * T 类型指定新闻公共类
 */
public abstract class MoreItemAdapter<T extends MoreItemType> extends RecyclerView.Adapter<BaseRecyclerHolder> {

    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private List<T>mData;

    private View mHeadView;
    private View mFooterView;
    public static final int TYPE_HEADER = 0;  //说明是带有Header的
    public static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    public MoreItemAdapter(Context mContext, List<T> mDatas) {
        this.mContext = mContext;
        this.mData = mDatas;
    }

    public void updateData(List<T> data){
        this.mData = data;
        notifyDataSetChanged();
    }
    public void addData(List<T> data){
        this.mData.addAll(data);
//        this.mData = data;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = null;
        if (mHeadView != null && viewType == TYPE_HEADER) {
            return BaseRecyclerHolder.getRecyclerHolder(mContext, mHeadView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return BaseRecyclerHolder.getRecyclerHolder(mContext, mFooterView);
        }
        for (int i = 0; i < mData.size(); i++) {
            if (viewType == mData.get(i).getmType()){
                layout = LayoutInflater.from(parent.getContext()).
                        inflate(mData.get(i).getLayoutId(), parent, false);
                break;
            }
        }
        return BaseRecyclerHolder.getRecyclerHolder(mContext, layout);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int position) {
        if (null == holder.itemView.getTag()){
            holder.itemView.setTag(position);
            convert(holder,position,mData.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener!=null){
                        mItemClickListener.onItemClick(v, position);
                    }
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {

        if (mHeadView == null && mFooterView == null) {
            return mData.get(position).getmType();
        }
        if (position == 0) {
            //第一个item应该加载Header
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1) {
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (mHeadView == null && mFooterView == null) {
            return mData.size();
        } else if (mHeadView == null && mFooterView != null) {
            return mData.size() + 1;
        } else if (mHeadView != null && mFooterView == null) {
            return mData.size() + 1;
        } else {
            return mData.size() + 2;
        }
    }

    public abstract void convert(BaseRecyclerHolder holder,int position,T t);

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}
