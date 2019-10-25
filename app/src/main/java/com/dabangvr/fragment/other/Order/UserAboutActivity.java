package com.dabangvr.fragment.other.Order;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.activity.MainActivity;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.VersionUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于我们
 */
public class UserAboutActivity extends BaseActivity {


    private static String versionName;
    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.tv_version)
    TextView tv_version;

    @BindView(R.id.tv_version_t)
    TextView tv_version_t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_about;
    }

    @Override
    public void initView() {
        String versionName = VersionUtil.getAppVersionCode(this);
        tv_version.setText(versionName);
        tv_version_t.setText(versionName);
    }

    @Override
    public void initData() {

        tv_content.setText("fe fef efe fe fef ef ef efe fe wf wefewofwepkfwef wef fewofwef ");
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
