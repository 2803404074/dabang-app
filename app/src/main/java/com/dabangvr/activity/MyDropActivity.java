package com.dabangvr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dabangvr.R;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 跳币中心
 */
public class MyDropActivity extends BaseActivity {

    @BindView(R.id.tvVip)
    TextView tvVip;
    @BindView(R.id.tvDrop)
    TextView tvDrop;

    //跳币视图
    @BindView(R.id.llDrop)
    LinearLayout llDrop;

    //vip视图
    @BindView(R.id.llContent)
    LinearLayout llContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_drop;
    }

    @Override
    public void initView() {
        showUI(true);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tvVip,R.id.tvDrop})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.tvVip:
                tvVip.setTextColor(getResources().getColor(R.color.colorAccentButton));
                tvDrop.setTextColor(getResources().getColor(R.color.colorGray4));
                showUI(true);
                break;
            case R.id.tvDrop:
                tvDrop.setTextColor(getResources().getColor(R.color.colorAccentButton));
                tvVip.setTextColor(getResources().getColor(R.color.colorGray4));
                showUI(false);
                break;
        }
    }

    /**
     * 显示控件
     * @param isVip true 显示充值vip UI
     */
    private void showUI(boolean isVip){
        if (isVip){
           llContent.setVisibility(View.VISIBLE);
           llDrop.setVisibility(View.GONE);
        }else {
            llContent.setVisibility(View.GONE);
            llDrop.setVisibility(View.VISIBLE);
        }
    }
}
