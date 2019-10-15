package com.dabangvr.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.dabangvr.live.activity.CreateLiveActivity;
import com.dabangvr.live.activity.LiveActivity;
import com.dbvr.baselibrary.eventBus.ReadEvent;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;


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
        UserMess userMess = SPUtils.instance(this).getUser();
        if (null == userMess){
            goTActivity(LoginActivity.class,null);
            AppManager.getAppManager().finishActivity(MainActivity.class);
        }

        if (userMess.isNewsUser()){
            registerHX(String.valueOf(userMess.getId()),"123");
        }else {
            loginToHx(String.valueOf(userMess.getId()),"123");
        }
    }

    private void registerHX(final String name, final String pass) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMClient.getInstance().createAccount(name, pass);
                    loginToHx(name, pass);
                    UserMess userMess =  SPUtils.instance(getContext()).getUser();
                    userMess .setNewsUser(false);
                    SPUtils.instance(getContext()).putUser(userMess.toString());
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
        String unist = "";
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                changeFragment(0);
                menuItem.setChecked(true);
                unist = "0";
                break;
            case R.id.navigation_tc:
                changeFragment(1);
                menuItem.setChecked(true);
                unist = "1";
                break;

            case R.id.navigation_fb:
                //功能菜单
                showFunction();
                break;
            case R.id.navigation_live:
                changeFragment(2);
                menuItem.setChecked(true);
                unist = "2";
                break;
            case R.id.navigation_my:
                changeFragment(3);
                menuItem.setChecked(true);
                unist = "3";
                break;
        }

        EventBus.getDefault().post(new ReadEvent("1000", 1000, unist));
        return false;
    }

    private void showFunction() {
        BottomDialogUtil2.getInstance(this).show(R.layout.dialog_main_function, 0, view -> {
            view.findViewById(R.id.tvOpenLive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goTActivity(CreateLiveActivity.class,null);
                    BottomDialogUtil2.getInstance(MainActivity.this).dess();
                }
            });
            view.findViewById(R.id.tvOpenVideo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomDialogUtil2.getInstance(MainActivity.this).dess();
                }
            });
            view.findViewById(R.id.tvOpenDynamic).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomDialogUtil2.getInstance(MainActivity.this).dess();
                }
            });
            view.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomDialogUtil2.getInstance(MainActivity.this).dess();
                }
            });
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
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void change(boolean isCheck,boolean s) {
        if (isCheck) {
            navView.getBackground().setAlpha(0);
        } else {
            navView.getBackground().setAlpha(255);
            navView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BottomDialogUtil2.getInstance(this).dess();
    }
}
