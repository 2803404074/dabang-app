package com.dabangvr.comment.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import butterknife.BindView;

import com.addressselection.bean.AdressBean_two;
import com.addressselection.manager.AddressDictManager;
import com.dabangvr.R;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.util.MyAnimatorUtil;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.ScreenUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WellcomActivity extends BaseActivity {
    private TextView text_version;
    //private AddressBEAN addressBeans;

    @BindView(R.id.tvShow01)
    TextView tvShow01;
    @BindView(R.id.tvShow02)
    TextView tvShow02;
    private MyAnimatorUtil animatorUtil01;
    private MyAnimatorUtil animatorUtil02;

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
        text_version.setText(getVersion());

        animatorUtil01 = new MyAnimatorUtil(getContext(), tvShow01);
        animatorUtil02 = new MyAnimatorUtil(getContext(), tvShow02);
        new Thread(() -> {
            runOnUiThread(()->{
                try {
                    Thread.sleep(1000);
                    animatorUtil01.startAnimatorx(ScreenUtils.getScreenWidth(this));
                    animatorUtil02.startAnimatorx(ScreenUtils.getScreenWidth(this));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    @Override
    public void initData() {

        if (StringUtils.isEmpty( SPUtils.instance(this).getToken())){
            goTActivity(LoginActivity.class);//未登陆
        } else {
            getUserInfo();//已经登录，获取用户最新信息
        }
        getInitializationData();
    }

    private void getUserInfo() {
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.getUserInfo, null, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)){
                    ToastUtil.showShort(getContext(),"信息已过期，请重新登录");
                    goTActivity(LoginActivity.class);
                    return;
                }
                try {
                    UserMess userMess = new Gson().fromJson(result, UserMess.class);
                    if (userMess == null){
                        ToastUtil.showShort(getContext(),"信息已过期，请重新登录");
                        goTActivity(LoginActivity.class);
                    }else {
                        SPUtils.instance(getContext()).put("token", userMess.getToken());
                        SPUtils.instance(getContext()).putUser(userMess);
                        goTActivity(MainActivity.class);
                    }
                }catch (Exception e){
                    ToastUtil.showShort(getContext(),"信息已过期，请重新登录");
                    goTActivity(LoginActivity.class);
                }
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),msg);
                goTActivity(LoginActivity.class);
            }
        });
    }


    private void goTActivity(final Class T) {
        new Handler().postDelayed(() -> {
            animatorUtil01.stopAnimatorx(300);
            animatorUtil02.stopAnimatorx(300);
            Intent intent = new Intent(WellcomActivity.this, T);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_out,R.anim.activity_in);
            finish();
        }, 2000);
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
        animatorUtil01 = null;
        animatorUtil02 = null;
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
            public void onUi(String result) {
                String rep = "\"citycode\":[]";
                String repe = "\"citycode\":\"0\"";
                String replace = result.replace(rep, repe);
                List<AdressBean_two> list = new Gson().fromJson(replace, new TypeToken<List<AdressBean_two>>() {
                }.getType());

                new Thread(() -> {
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
            new AddressDictManager(this, true, list);
        }
    }

}
