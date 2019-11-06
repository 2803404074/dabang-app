package com.dabangvr.fragment.other.Order;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.activity.LoginActivity;
import com.dbvr.baselibrary.utils.CacheUtil;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户编辑信息
 */
public class UserSettingActivity extends BaseActivity {

    @BindView(R.id.tv_memory_size)
    TextView tv_memory_size;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_setting;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        //获取缓存
        try {
            tv_memory_size.setText(CacheUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.ivBack, R.id.tv_logout, R.id.ll_memory_size, R.id.ll_version_update})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.ll_memory_size:
                show("清除缓存会导致应用所加载数据清空，但不包括您的个人信息，是否确定？");
                break;
            case R.id.ll_version_update:
                break;
            case R.id.tv_logout:
                DialogUtil.getInstance(this).show(R.layout.dialog_tip, holder -> {
                    holder.findViewById(R.id.tvCancel).setOnClickListener(view1 -> DialogUtil.getInstance(getContext()).des());
                    holder.findViewById(R.id.tvConfirm).setOnClickListener(view12 -> logOut());
                });
                break;
        }
    }

    private void logOut() {
        SPUtils.instance(this).removeUser();
        goTActivity(LoginActivity.class,null);
        AppManager.getAppManager().finishAllActivityTo(LoginActivity.class);
    }

    private void show(String mess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mess);
        //添加"Yes"按钮
        builder.setPositiveButton("确定", (dialogInterface, i) -> {

            CacheUtil.clearAllCache(UserSettingActivity.this);
            try {
                tv_memory_size.setText("缓存已清除");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //添加普通按钮
        builder.setNeutralButton("取消", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }

}
