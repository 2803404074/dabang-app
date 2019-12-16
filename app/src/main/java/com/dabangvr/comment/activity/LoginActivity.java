package com.dabangvr.comment.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.dabangvr.R;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.BottomDialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }
    @Override
    public int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() { }
    @Override
    public void initData() { }

    @OnClick({R.id.wechat_login, R.id.qq_login, R.id.phone_login})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.wechat_login:
                setLoaddingView(true);
                login(Wechat.NAME);
                break;
            case R.id.qq_login:
                login(QQ.NAME);
                setLoaddingView(true);
                break;
            case R.id.phone_login:
                showBottomView();
                break;
            default:
                break;
        }
    }

    private void login(String loginName){
        Platform platform = ShareSDK.getPlatform(loginName);  //平台名称和上述5中的devInfo里的一样，后面接NAME，只有配置里有才会有对应的类
        if (platform.isAuthValid()){  //如果需要每次使用第三方登录时清除授权状态，则加入这段代码，否则一旦登录过后，就会在后续使用时直接授权登录
            platform.removeAccount(true);
        }
        platform.SSOSetting(false);  //设置为false时，有客户端的话会直接使用客户端，否则使用Web登录授权，第一次需要用户输入账号密码
        platform.setPlatformActionListener(new PlatformActionListener() {  //不设置监听的话会使用普通的Toast提示登录授权结果，自己设置监听的话可以做其他操作，或者使用自己app风格的Toast来提示结果
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //hashMap中储存很多相关的信息，不过建议使用PlatformDb，里面的信息更全，包含Token
                PlatformDb platformDb = platform.getDb();
                senMyServer(
                        platformDb.getToken(),
                        platformDb.getUserName(),
                        platformDb.getUserIcon(),
                        loginName);

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        platform.showUser(null);   //触发方法
    }

    private void senMyServer(final String openID, final String uName, final String icon, final String type) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("openId", openID);
        map.put("nickName", uName);
        map.put("icon", icon);
        map.put("loginType", type);
        Looper.prepare();
        OkHttp3Utils.getInstance(LoginActivity.this).doPostJson(UserUrl.login, map, new ObjectCallback<String>(LoginActivity.this) {
            @Override
            public void onUi(String result) {
                SPUtils.instance(getContext()).putUser(result);
                //判断是否首次登陆
                boolean isFirst = (boolean) SPUtils.instance(getContext()).getkey("isFirst", true);
                if (isFirst) {
                    goTActivityTou(WellComePageActivity.class, null);
                } else {
                    goTActivityTou(MainAc.class, null);
                }
            }

            @Override
            public void onFailed(String msg) {
                //Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                setLoaddingView(false);
            }
        });
        Looper.loop();
    }

    /**
     * 手机登陆底部弹窗
     */
    private BottomDialogUtil dialogUtil;
    private TextView tvGetCode;

    private void showBottomView() {
        dialogUtil = new BottomDialogUtil(this, R.layout.dialog_login, 1.02) {
            @Override
            public void convert(View holder) {
                //返回
                holder.findViewById(R.id.ivCancel).setOnClickListener(view -> dialogUtil.dess());
                //验证码
                EditText etCode = holder.findViewById(R.id.etCode);

                //获取验证码
                tvGetCode = holder.findViewById(R.id.tvGetCode);
                EditText etPhone = holder.findViewById(R.id.etPhone);
                tvGetCode.setOnClickListener(view -> {
                    setLoaddingView(true);
                    if (TextUtils.isEmpty(etPhone.getText().toString().trim())){
                        setLoaddingView(false);
                        return;
                    }
                    getMessage(etPhone.getText().toString());
                });

                //登陆
                TextView tvLogin = holder.findViewById(R.id.tvLogin);
                tvLogin.setOnClickListener(view -> {
                    if (TextUtils.isEmpty(etPhone.getText().toString())) {
                        ToastUtil.showShort(getContext(), "请输入手机号");
                        return;
                    }
                    setLoaddingView(true);
                    phoneLogin(etPhone.getText().toString(), etCode.getText().toString());
                });

                etCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (etCode.getText().toString().length()==6){
                                tvLogin.setEnabled(true);
                                tvLogin.setBackgroundResource(R.drawable.shape_db);
                            }else {
                                tvLogin.setEnabled(false);
                                tvLogin.setBackgroundResource(R.drawable.shape_touming);
                            }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        };
        dialogUtil.show();
    }

    /**
     * 手机登录方法
     */
    private void phoneLogin(String phone, String code) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);
        map.put("loginType", "PHONE");
        OkHttp3Utils.getInstance(this).doPostJson(UserUrl.login, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                SPUtils.instance(getContext()).putUser(result);
                //判断是否首次登陆
                boolean isFirst = (boolean) SPUtils.instance(getContext()).getkey("isFirst", true);
                if (isFirst) {
                    goTActivityTou(WellComePageActivity.class, null);
                } else {
                    goTActivityTou(MainAc.class, null);
                }
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
                ToastUtil.showShort(getContext(), msg);
            }
        });
    }

    /**
     * 请求后端发送短信
     */
    private void getMessage(String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        OkHttp3Utils.getInstance(this).doPostJson(UserUrl.sendCode, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                setLoaddingView(false);
                downDate();
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
                ToastUtil.showShort(getContext(), msg);
            }
        });
    }

    /**
     * 倒计时开始
     */
    private void downDate() {
        new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                tvGetCode.setText(millisUntilFinished / 1000 + "秒");
                tvGetCode.setClickable(false);
            }

            @Override
            public void onFinish() {
                tvGetCode.setClickable(true);
                tvGetCode.setText("重新获取");
            }
        }.start();
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
}
