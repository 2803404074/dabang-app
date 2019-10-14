package com.dabangvr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.application.MyApplication;
import com.dabangvr.wxapi.WXEntryActivity;
import com.dabangvr.wxapi.WchatLogin;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.BottomDialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.dbvr.baselibrary.other.ThirdParty.QQ_APP_ID;
import static com.dbvr.baselibrary.other.ThirdParty.WECHART_APP_ID;

public class LoginActivity extends BaseActivity implements IUiListener {
    private Tencent mTencent;
    private UserInfo mUserInfo;

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
    public void initView() {
        mTencent = Tencent.createInstance(QQ_APP_ID, LoginActivity.this.getApplicationContext());
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.wechat_login, R.id.qq_login, R.id.phone_login})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.wechat_login:
                setLoaddingView(true);

                wechatLogin();
                break;
            case R.id.qq_login:
                mTencent.login(LoginActivity.this, "all", this);
                setLoaddingView(true);
                break;
            case R.id.phone_login:
                showBottomView();
                break;
            default:
                break;
        }
    }

    /**
     * 手机登陆底部弹窗
     */
    private BottomDialogUtil dialogUtil;
    private TextView tvGetCode;
    private void showBottomView() {
        dialogUtil  = new BottomDialogUtil(this,R.layout.dialog_login,1.02) {
            @Override
            public void convert(View holder) {

                //返回
               holder.findViewById(R.id.ivCancel).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       dialogUtil.dess();
                   }
               });

               //验证码
                EditText etCode = holder.findViewById(R.id.etCode);

               //获取验证码
               tvGetCode = holder.findViewById(R.id.tvGetCode);
               EditText etPhone = holder.findViewById(R.id.etPhone);
               tvGetCode.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (TextUtils.isEmpty(etPhone.getText().toString()))return;
                        getMessage(etPhone.getText().toString());
                   }
               });

               //登陆
               holder.findViewById(R.id.tvLogin).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (TextUtils.isEmpty(etPhone.getText().toString())){
                           ToastUtil.showShort(getContext(),"请输入手机号");
                           return;
                       }
                       if (TextUtils.isEmpty(etCode.getText().toString())){
                           ToastUtil.showShort(getContext(),"请输入验证码");
                           return;
                       }
                       setLoaddingView(true);
                       phoneLogin(etPhone.getText().toString(),etCode.getText().toString());
                   }
               });
            }
        };
        dialogUtil.show();
    }

    /**
     * 手机登录方法
     */
    private void phoneLogin(String phone,String code) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);
        map.put("loginType", "PHONE");
        OkHttp3Utils.getInstance(this).doPostJson(UserUrl.login, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result){
                setLoaddingView(false);
                Gson gson = new Gson();
                UserMess userMess = gson.fromJson(result,UserMess.class);
                if (userMess!=null){
                    SPUtils.instance(getContext()).putUser(result);
                    SPUtils.instance(getContext()).putObj("token",userMess.getToken());
                    goTActivity(MainActivity.class,null);
                    AppManager.getAppManager().finishActivity(LoginActivity.class);
                }else {
                    ToastUtil.showShort(getContext(),"登陆异常");
                }
            }
            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
                ToastUtil.showShort(getContext(),msg);
            }
        });
    }

    /**
     * 请求后端发送短信
     */
    private void getMessage(String phone){
        Map<String,Object>map = new HashMap<>();
        map.put("phone",phone);
        OkHttp3Utils.getInstance(this).doPostJson(UserUrl.sendCode, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result){
                downDate();
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),msg);
            }
        });
    }
    /**
     * 倒计时开始
     */
    private void downDate(){
        new CountDownTimer(60*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                tvGetCode.setText(millisUntilFinished/1000+"秒");
                tvGetCode.setClickable(false);
            }

            @Override
            public void onFinish() {
                tvGetCode.setClickable(true);
                tvGetCode.setText("重新获取");
            }
        }.start();
    }


    /**
     * 微信登陆
     */
    private void wechatLogin() {
        if (MyApplication.api == null) {
            MyApplication.api = WXAPIFactory.createWXAPI(this, WECHART_APP_ID, true);
        }
        if (!MyApplication.api.isWXAppInstalled()) {
            setLoaddingView(false);
            ToastUtil.showShort(this, "您手机尚未安装微信，请安装后再登录");
            return;
        }
        MyApplication.api.registerApp(WECHART_APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_xb_live_state";//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
        MyApplication.api.sendReq(req);
    }



    @Override
    public void onComplete(Object response) {
        Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
        JSONObject obj = (JSONObject) response;
        try {
            final String openID = obj.getString("openid");
            String accessToken = obj.getString("access_token");
            String expires = obj.getString("expires_in");
            mTencent.setOpenId(openID);
            mTencent.setAccessToken(accessToken, expires);
            QQToken qqToken = mTencent.getQQToken();//这个应该是qq登陆成功后构造一个返回信息方便序列化
            mUserInfo = new UserInfo(getApplicationContext(), qqToken);
            mUserInfo.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object response) {
                    JSONObject objectResult = null;
                    try {
                        objectResult = new JSONObject(response.toString());
                        String uName = objectResult.optString("nickname");//获取第三方返回的昵称
                        String icon = objectResult.optString("figureurl_2");//第三方头像
                        String type = "qq";//登陆类型
                        senMyServer(openID, uName, icon, type);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    setLoaddingView(false);
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, "登录取消", Toast.LENGTH_SHORT).show();
                    setLoaddingView(false);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(UiError uiError) {
        Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void senMyServer(final String openID, final String uName, final String icon, final String type) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("openId", openID);
        map.put("nickName", uName);
        map.put("icon", icon);
        map.put("loginType", type);
        OkHttp3Utils.getInstance(this).doPostJson(UserUrl.login, map, new ObjectCallback<String>(this) {

            @Override
            public void onUi(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    String userStr = data.optString("user");
                    SPUtils.instance(getContext()).putUser(userStr);
                    //判断是否首次登陆
                    boolean isFirst = (boolean) SPUtils.instance(getContext()).getkey("isFirst",true);
                    if (isFirst){
                        goTActivity(WellComePageActivity.class,null);
                    }else {
                        goTActivity(MainActivity.class,null);
                    }
                    AppManager.getAppManager().finishActivity(LoginActivity.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String msg) {
                Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                setLoaddingView(false);
            }
        });
    }

}
