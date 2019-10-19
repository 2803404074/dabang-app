package com.dbvr.httplibrart.utils;

import android.content.Context;
import android.os.Handler;

import com.dbvr.httplibrart.constans.DyUrl;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static java.lang.String.valueOf;

/**
 * 1. 类的用途 封装OkHttp3的工具类 用单例设计模式
 */

public class OkHttp3Utils {
    /**
     * 懒汉 安全 加同步
     * 私有的静态成员变量 只声明不创建
     * 私有的构造方法
     * 提供返回实例的静态方法
     */

    private static OkHttp3Utils okHttp3Utils = null;
    private Context mContext;

    private OkHttp3Utils(Context mContext) {
        this.mContext = mContext;
    }

    public static OkHttp3Utils getInstance(Context mContext) {
        if (okHttp3Utils == null) {
            //加同步安全
            synchronized (OkHttp3Utils.class) {
                if (okHttp3Utils == null) {
                    okHttp3Utils = new OkHttp3Utils(mContext);
                }
            }

        }

        return okHttp3Utils;
    }

    public static OkHttp3Utils desInstance() {
        if (okHttp3Utils != null) {
            //加同步安全
            synchronized (OkHttp3Utils.class) {
                if (okHttp3Utils != null) {
                    okHttp3Utils = null;
                }
            }
        }
        return okHttp3Utils;
    }

    private static OkHttpClient okHttpClient = null;

    public synchronized OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                    //添加OkHttp3的拦截器
                    .writeTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(new MyInterceptor(mContext))
                    .build();
        }
        return okHttpClient;
    }

    private static Handler mHandler = null;

    public synchronized static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    /**
     * get请求
     * 参数1 url
     * 参数2 回调Callback
     */
    public void doGet(String url, Callback callback) {
        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //补全请求地址
        String requestUrl = DyUrl.BASE + url;
        //创建Request
        Request request = new Request.Builder().url(requestUrl).build();
        //得到Call对象
        Call call = okHttpClient.newCall(request);
        //执行异步请求
        call.enqueue(callback);
    }


    /**
     * 請求其他服務的get方法
     *
     * @param url
     * @param callback
     */
    public void doGetOther(String url, Callback callback) {
        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //创建Request
        Request request = new Request.Builder().url(url).build();
        //得到Call对象
        Call call = okHttpClient.newCall(request);
        //执行异步请求
        call.enqueue(callback);
    }

    /**
     * post请求
     * 参数1 url
     * 参数2 回调Callback
     */

    public void doPost(String url, Map<String, String> params, Callback callback) {

        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //3.x版本post请求换成FormBody 封装键值对参数

        FormBody.Builder builder = new FormBody.Builder();

        //遍历集合
        if (params != null) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }

        //补全请求地址
        String requestUrl = DyUrl.BASE + url;

        //创建Request
        Request request = new Request.Builder().url(requestUrl).post(builder.build()).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 表单请求
     *
     * @param url
     * @param params
     * @param callback
     */
    public void doPostForm(String url, Map<String, String> params, Callback callback) {
        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getOkHttpClient();
        //3.x版本post请求换成FormBody 封装键值对参数
        FormBody.Builder builder = new FormBody.Builder();
        //遍历集合
        if (params != null) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        //补全请求地址
        String requestUrl = DyUrl.BASE + url;
        //创建Request
        Request request = new Request.Builder().url(requestUrl).post(builder.build()).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }


    /**
     * Post请求发送JSON数据
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    public void doPostJson(String url, Map<String, Object> map, Callback callback) {
        Gson gson = new Gson();
        String jsonParams = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder().url(DyUrl.BASE + url).post(requestBody).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    public void doPostJson(String url, Map<String, Object> map, String token, Callback callback) {
        Gson gson = new Gson();
        String jsonParams = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder().url(DyUrl.BASE + url).addHeader(DyUrl.TOKEN_NAME, token).post(requestBody).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /**
     * 上传文件到服务器
     *
     * @param url
     * @param map      可以放一个文件，也可以放文件列表
     * @param callback
     */
    public void upLoadFile(String url, Map<String, Object> map, Callback callback) {
        OkHttpClient client = getOkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (map != null) {
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue() instanceof File) {
                    File file = (File) entry.getValue();
                    RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
                    requestBody.addFormDataPart(valueOf(entry.getKey()), file.getName(), body);
                } else if (entry.getValue() instanceof java.util.List) {
                    List<File> list = (List<File>) entry.getValue();
                    for (int i = 0; i < list.size(); i++) {
                        RequestBody body = RequestBody.create(MediaType.parse("image/*"), list.get(i));
                        requestBody.addFormDataPart(valueOf(entry.getKey()), list.get(i).getName(), body);
                    }
                } else {
                    requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
                }
            }

        }
        String requestUrl = DyUrl.BASE + url;
        Request request = new Request.Builder().url(requestUrl).post(requestBody.build()).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }


}

