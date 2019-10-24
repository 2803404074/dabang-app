package com.dabangvr.fragment.other.Order;

import android.os.Bundle;
import android.view.View;

import com.dabangvr.R;

import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import butterknife.OnClick;

/**
 * 商家入驻申请协议
 */
public class UserAgreeActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_agree;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;

        }
    }

}
