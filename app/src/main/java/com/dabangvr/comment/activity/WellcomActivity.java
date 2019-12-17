package com.dabangvr.comment.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.comment.service.UserService;
import com.dabangvr.util.MyAnimatorUtil;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.ScreenUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import butterknife.BindView;

public class WellcomActivity extends BaseActivity {
    private TextView text_version;
    //private AddressBEAN addressBeans;

    @BindView(R.id.tvShow02)
    TextView tvShow02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_wellcom;
    }

    @Override
    public void initView() {
        text_version = this.findViewById(R.id.text_version);
        text_version.setText(getVersion());
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        new Handler().postDelayed(() -> {
            //如果已经登陆，则获取用户信息
            if (!StringUtils.isEmpty( SPUtils.instance(getContext()).getToken())){
                Intent intent = new Intent(getContext(), UserService.class);
                startService(intent);
            }
            startActivity(CownTimerActivity.class);
        },1500);

        tvShow02.animate()
                .translationX((float) (outMetrics.widthPixels*0.2))
                .setDuration(2000)
                .setInterpolator(new DecelerateInterpolator())//加速
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tvShow02.animate().translationX(outMetrics.widthPixels).setDuration(1000).setInterpolator(new AccelerateInterpolator());//加速
            }
        });
    }

    @Override
    public void initData() {

    }

    private void startActivity(final Class T) {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WellcomActivity.this, T);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_out,R.anim.activity_in);
            finish();
        }, 2000);
    }

    //获取版本号
    private String getVersion() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException eArp) {
        }
        return "";
    }
}
