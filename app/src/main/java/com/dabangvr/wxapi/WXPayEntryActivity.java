package com.dabangvr.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.dbvr.baselibrary.other.ThirdParty.WECHART_APP_ID;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

    protected IWXAPI api;
    private static WXPayEntryActivity instance;
    private static WXPlayCallBack wxPlayCallBack;

    public static WXPayEntryActivity getInstance(){
        if (instance == null){
            instance = new WXPayEntryActivity();
        }
        return instance;
    }

    public void setWxPlayCallBack(WXPlayCallBack wxPlayCallBack) {
        this.wxPlayCallBack = wxPlayCallBack;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WECHART_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0){//支付成功
                if (wxPlayCallBack!=null){
                    wxPlayCallBack.success();
                    finish();
                }
            }else if (resp.errCode == -1){
                if (wxPlayCallBack!=null){//支付异常
                    wxPlayCallBack.error(resp.errStr);
                    finish();
                }
            } else if (resp.errCode == -2){
                if (wxPlayCallBack!=null){//支付取消
                    wxPlayCallBack.cancel();
                    finish();
                }
            }else {
                if (wxPlayCallBack!=null){//支付失败
                    wxPlayCallBack.error(resp.errStr);
                    finish();
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wxPlayCallBack = null;
        instance = null;
    }
}
