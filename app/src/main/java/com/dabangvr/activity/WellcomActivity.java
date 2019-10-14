package com.dabangvr.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import okhttp3.Call;

import com.dabangvr.R;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WellcomActivity extends BaseActivity {
    private TextView text_version;

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
            SPUtils.instance(this).put("token", userMess.getToken());
        }

        getAnchorList();
    }

    private void goTActivity(final Class T) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WellcomActivity.this, T);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    private void getAnchorList() {
        Map<String, String> map = new HashMap<>();
        map.put("page", "1");
        map.put("limit", "10");
        OkHttp3Utils.getInstance(getContext()).doPost(DyUrl.indexAnchorList, map,
                new ObjectCallback(getContext()) {
                    //主线程处理
                    @Override
                    public void onUi(Object msg) {
                        SPUtils.instance(getApplicationContext()).put("AnchorList", msg);

                    }

                    //请求失败
                    @Override
                    public void onFailed(String messsge) {
                        Log.d("luhuas", "onFailed: "+messsge);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);


                    }
                });
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
}
