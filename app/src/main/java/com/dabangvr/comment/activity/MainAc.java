package com.dabangvr.comment.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dabangvr.R;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.home.fragment.VideoFragment;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity;

import butterknife.BindView;

public class MainAc extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavHelper.OnTabChangeListener<String> {

    @BindView(R.id.bnvView)
    BottomNavigationView bottomNavigationView;

    private NavHelper<String> mNavHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }
    @Override
    public int setLayout() {
        return R.layout.activity_main2;
    }

    @Override
    public void initView() {
        findViewById(R.id.rlFunction).setOnClickListener((view)->{
            showFunction();
        });
        mNavHelper = new NavHelper<String>(this,R.id.fg_layout,getSupportFragmentManager(),this);
        mNavHelper.add(R.id.navigation_home,new NavHelper.Tab<>(HomeFragmentHome.class,"aaa"))
                .add(R.id.navigation_tc,new NavHelper.Tab<>(CityFragment.class,"aaa"))
                .add(R.id.navigation_mess,new NavHelper.Tab<>(MessageFragment.class,"aaa"))
                .add(R.id.navigation_my,new NavHelper.Tab<>(MyFragment.class,"aaa"));

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        menu.performIdentifierAction(R.id.navigation_home,0);
    }

    @Override
    public void initData() {
        // TODO: 2019/10/8 屏蔽登陆 放开全部就可以了
        UserMess userMess = SPUtils.instance(this).getUser();
        if (null == userMess){
            goTActivity(LoginActivity.class,null);
            AppManager.getAppManager().finishActivity(MainAc.class);
            return;
        }

        if (userMess.isNewsUser()){
            registerHX(String.valueOf(userMess.getId()),"123");
        }else {
            loginToHx(String.valueOf(userMess.getId()),"123");
        }

    }


    private void registerHX(final String name, final String pass) {
        new Thread(() -> {
            try {
                // 调用sdk注册方法
                EMClient.getInstance().createAccount(name, pass);
                loginToHx(name, pass);
                UserMess userMess =  SPUtils.instance(getContext()).getUser();
                userMess .setNewsUser(false);
                SPUtils.instance(getContext()).putUser(userMess);
            } catch (final HyphenateException e) {
                e.printStackTrace();
                int errorCode=e.getErrorCode();
                if(errorCode == EMError.USER_ALREADY_EXIST){
                    loginToHx(name, pass);
                }
            }
        }).start();
    }

    private void loginToHx(String name,String psd){
        EMClient.getInstance().login(name, psd, new EMCallBack() {
            @Override
            public void onSuccess() {
                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
            }

            @Override
            public void onProgress(int progress, String status) {
            }
            @Override
            public void onError(final int code, final String message) {
                if (code == 202){
                    //ToastUtil.showShort(getContext(),"密码错误");
                    return;
                }
                if(code == EMError.USER_NOT_FOUND){
                    //找不到用户,去注册
                    registerHX(name, psd);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_function){
            return false;
        }else {
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
        UserHolper.getUserHolper(getContext()).ondessUser();
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
        if (requestCode == 222){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goTActivity(GeGoActivity.class, null);
            } else {
                ToastUtil.showShort(getContext(),"您已禁用了相机权限，将无法使用开播功能");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
