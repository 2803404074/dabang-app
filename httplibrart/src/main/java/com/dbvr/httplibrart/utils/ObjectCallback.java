package com.dbvr.httplibrart.utils;

import android.content.Context;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeoutException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 类的用途 如果要将得到的json直接转化为集合 建议使用该类
 */

public abstract class ObjectCallback<T> implements Callback {
    private Context mContext;

    public ObjectCallback(Context mContext) {
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
        try {
            final JSONObject object = new JSONObject(json);
            if (object.optInt("errno")== 0){
                if (object.optInt("code") == 500){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onFailed(object.optString("msg"));
                        }
                    });
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onUi((T) object.optString("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String str = object.optString("errmsg");
                        onFailed(str.equals("")||str==null?response.toString():str);
                    }
                });
            }
        } catch (final JSONException e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onFailed(e.getMessage());
                }
            });
        } catch (Exception e){
            if (e instanceof ConnectException){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onUi((T) "连接异常");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else if (e instanceof TimeoutException){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onUi((T) "连接超时");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}