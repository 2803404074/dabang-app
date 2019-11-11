package com.dabangvr.activity;

import butterknife.BindView;
import butterknife.OnClick;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dabangvr.R;
import com.dbvr.baselibrary.eventBus.ReadEvent;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class PhoneBindActivity extends BaseActivity {
    @BindView(R.id.tv_phone)
    EditText tv_phone;
    @BindView(R.id.tv_phoneCode)
    EditText tv_phoneCode;
    private CountDownTimer count;
    @BindView(R.id.getphoneCode)
    TextView getphoneCode;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_phone_bind;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack, R.id.getphoneCode,R.id.tv_sub})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(PhoneBindActivity.class);
                break;
            case R.id.getphoneCode:
                phone = tv_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(getContext(), "请输入手机号码");
                    return;
                }
                getMessage(phone);
                break;
            case R.id.tv_sub:
                String phoneCode = tv_phoneCode.getText().toString().trim();
                if (TextUtils.isEmpty(phoneCode)) {
                    ToastUtil.showShort(getContext(), "请输入手机验证码");

                    return;
                }
                setLoaddingView(true);
                next(phone, phoneCode, "0");
                break;

        }
    }


    /**
     * 获取手机验证码
     *
     * @param phone
     */
    private void getMessage(String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        OkHttp3Utils.getInstance(this).doPostJson(UserUrl.sendCode, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                downDate();
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(), msg);
            }
        });
    }

    /**
     * 倒计时开始
     */
    private void downDate() {
        if (count != null) {
            count = null;
        }
        count = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                getphoneCode.setText(millisUntilFinished / 1000 + "秒");
                getphoneCode.setClickable(false);
            }

            @Override
            public void onFinish() {
                getphoneCode.setClickable(true);
                getphoneCode.setText("重新获取");
            }
        }.start();
    }


    /**
     * 验证手机验证码
     *
     * @param phone
     */
    private void next(String phone, String code, String agreedAgreement) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);

        OkHttp3Utils.getInstance(this).doPostJson(UserUrl.toValidateDeptCode, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                Log.d("luhuas", "onUi: " + result);
                setLoaddingView(false);
                EventBus.getDefault().post(new ReadEvent("1001", 1111,phone));
                AppManager.getAppManager().finishActivity();
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(), msg);
                setLoaddingView(false);
            }
        });
    }

}
