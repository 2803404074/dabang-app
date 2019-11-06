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
        //如果存在订单页面，结束掉
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0){//跳转支付成功页面
                ToastUtil.showShort(WXPayEntryActivity.this,"支付成功");
            }else {
                ToastUtil.showShort(WXPayEntryActivity.this,"支付失败");
            }
        }
        finish();
    }
}
