package com.dabangvr.comment.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dabangvr.R;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.comment.service.MessageService;
import com.dabangvr.home.fragment.VideoFragment;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dabangvr.comment.view.NavHelper;
import com.dabangvr.home.fragment.CityFragment;
import com.dabangvr.home.fragment.HomeFragmentHome;
import com.dabangvr.home.fragment.MessageFragment;
import com.dabangvr.home.fragment.MyFragment;
import com.dabangvr.live.activity.GeGoActivity;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity;

import java.util.List;

import butterknife.BindView;

public class MainAc extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavHelper.OnTabChangeListener<String> {

    public static MainAc mainInstance;
    @BindView(R.id.bnvView)
    BottomNavigationView bottomNavigationView;

    private Intent intent;

    private NavHelper<String> mNavHelper;

    private TextView tvMessCount;

    public void setMessageCount(int count){
        int b = Integer.parseInt(tvMessCount.getText().toString());
        int c = b-count;
        if (c==0){
            tvMessCount.setVisibility(View.GONE);
        }
        tvMessCount.setText(String.valueOf(c));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainInstance = this;
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_main2;
    }

    @Override
    public void initView() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        //这里就是获取所添加的每一个Tab(或者叫menu)，
        View tab = menuView.getChildAt(3);
        BottomNavigationItemView itemView = (BottomNavigationItemView) tab;
        //加载我们的角标View，新创建的一个布局
        View badge = LayoutInflater.from(MainAc.this).inflate(R.layout.menu_badge, menuView, false);
        tvMessCount = badge.findViewById(R.id.tvTab);
        //添加到Tab上
        itemView.addView(badge);


        findViewById(R.id.rlFunction).setOnClickListener((view) -> {
            showFunction();
        });
        mNavHelper = new NavHelper<String>(this, R.id.fg_layout, getSupportFragmentManager(), this);
        mNavHelper.add(R.id.navigation_home, new NavHelper.Tab<>(HomeFragmentHome.class, "aaa"))
                .add(R.id.navigation_tc, new NavHelper.Tab<>(CityFragment.class, "aaa"))
                .add(R.id.navigation_mess, new NavHelper.Tab<>(MessageFragment.class, "aaa"))
                .add(R.id.navigation_my, new NavHelper.Tab<>(MyFragment.class, "aaa"));

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        menu.performIdentifierAction(R.id.navigation_home, 0);
    }

    @Override
    public void initData() {
        UserMess userMess = UserHolper.getUserHolper(getContext()).getUserMess();
        //直接去登录
        if (null == userMess) {
            goTActivity(LoginActivity.class, null);
            AppManager.getAppManager().finishActivity(MainAc.class);
            return;
        } else {
            //更新用户信息
            getUserInfo();
        }
    }


    private void registerHX(final String name, final String pass) {
        new Thread(() -> {
            try {
                // 调用sdk注册方法
                EMClient.getInstance().createAccount(name, pass);
                loginToHx(name, pass);
                UserMess userMess = SPUtils.instance(getContext()).getUser();
                userMess.setNewsUser(false);
                SPUtils.instance(getContext()).putUser(userMess);
            } catch (final HyphenateException e) {
                e.printStackTrace();
                int errorCode = e.getErrorCode();
                if (errorCode == EMError.USER_ALREADY_EXIST) {
                    loginToHx(name, pass);
                }
            }
        }).start();
    }

    private void loginToHx(String name, String psd) {
        EMClient.getInstance().login(name, psd, new EMCallBack() {
            @Override
            public void onSuccess() {
                //注册消息监听服务
                intent = new Intent(MainAc.this, MessageService.class);
                startService(intent);
                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                //获取所有未读消息数量，为底部导航栏添加角标
                int messCount = EMClient.getInstance().chatManager().getUnreadMessageCount();
                if (messCount != 0) {
                    runOnUiThread(() -> {
                        tvMessCount.setVisibility(View.VISIBLE);
                        tvMessCount.setText(String.valueOf(messCount));
                    });
                } else {
                    runOnUiThread(()->{
                        tvMessCount.setVisibility(View.GONE);
                    });
                }

                new Handler(getMainLooper()).postDelayed(() -> {
                    MessageService.service.setCallBack(messages -> {
                        if (tvMessCount != null) {
                            runOnUiThread(()->{
                                int a = Integer.parseInt(tvMessCount.getText().toString());
                                a++;
                                tvMessCount.setText(String.valueOf(a));
                                tvMessCount.setVisibility(View.VISIBLE);
                            });
                        }
                    });
                },2000);
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                if (code == 202) {
                    //ToastUtil.showShort(getContext(),"密码错误");
                    return;
                }
                if (code == EMError.USER_NOT_FOUND) {
                    //找不到用户,去注册
                    registerHX(name, psd);
                }
            }
        });
    }

    private void goLogin() {
        goTActivity(LoginActivity.class, null);
        AppManager.getAppManager().finishActivity(MainActivity.class);
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
                        SPUtils.instance(getContext()).put("token", userMess.getToken());
                        SPUtils.instance(getContext()).putUser(userMess);
                        UserHolper.getUserHolper(getContext()).upUserMess();
                        if (userMess.isNewsUser()) {
                            registerHX(String.valueOf(userMess.getId()), "com.haitiaotiao");
                        } else {
                            loginToHx(String.valueOf(userMess.getId()), "com.haitiaotiao");
                        }
                    }
                } catch (Exception e) {
                    ToastUtil.showShort(getContext(), "信息已过期，请重新登录");
                    goLogin();
                }
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(), msg);
                goLogin();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_function) {
            return false;
        } else {
            return mNavHelper.performClickMenu(item.getItemId());
        }
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().AppExit(this);
                UserHolper.getUserHolper(getContext()).ondessUser();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainInstance = null;
        MessageService.service = null;
        UserHolper.getUserHolper(getContext()).ondessUser();
        stopService(intent);
    }

    @Override
    public void onTabChange(NavHelper.Tab<String> newTab, NavHelper.Tab<String> oldTab) {

    }

    private void showFunction() {
        BottomDialogUtil2.getInstance(this).show(R.layout.dialog_main_function, 0, view -> {
            view.findViewById(R.id.tvOpenLive).setOnClickListener(view13 -> {
                //goTActivity(CreateLiveActivity.class,null);
                checkPermission();
                BottomDialogUtil2.getInstance(this).dess();
            });
            view.findViewById(R.id.tvOpenVideo).setOnClickListener(view12 -> {
                startVideoRecordActivity();
                BottomDialogUtil2.getInstance(this).dess();
            });
            view.findViewById(R.id.ivClose).setOnClickListener(view1 -> BottomDialogUtil2.getInstance(this).dess());
        });
    }

    private void startVideoRecordActivity() {
        MyApplication.getInstance().initShortVideo();
        TCVideoRecordActivity.openRecordActivity(this);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 222);
                return;
            } else {
                goTActivity(GeGoActivity.class, null);
            }
        } else {
            goTActivity(GeGoActivity.class, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 222) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goTActivity(GeGoActivity.class, null);
            } else {
                ToastUtil.showShort(getContext(), "您已禁用了相机权限，将无法使用开播功能");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



}
