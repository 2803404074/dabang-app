package com.dabangvr.fragment.other.Order;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.activity.MainActivity;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商家申请成功显示。主播申请显示成功
 */
public class UserApplySuccessActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_content)
    TextView tv_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_apply_success;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        String name = getIntent().getStringExtra("name");
        tv_title.setText(name);
        tv_content.setText(name + "申请提交成功,后台正在努力进行审核！" + "\n审核结果讲发送到您的手机上,请耐心等候！");
    }

    @OnClick({R.id.ivBack, R.id.tv_back})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tv_back:
                AppManager.getAppManager().finishAllActivityTo(MainActivity.class);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            AppManager.getAppManager().finishAllActivityTo(MainActivity.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
