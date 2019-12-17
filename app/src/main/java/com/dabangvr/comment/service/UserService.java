package com.dabangvr.comment.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;

public class UserService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getUserInfo();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void getUserInfo() {
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.getUserInfo, null, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)){
                    ToastUtil.showShort(UserService.this,"登陆信息已过期");
                    stopSelf();
                    return;
                }
                try {
                    UserMess userMess = new Gson().fromJson(result, UserMess.class);
                    if (userMess == null){
                        ToastUtil.showShort(UserService.this,"信息已过期，请重新登录");
                    }else {
                        SPUtils.instance(UserService.this).put("token", userMess.getToken());
                        SPUtils.instance(UserService.this).putUser(userMess);
                    }
                    stopSelf();
                }catch (Exception e){
                    ToastUtil.showShort(UserService.this,"信息已过期，请重新登录");
                    stopSelf();
                }
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(UserService.this,msg);
                stopSelf();
            }
        });
    }

}
