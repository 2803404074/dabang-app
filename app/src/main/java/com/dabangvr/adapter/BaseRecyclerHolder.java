package com.dabangvr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dabangvr.R;
import com.dbvr.baselibrary.utils.StringUtils;

/**
 * 万能RecyclerHolder
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;
    private Context context;

    private BaseRecyclerHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        //指定一个初始为8
        views = new SparseArray<>(8);
    }

    /**
     * 取得一个RecyclerHolder对象
     *
     * @param context  上下文
     * @param itemView 子项
     * @return 返回一个RecyclerHolder对象
     */
    public static BaseRecyclerHolder getRecyclerHolder(Context context, View itemView) {
        return new BaseRecyclerHolder(context, itemView);
    }

    public SparseArray<View> getViews() {
        return this.views;
    }

    /**
     * 通过view的id获取对应的控件，如果没有则加入views中
     *
     * @param viewId 控件的id
     * @return 返回一个控件
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置字符串
     */
    public BaseRecyclerHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        if (!StringUtils.isEmpty(text) && !text.equals("null")) {
            tv.setText(text);
        }
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(drawableId);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    public BaseRecyclerHolder setImageByUrl(int viewId, String url) {

        if (!StringUtils.isEmpty(url) && !url.endsWith("null")) {

            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.test); //设置加载未完成时的占位图
            options.error(R.mipmap.err);
            //GlideLoadUtils.getInstance().glideLoad(context,url,(ImageView) getView(viewId));
            Glide.with(context).load(url).apply(options).into((ImageView) getView(viewId));
        }
        return this;
    }


    public BaseRecyclerHolder setImageByPath(int viewId, String path) {
        Glide.with(context)
                .load(path)
                .into((ImageView)getView(viewId));
        return this;
    }
}
