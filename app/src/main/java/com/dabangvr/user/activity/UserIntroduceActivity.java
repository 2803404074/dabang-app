package com.dabangvr.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dabangvr.R;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 自我介绍activity
 */
public class UserIntroduceActivity extends BaseActivity {

    @BindView(R.id.etInput)
    EditText etInput;

    @BindView(R.id.tvCommit)
    TextView tvCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_introduce;
    }

    @OnClick({R.id.ivBack,R.id.tvCommit})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tvCommit:
                commit();
                break;
                default:break;
        }
    }

    private void commit() {
        UserHolper.getUserHolper(getContext()).upUser(5,etInput.getText().toString());
        AppManager.getAppManager().finishActivity();
    }

    @Override
    public void initView() {
        String str = UserHolper.getUserHolper(getContext()).getUserMess().getAutograph();
        etInput.setText(StringUtils.isEmpty(str)?"介绍一下你自己吧~~~":str);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (StringUtils.isEmpty(etInput.getText().toString())){
                    tvCommit.setBackgroundResource(R.drawable.shape_gray_w);
                    tvCommit.setClickable(false);
                }else {
                    tvCommit.setBackgroundResource(R.drawable.shape_style_green_blue);
                    tvCommit.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void initData() {

    }
}
