package com.dabangvr.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dabangvr.activity.LoginActivity;
import com.dabangvr.activity.MainActivity;
import com.dabangvr.activity.WellComePageActivity;
import com.dabangvr.application.MyApplication;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.dbvr.httplibrart.utils.OtherCallback;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import okhttp3.Call;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI mWeixinAPI;
    private static final String APP_SECRET = "93e6fe9703ba9e1b571a039bab64ef69";
    public static final String WEIXIN_APP_ID = "wx2351c48134140a3c";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        mWeixinAPI = MyApplication.api;
        mWeixinAPI.handleIntent(this.getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWeixinAPI.handleIntent(intent, this);//必须调用此句话
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq req) {
        Log.d("wechat", "onReq");
    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        Log.d("wechat", "onResp");
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
                    finish();
                    return;
                }
                String code = ((SendAuth.Resp) resp).code;
                getAccessToken(code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ToastUtil.showShort(WXEntryActivity.this, "取消");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ToastUtil.showShort(WXEntryActivity.this, "请求拒绝");
                finish();
                break;
            default:
                //发送返回
                finish();
                break;
        }
    }

    //66f9d1f7e2328f65339cdec1d993bd79
    //获取授权信息，即获取access_token 和 openId
    private void getAccessToken(String code) {
        String loginUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WEIXIN_APP_ID + "&secret=" + APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
        OkHttp3Utils.getInstance(this).doGetOther(loginUrl, new OtherCallback<String>(this) {
            @Override
            public void onUi(String result) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                String accessToken = jsonObject.optString("access_token");
                String openId = jsonObject.getString("openid");

                //获取用户信息
                getUserInfo(accessToken, openId);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param acc
     * @param openId
     */
    private void getUserInfo(String acc, String openId) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + acc + "&openid=" + openId + "";
        OkHttp3Utils.getInstance(this).doGetOther(url, new OtherCallback<String>(this) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (StringUtils.isEmpty(result)) {
                        finish();
                        return ;
                    }
                    String openId = object.optString("openid");
                    String nickname = object.optString("nickname");
                    String sex = object.optString("sex");
                    String headimgurl = object.optString("headimgurl");

                    //登陆后端
                    senMyServer(openId, nickname, headimgurl, "wechat");
                } catch (JSONException e) {
                    e.printStackTrace();
                    finish();
                }
            }

            @Override
            public void onFailed(String msg) {
                finish();
            }
        });
    }
    private void senMyServer(final String openID, final String uName, final String icon, final String type) {
        OkHttp3Utils.desInstance();
        HashMap<String, String> map = new HashMap<>();
        map.put("openId", openID);
        map.put("nickName", uName);
        map.put("icon", icon);
        map.put("loginType", type);

        OkHttp3Utils.getInstance(this).doPost(UserUrl.login, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    String userStr = data.optString("user");
                    SPUtils.instance(WXEntryActivity.this).putUser(userStr);

                    //判断是否首次登陆
                    boolean isFirst = (boolean) SPUtils.instance(getApplicationContext()).getkey("isFirst",true);
                    if (isFirst){//首次
                        Intent intent = new Intent(WXEntryActivity.this,WellComePageActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(WXEntryActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                    AppManager.getAppManager().finishActivity(WXEntryActivity.class);
                    AppManager.getAppManager().finishActivity(LoginActivity.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppManager.getAppManager().finishActivity(WXEntryActivity.class);
                }
            }
            @Override
            public void onFailed(String msg) {
                Toast.makeText(WXEntryActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                AppManager.getAppManager().finishActivity(WXEntryActivity.class);
            }
        });
    }
}
