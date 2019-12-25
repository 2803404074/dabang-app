package com.dabangvr.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.dabangvr.R;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.model.DepVo;
import com.dbvr.baselibrary.model.DepVoRz;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商家入驻申请第一步手机号码验证
 */
public class UserSJRZOneActivity extends BaseActivity {

    @BindView(R.id.tv_phone_authentication)
    TextView tv_phone_authentication;

    @BindView(R.id.tv_card)
    TextView tv_card;

    @BindView(R.id.tv_shop)
    TextView tv_shop;

    @BindView(R.id.tvGetCode)
    TextView tvGetCode;
    @BindView(R.id.cb_select)
    CheckBox cb_select;

    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etCode)
    EditText etCode;

    private boolean isChecked;
    private CountDownTimer count;
    private DepVoRz depVo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_sjrz_one;
    }

    @Override
    public void initView() {
        if (depVo == null) {
            depVo = new DepVoRz();
        }
    }

    @Override
    public void initData() {
        cb_select.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("luhuas", "onCheckedChanged: " + isChecked);
            UserSJRZOneActivity.this.isChecked = isChecked;
        });
    }

    private long lastonclickTime = 0;//全局变量


    @OnClick({R.id.ivBack, R.id.tvGetCode, R.id.tv_look_up, R.id.tv_next})
    public void onclick(View view) {

        long time = SystemClock.uptimeMillis();//防止多次响应
        if (time - lastonclickTime >= ParameterContens.clickTime) {
            lastonclickTime = time;
        } else {
            return;
        }
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tvGetCode: //获取验证码
                if (TextUtils.isEmpty(etPhone.getText().toString())) return;
                getMessage(etPhone.getText().toString());
                break;
            case R.id.tv_look_up:
                goTActivity(UserAgreeActivity.class, null);
                break;
            case R.id.tv_next:
                String phone = etPhone.getText().toString().trim();
                String code = etCode.getText().toString().trim();
                if (!StringUtils.isMobileNO(phone)) {
                    ToastUtil.showShort(this, "请输入正确的手机号码和验证码");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort(this, "请输入验证码");
                    return;
                }
                if (!isChecked) {
                    ToastUtil.showShort(this, "请确认我已了解");
                    return;
                }
                next(phone, code, "1");



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
     * 验证手机验证码
     *
     * @param phone
     */
    private void next(String phone, String code, String agreedAgreement) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);
        map.put("agreedAgreement", agreedAgreement);
        OkHttp3Utils.getInstance(this).doPostJson(UserUrl.toValidateDeptCode, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                Log.d("luhuas", "onUi: " + result);
                Intent intent = new Intent(getContext(), UserSJRZTwoActivity.class);
                depVo.setPhone(phone);
                intent.putExtra(ParameterContens.depVo, depVo);
                startActivity(intent);
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
}
