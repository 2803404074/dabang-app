package com.dabangvr.comment.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.im.service.MessageService;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.utils.UserHolper;
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
        getUserInfo();
        text_version = this.findViewById(R.id.text_version);
        text_version.setText(getVersion());
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
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
        Handler handler = new Handler();
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

    private void goLogin(){
        new Handler().postDelayed(() -> {
            startActivity(LoginActivity.class);
        },1500);
    }
    private void goDow(){
        new Handler().postDelayed(() -> {
            startActivity(CownTimerActivity.class);
        },1500);
    }

    private void getUserInfo() {
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.getUserInfo, null, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                if (StringUtils.isEmpty(result)) {
                    goLogin();
                    return;
                }
                try {
                    UserMess userMess = new Gson().fromJson(result, UserMess.class);
                    if (userMess == null) {
                        ToastUtil.showShort(getContext(), "信息已过期，请重新登录");
                        goLogin();
                    } else {
                        SPUtils.instance(getContext()).putUser(userMess);
                        SPUtils.instance(getContext()).put("token",userMess.getToken());
                        UserHolper.getUserHolper(getContext()).setUserMess(userMess);
                        goDow();
                    }
                } catch (Exception e) {
                    ToastUtil.showShort(getContext(), "信息已过期，请重新登录");
                    goLogin();
                }
            }

            @Override
            public void onFailed(String msg) {
                goLogin();
            }
        });
    }
}
