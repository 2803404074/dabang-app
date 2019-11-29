package com.dabangvr.user.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class PhoneSetActivity extends BaseActivity {
    @BindView(R.id.tvCancelBind)
    TextView tvCancelBind;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_phone_set;
    }

    @Override
    public void initView() {
        mobile = getIntent().getStringExtra("mobile");
        if (!TextUtils.isEmpty(mobile)) {
            tv_phone.setText(StringUtils.hidTel(mobile));
            tvCancelBind.setText("解除绑定");
        } else {
            tvCancelBind.setText("绑定手机号");
        }
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack, R.id.tvCancelBind})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity();
                break;
            case R.id.tvCancelBind:
                if (!TextUtils.isEmpty(mobile)) {
                    DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, holder -> {
                        TextView tvTitle = holder.findViewById(R.id.tv_title);
                        tvTitle.setText("是否继续解除绑定?");
                        holder.findViewById(R.id.tvCancel).setOnClickListener(v -> DialogUtil.getInstance(getContext()).des());
                        holder.findViewById(R.id.tvConfirm).setOnClickListener(v -> {
                            goTActivity(PhoneBindActivity.class, null);
                            DialogUtil.getInstance(getContext()).des();
                            AppManager.getAppManager().finishActivity();
                        });
                    });
                } else {
                    goTActivity(PhoneBindActivity.class, null);
                    AppManager.getAppManager().finishActivity();
                }

                break;
        }
    }
}
