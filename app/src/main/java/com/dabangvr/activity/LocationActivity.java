package com.dabangvr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dabangvr.R;
import com.dbvr.baselibrary.ui.ShowButtonLayout;
import com.dbvr.baselibrary.ui.ShowButtonLayoutData;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class LocationActivity extends BaseActivity {

    @BindView(R.id.sblLocation)
    ShowButtonLayout showButtonLayout;
    private List<String> listHots = new ArrayList<>();//地区

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_location;
    }

    @Override
    public void initView() {
        for (int i = 0; i < 36; i++) {
            listHots.add("广西省");
        }

        ShowButtonLayoutData data1 = new ShowButtonLayoutData<String>(this, showButtonLayout,
                listHots, new ShowButtonLayoutData.MyClickListener() {
            @Override
            public void clickListener(View v, String txt, double arg1, double arg2, boolean isCheck) {
                Intent i = new Intent();
                i.putExtra("result", txt);
                setResult(103, i);
                finish();
            }
        });
        data1.setDrawableBg(R.drawable.shape_gray);
        data1.setData();
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
            default:
                break;
        }
    }
}
