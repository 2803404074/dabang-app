package com.dabangvr.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dabangvr.R;
import com.dabangvr.fragment.MessageFragment;
import com.dabangvr.fragment.MyFragment;
import com.dabangvr.fragment.SameCityFragment;
import com.dabangvr.fragment.home.HomeFragment;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;

import static com.dbvr.baselibrary.other.ThirdParty.ALL_HY_PASS;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, HomeFragment.ChangeCallBack {

    @BindView(R.id.container)
    RelativeLayout relativeLayout;

    @BindView(R.id.nav_view)
    BottomNavigationView navView;

    @BindView(R.id.fg_content)
    FrameLayout frameLayout;

    private HomeFragment homeFragment;
    private SameCityFragment sameCityFragment;
    private MessageFragment messageFragment;
    private MyFragment myFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initView() {
        navView.setLabelVisibilityMode(1);
        fragmentManager = getSupportFragmentManager();
        navView.setOnNavigationItemSelectedListener(this);
        changeFragment(0);

    }

    @Override
    public void initData() {
        // TODO: 2019/10/8 屏蔽登陆 放开全部就可以了
//        UserMess userMess = SPUtils.instance(this).getUser();
//        if (null == userMess){
//            goTActivity(LoginActivity.class,null);
//            AppManager.getAppManager().finishActivity(MainActivity.class);
//        }else {
//            //登陆环信
//            boolean isLoginHy = (boolean) SPUtils.instance(this).getkey("isLoginHy",false);
//            if (isLoginHy){
//                loginToHx(String.valueOf(userMess.getId()),ALL_HY_PASS);
//            }else {
//                registerHX(String.valueOf(userMess.getId()),ALL_HY_PASS);
//            }
//        }
    }


    /**
     * 注册
     */
    private void registerHX(final String name, final String pass) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMClient.getInstance().createAccount(name, pass);
                    loginToHx(name, pass);
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    int errorCode=e.getErrorCode();
                    if(errorCode == EMError.USER_ALREADY_EXIST){
                        loginToHx(name, pass);
                    }
                }
            }
        }).start();
    }

    /**
     * /登陆环信
     * @param name
     * @param psd
     */
    private void loginToHx(String name,String psd){
        EMClient.getInstance().login(name, psd, new EMCallBack() {
            @Override
            public void onSuccess() {
                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
//                ToastUtil.showShort(getContext(),"环信登陆成功");
                SPUtils.instance(getContext()).put("isLoginHy",true);
            }

            @Override
            public void onProgress(int progress, String status) {
            }
            @Override
            public void onError(final int code, final String message) {
                Log.e("aaa",message);
                //ToastUtil.showShort(getContext(),message);
            }
        });
    }





    public void changeFragment(int index) {
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        hideFragments(beginTransaction);
        switch (index) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    homeFragment.setChangeCallBack(this);
                    beginTransaction.add(R.id.fg_content, homeFragment);
                } else {
                    beginTransaction.show(homeFragment);
                }
                break;
            case 1:
                if (sameCityFragment == null) {
                    sameCityFragment = new SameCityFragment();
                    beginTransaction.add(R.id.fg_content, sameCityFragment);
                } else {
                    beginTransaction.show(sameCityFragment);
                }
                break;
            case 2:
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    beginTransaction.add(R.id.fg_content, messageFragment);
                } else {
                    beginTransaction.show(messageFragment);
                }
                break;
            case 3:
                if (myFragment == null) {
                    myFragment = new MyFragment();
                    beginTransaction.add(R.id.fg_content, myFragment);
                } else {
                    beginTransaction.show(myFragment);
                }
                break;
            default:
                break;
        }
        //需要提交事务
        beginTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        /*****/
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }

        if (sameCityFragment != null) {
            transaction.hide(sameCityFragment);
        }

        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }

        if (myFragment != null) {
            transaction.hide(myFragment);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                changeFragment(0);
                menuItem.setChecked(true);
                break;
            case R.id.navigation_tc:
                changeFragment(1);
                menuItem.setChecked(true);
                break;
            case R.id.navigation_live:
                changeFragment(2);
                menuItem.setChecked(true);
                break;
            case R.id.navigation_my:
                changeFragment(3);
                menuItem.setChecked(true);
                break;
        }
        return false;
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
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void change(boolean isCheck) {
        if (isCheck){
            navView.getBackground().setAlpha(0);
        }else {
            navView.getBackground().setAlpha(255);
            navView.setVisibility(View.VISIBLE);
        }
    }
}
