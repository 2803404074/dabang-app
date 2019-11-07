package com.dabangvr.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dabangvr.R;

import com.dabangvr.application.MyApplication;
import com.dabangvr.wxapi.WXPayEntryActivity;
import com.dabangvr.wxapi.WXPayUtils;
import com.dabangvr.wxapi.WXPlayCallBack;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.rey.material.app.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PayDialog {

    private int checkPayId;//下标，0微信，1支付宝
    private Context mContext;
    private BottomSheetDialog dialog;

    private WXPlayCallBack wxPlayCallBack;
    private int price;

    public int getPrice() {
        return price;
    }

    public void setWxPlayCallBack(WXPlayCallBack wxPlayCallBack) {
        this.wxPlayCallBack = wxPlayCallBack;
    }

    /**
     * @param mContext
     */
    public PayDialog(Context mContext) {
        this.mContext = mContext;
    }

    private ProgressBar progressBar;
    public void showDialog(int price) {
        dialog = new BottomSheetDialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.orther_dialog, null);
        //支付价钱
        TextView tvPrice = view.findViewById(R.id.dialog_price);
        tvPrice.setText("¥" + price);
        this.price = price;
        progressBar = view.findViewById(R.id.loading);

        RadioGroup radioGroup = view.findViewById(R.id.orther_radio_group);
        radioGroup.setOnCheckedChangeListener((group, cId) -> {
            //获取下标
            checkPayId = group.indexOfChild(group.findViewById(cId));
        });
        //立即支付，当前跳过支付，直接提交
        view.findViewById(R.id.zf_now).setOnClickListener(v -> {
            if (checkPayId == 0) {//微信支付
                progressBar.setVisibility(View.VISIBLE);
                getWXMESS(price);//直接支付
            }
            if (checkPayId == 2) {//支付宝支付
                ToastUtil.showShort(mContext, "支付宝支付维护中，将于 2019/11/30 开放");
            }
        });
        dialog.contentView(view)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }

    /**
     * 获取微信支付需要的参数值
     *
     * @param * orderSnTotal和orderSn只能二选一
     *          1：立即购买=》直接支付     使用orderSnTotal
     *          2：立即购买=》取消=》重新付款     使用orderSnTotal
     *          3：立即购买=》取消=》查看订单=》订单详情=》去付款    使用orderSn
     *          4：购物车=》去付款    使用orderSnTotal
     *          5：购物车=》去付款=》取消=》重新付款    使用orderSnTotal
     *          6：购物车=》去付款=》取消=》查看订单=》订单详情=》去付款    使用orderSn
     */
    private void getWXMESS(int price) {
        Map<String, Object> map = new HashMap<>();
        map.put("price", price);
        OkHttp3Utils.getInstance(MyApplication.getInstance()).doPostJson(DyUrl.payDiamond, map, new ObjectCallback<String>(MyApplication.getInstance()) {
            @Override
            public void onUi(String result) throws JSONException {
                JSONObject object = new JSONObject(result);
                toWXPay(object);
            }
            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(mContext, msg);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void toWXPay(JSONObject object) {
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(object.optString("appid"))
                .setPartnerId(object.optString("partnerid"))
                .setPrepayId(object.optString("prepayid"))
                .setPackageValue(object.optString("package"))
                .setNonceStr(object.optString("noncestr"))
                .setTimeStamp(object.optString("timestamp"))
                .setSign(object.optString("sign"))
                .build().toWXPayNotSign(mContext);
        ToastUtil.showShort(mContext, "正在打开微信...");
        progressBar.setVisibility(View.GONE);

        WXPayEntryActivity.getInstance().setWxPlayCallBack(new WXPlayCallBack() {
            @Override
            public void success() {
                dialog.dismiss();
                wxPlayCallBack.success();
            }

            @Override
            public void error(String errorMessage) {
                dialog.dismiss();
                wxPlayCallBack.error(errorMessage);
            }

            @Override
            public void cancel() {
                dialog.dismiss();
                wxPlayCallBack.cancel();
            }

        });
    }

    public void desDialogView() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
