package com.dbvr.baselibrary.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.dbvr.baselibrary.R;
import com.dbvr.baselibrary.ui.LoadingUtils;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.r0adkll.slidr.Slidr;



import java.util.Map;
import butterknife.ButterKnife;

/**
 * Created by 黄仕豪 on 2019/7/03
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
//        EventBus.getDefault().register(this); //第1步: 注册
        initView();
        initData();


        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    private LoadingUtils mLoaddingUtils;

    public void setLoaddingView(boolean isLoadding) {
        if (mLoaddingUtils == null) {
            mLoaddingUtils = new LoadingUtils(this);
        }
        if (isLoadding) {
            mLoaddingUtils.show();
        } else {
            mLoaddingUtils.dismiss();
        }
    }

    public void goTActivity(final Class T, Map<String,Object>map){
        new Thread(() -> {
            Intent intent = new Intent(BaseActivity.this,T);
            if (map!=null){
                for (String key : map.keySet()) {
                    if (map.get(key) instanceof Boolean){
                        intent.putExtra(key,(boolean)map.get(key));
                    }
                    if (map.get(key) instanceof String){
                        intent.putExtra(key,(String)map.get(key));
                    }
                    if (map.get(key) instanceof Integer){
                        intent.putExtra(key,(Integer)map.get(key));
                    }
                }
            }
            startActivity(intent);
        }).start();
    }

    public void goTActivityTou(final Class T, Map<String,Object>map){
        new Thread(() -> {
            Intent intent = new Intent(BaseActivity.this,T);
            if (map!=null){
                for (String key : map.keySet()) {
                    if (map.get(key) instanceof Boolean){
                        intent.putExtra(key,(boolean)map.get(key));
                    }
                    if (map.get(key) instanceof String){
                        intent.putExtra(key,(String)map.get(key));
                    }
                    if (map.get(key) instanceof Integer){
                        intent.putExtra(key,(Integer)map.get(key));
                    }
                }
            }
            startActivity(intent);
            overridePendingTransition(R.anim.activity_out,R.anim.activity_in);
        }).start();
    }

    public void goTActivityForResult(final Class T, Map<String,Object> map,int requestCode){
        if (T == null)return;
        Intent intent = new Intent(getContext(),T);
        if (map!=null){
            for (String key : map.keySet()) {
                if (map.get(key) instanceof Boolean){
                    intent.putExtra(key,(boolean)map.get(key));
                }
                if (map.get(key) instanceof String){
                    intent.putExtra(key,(String)map.get(key));
                }
                if (map.get(key) instanceof Integer){
                    intent.putExtra(key,(Integer)map.get(key));
                }
            }
        }
        startActivityForResult(intent,requestCode);
    }

    // 设置布局
    public abstract int setLayout();

    public abstract void initView();

    public abstract void initData();

    @Override
    protected void onDestroy() {

        setLoaddingView(false);
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();
    }

    /**
     * 設置右側返回
     */
    public void setSlidr(){
        Slidr.attach(this);
    }


    public Context getContext() {
        return this;
    }
    public String getToken(){
        return (String) SPUtils.instance(this.getContext()).getkey("token","");
    }
}
