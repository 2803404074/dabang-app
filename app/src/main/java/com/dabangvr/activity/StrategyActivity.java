package com.dabangvr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.dabangvr.R;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;

public class StrategyActivity extends BaseActivity {

    @BindView(R.id.ivBack)
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_strategy;
    }

    @Override
    public void initView() {
        imageView.setOnClickListener(view ->{
            AppManager.getAppManager().finishActivity(this);
        });
    }

    @Override
    public void initData() {

    }
}
