package com.dabangvr.fragment.other.Order;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.addressselection.utils.Dev;
import com.dabangvr.R;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.model.DepVo;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 主播入驻申请第一步手机号码验证
 */
public class UserZBSQOneActivity extends BaseActivity {


    @BindView(R.id.tv_phone_authentication)
    TextView tv_phone_authentication;

    @BindView(R.id.tv_card)
    TextView tv_card;

    @BindView(R.id.tvGetCode)
    TextView tvGetCode;
    @BindView(R.id.cb_select)
    CheckBox cb_select;

    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.et_other)
    EditText et_other;

    private boolean isChecked;
    private long lastonclickTime=0;
    private DepVo depVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_zbsq_one;
    }

    @Override
    public void initView() {
        depVo = (DepVo) getIntent().getSerializableExtra(ParameterContens.depVo);
        if (depVo !=null){
            etPhone.setText(depVo.getPhone());
        }else {
           depVo =new DepVo();
        }
    }

    @Override
    public void initData() {
    cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d("luhuas", "onCheckedChanged: "+ isChecked);
            UserZBSQOneActivity.this.isChecked = isChecked;

        }
    });
    }


    @OnClick({R.id.ivBack, R.id.tvGetCode, R.id.tv_look_up, R.id.tv_next})
    public void onclick(View view) {
        long time = SystemClock.uptimeMillis();//防止多次响应
        if (time - lastonclickTime >= ParameterContens.clickTime) {
            lastonclickTime = time;
        }else {
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
                String other = et_other.getText().toString().trim();
                if (!StringUtils.isMobileNO(phone)||TextUtils.isEmpty(code)){
                    ToastUtil.showShort(this,"请输入正确的手机号码和验证码");
                    return;
                }
                if (!isChecked){
                    ToastUtil.showShort(this,"请确认我已了解");
                    return;
                }
                depVo.setPhone(phone);
                depVo.setZipCode(other);
                Intent intent =new Intent(this,UserZBSQTwoActivity.class);
                intent.putExtra(ParameterContens.depVo,depVo);
                startActivity(intent);
                break;
        }


    }

    /**
     * 获取手机验证码
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
}
