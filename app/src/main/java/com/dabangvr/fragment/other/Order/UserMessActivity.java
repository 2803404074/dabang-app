package com.dabangvr.fragment.other.Order;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户编辑信息
 */
public class UserMessActivity extends BaseActivity {

    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;
    @BindView(R.id.tvNickName)
    TextView tvNickName;
    @BindView(R.id.tvUserId)
    TextView tvId;
    @BindView(R.id.tvSex)
    TextView tvSex;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvIntroduce)
    TextView tvIntroduce;
    @BindView(R.id.tvPhone)
    TextView tvPhone;

    private UserMess userMess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_mess;
    }

    @Override
    public void initView() {
        userMess = SPUtils.instance(this).getUser();
        sdvHead.setImageURI(userMess.getHeadUrl());
        tvNickName.setText(userMess.getNickName());
        tvDate.setText("----");

        tvId.setText(String.valueOf(userMess.getId()));

        //性别
        if (StringUtils.isEmpty(userMess.getSex())) {
            tvSex.setHint("未设置性别");
        } else {
            tvSex.setText(userMess.getSex());
        }

        //常住地
        if (StringUtils.isEmpty(userMess.getPermanentResidence())) {
            tvLocation.setHint("设置你的常驻地址，有利于吸引周边人气哦~");
        } else {
            tvLocation.setText(userMess.getPermanentResidence());
        }

        //手机号
        if (StringUtils.isEmpty(userMess.getMobile())) {
            tvPhone.setHint("未绑定手机号");
        } else {
            tvPhone.setText(userMess.getMobile());
        }

        //个人说明
        tvIntroduce.setHint(StringUtils.isEmpty(userMess.getAutograph()) ? "添加个人说明，如你的座右铭等" : userMess.getAutograph());
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tvLogOut:
                DialogUtil.getInstance(this).show(R.layout.dialog_tip, holder -> {
                    holder.findViewById(R.id.tvCancel).setOnClickListener(view1 -> DialogUtil.getInstance(getContext()).des());
                    holder.findViewById(R.id.tvConfirm).setOnClickListener(view12 -> logOut());
                });
                break;
        }
    }

    private void logOut() {
        SPUtils.instance(this).removeUser();
        AppManager.getAppManager().finishAllActivityTo(UserMessActivity.class);
    }
}
