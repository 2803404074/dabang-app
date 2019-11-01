package com.dabangvr.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import okhttp3.Call;

import com.addressselection.bean.AddressBEAN;
import com.addressselection.bean.AdressBean;
import com.addressselection.bean.AdressBean_two;
import com.addressselection.manager.AddressDictManager;
import com.addressselection.manager.DBUtils;
import com.dabangvr.R;
import com.dabangvr.application.MyApplication;
import com.dbvr.baselibrary.model.MenuBean;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WellcomActivity extends BaseActivity {
    private TextView text_version;
    private AddressBEAN addressBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_wellcom;
    }

    @Override
    public void initView() {
        text_version = this.findViewById(R.id.text_version);
        text_version.setText("V" + getVersion());
    }

    @Override
    public void initData() {
        UserMess userMess = SPUtils.instance(this).getUser();
        if (null == userMess) {
            goTActivity(LoginActivity.class);
        } else {
            goTActivity(MainActivity.class);
            getUserInfo();
        }
        getInitializationData();
    }

    private void getUserInfo() {
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.getUserInfo, null, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                UserMess userMess = new Gson().fromJson(result, UserMess.class);
                SPUtils.instance(getContext()).put("token", userMess.getToken());
                SPUtils.instance(getContext()).putUser(userMess);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }


    private void goTActivity(final Class T) {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WellcomActivity.this, T);
            startActivity(intent);
            finish();
        }, 1500);
    }

    //获取版本号
    private String getVersion() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException eArp) {
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getInitializationData() {
        getChannelMenu();
        getAddressIfno();
    }


    private void getChannelMenu() {
        Map<String, Object> map = new HashMap<>();
        map.put("mallSpeciesId", 8);
        //获取标签
        OkHttp3Utils.getInstance(MyApplication.getInstance()).doPostJson(DyUrl.getChannelMenuList, map, new ObjectCallback<String>(MyApplication.getInstance()) {
            @Override
            public void onUi(String result) throws JSONException {

                SPUtils.instance(WellcomActivity.this).put("getChannelMenuList", result);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    private void getAddressIfno() {
        Map<String, Object> map = new HashMap<>();
        map.put("mallSpeciesId", 8);
        //获取标签
        OkHttp3Utils.getInstance(MyApplication.getInstance()).doPostJson(DyUrl.getAmapDistrict, map, new ObjectCallback<String>(MyApplication.getInstance()) {
            @Override
            public void onUi(String result){
                String rep = "\"citycode\":[]";
                String repe = "\"citycode\":\"0\"";
                String replace = result.replace(rep, repe);
                List<AdressBean_two> list = new Gson().fromJson(replace, new TypeToken<List<AdressBean_two>>() {
                }.getType());

                new Thread(()->{
                    inidSql(list);
                }).start();
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    private void inidSql(List<AdressBean_two> list) {
        if (list != null && list.size() > 0) {
           new AddressDictManager(this,true,list);
        }

    }

}
