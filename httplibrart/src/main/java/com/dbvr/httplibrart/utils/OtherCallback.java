package com.dbvr.httplibrart.utils;

import android.content.Context;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 类的用途 如果要将得到的json直接转化为集合 建议使用该类
 */

public abstract class OtherCallback<T> implements Callback {
    private Context mContext;

    public OtherCallback(Context mContext) {
        this.mContext = mContext;
    }

    private Handler handler = OkHttp3Utils.getInstance(mContext).getHandler();

    //主线程处理
    public abstract void onUi(T result) throws JSONException;

    //主线程处理
    public abstract void onFailed(String msg);

    //请求失败
    @Override
    public void onFailure(final Call call, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailed(e.getMessage());
            }
        });
    }

    //请求json 并直接返回泛型的对象 主线程处理
    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        final String json = response.body().string();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    onUi((T)json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}