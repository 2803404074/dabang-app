package com.dabangvr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

public class PhoneSetActivity extends BaseActivity {

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
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.getAppManager().finishActivity(PhoneSetActivity.class);
            }
        });
        findViewById(R.id.tvCancelBind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, holder -> {
                    TextView tvTitle = holder.findViewById(R.id.tv_title);
                    tvTitle.setText("是否继续解除绑定?");
                    holder.findViewById(R.id.tvCancel).setOnClickListener(view1 -> DialogUtil.getInstance(getContext()).des());
                    holder.findViewById(R.id.tvConfirm).setOnClickListener(view12 -> goTActivity(PhoneBindActivity.class,null));DialogUtil.getInstance(getContext()).des();

                });
            }
        });
    }

    @Override
    public void initData() {

    }
}
