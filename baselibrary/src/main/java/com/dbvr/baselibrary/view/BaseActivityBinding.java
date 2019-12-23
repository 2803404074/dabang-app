package com.dbvr.baselibrary.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.dbvr.baselibrary.R;
import com.dbvr.baselibrary.ui.LoadingUtils;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.UserHolper;
import com.r0adkll.slidr.Slidr;

import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by 黄仕豪 on 2019/7/03
 */

public abstract class BaseActivityBinding<DB extends ViewDataBinding> extends AppCompatActivity {
    public DB mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        AppManager.getAppManager().addActivity(this);
        mBinding = DataBindingUtil.setContentView(this,setLayout());
        initView();
        initData();
        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }

    }
    public void goTActivity(final Class T, Map<String,Object>map){
        new Thread(() -> {
            Intent intent = new Intent(BaseActivityBinding.this,T);
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
            Intent intent = new Intent(BaseActivityBinding.this,T);
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


    public void goTActivityIntent(final Class T, Intent intent){
        if (T == null)return;
        startActivity(intent);
    }

    public void isLoading(boolean t){
        if (t){
            DialogUtil.getInstance(this).showAn(R.layout.loading_layout,false, view -> {});
        }else {DialogUtil.getInstance(this).des(); }
    }


    // 设置布局
    public abstract int setLayout();

    public abstract void initView ();

    public abstract void initData();

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().finishActivity(this);
        DialogUtil.getInstance(this).des();
        super.onDestroy();
    }
    public Activity getContext() {
        return this;
    }

    public String getToken(){
        return UserHolper.getUserHolper(getContext()).getToken();
    }
}
