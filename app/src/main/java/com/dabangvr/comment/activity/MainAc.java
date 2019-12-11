package com.dabangvr.comment.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;

import com.dabangvr.R;
import com.dabangvr.comment.service.UserHolper;
import com.dabangvr.comment.view.BottomBar;
import com.dabangvr.home.fragment.CityFragment;
import com.dabangvr.home.fragment.HomeFragmentHome;
import com.dabangvr.home.fragment.MessageFragment;
import com.dabangvr.home.fragment.MyFragment;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import butterknife.BindView;

@SuppressLint("Registered")
public class MainAc extends BaseActivity {
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_main2;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void initView() {
        bottomBar.setContainer(R.id.fg_layout)
                .setTitleBeforeAndAfterColor("#999999", "#24ecbb")
                .addItem(HomeFragmentHome.class,
                        "首页",
                        R.mipmap.home_no,
                        R.mipmap.home_click)
                .addItem(CityFragment.class,
                        "同城",
                        R.mipmap.main_tc,
                        R.mipmap.main_tc_click)
                .addItem(MessageFragment.class,
                        "消息",
                        R.mipmap.main_mess_no,
                        R.mipmap.main_mess_click)
                .addItem(MyFragment.class,
                        "消息",
                        R.mipmap.main_my_no,
                        R.mipmap.main_my_click)
                .build();
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
}
