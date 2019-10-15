package com.dabangvr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dabangvr.R;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.BaseActivity;

/**
 * 发表动态
 */
public class CreateDynamicActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_create_dynamic;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}
