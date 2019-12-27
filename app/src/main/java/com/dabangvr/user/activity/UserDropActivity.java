package com.dabangvr.user.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.comment.PayDialog;
import com.dabangvr.comment.activity.RechargeDetailedActivity;
import com.dabangvr.comment.activity.StrategyActivity;
import com.dabangvr.wxapi.WXPlayCallBack;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 跳币中心
 */
public class UserDropActivity extends BaseActivity {

    @BindView(R.id.tvDropNum)
    TextView tvDropNum;

    @BindView(R.id.cb01)
    CheckBox cb01;
    @BindView(R.id.cb02)
    CheckBox cb02;
    @BindView(R.id.cb03)
    CheckBox cb03;
    @BindView(R.id.cb04)
    CheckBox cb04;
    @BindView(R.id.cb05)
    CheckBox cb05;
    @BindView(R.id.cb06)
    CheckBox cb06;

    private CheckBox[] cbList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    private UserMess userMess;

    @Override
    public int setLayout() {
        return R.layout.activity_my_drop;
    }

    @Override
    public void initView() {
        cbList = new CheckBox[]{cb01, cb02, cb03, cb04, cb05, cb06};
        userMess = SPUtils.instance(getContext()).getUser();
        tvDropNum.setText("可用跳币：" + userMess.getDiamond());

        findViewById(R.id.ll01).setOnClickListener(view -> {
            price = 5;
            num = 50;
            select(0);
        });
        findViewById(R.id.ll02).setOnClickListener(view -> {
            price = 10;
            num = 100;
            select(1);
        });
        findViewById(R.id.ll03).setOnClickListener(view -> {
            price = 20;
            num = 200;
            select(2);
        });
        findViewById(R.id.ll04).setOnClickListener(view -> {
            price = 50;
            num = 500;
            select(3);
        });
        findViewById(R.id.ll05).setOnClickListener(view -> {
            price = 100;
            num = 1000;
            select(4);
        });
        findViewById(R.id.ll06).setOnClickListener(view -> {
            price = 200;
            num = 2000;
            select(5);
        });
    }

    private void select(int position) {
        for (int i = 0; i < cbList.length; i++) {
            if (position == i) {
                cbList[i].setChecked(true);
            } else {
                cbList[i].setChecked(false);
            }
        }
    }

    protected int price;
    private int num;

    @Override
    public void initData() {

    }

    @OnClick({R.id.tvDetailed, R.id.ivBack, R.id.tvConfirm, R.id.tvSeeServer})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tvDetailed:
                goTActivity(RechargeDetailedActivity.class, null);
                break;
            case R.id.tvConfirm:
                paymentDialog();
                break;
            case R.id.tvSeeServer:
                goTActivity(StrategyActivity.class, null);
                break;
            default:
                break;
        }
    }

    /**
     * 支付弹窗
     */
    private void paymentDialog() {
        PayDialog payDialog = new PayDialog(getContext());
        payDialog.setWxPlayCallBack(new WXPlayCallBack() {
            @Override
            public void success() {
                DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, view -> {
                    TextView tvShow = view.findViewById(R.id.tvShow);
                    tvShow.setVisibility(View.VISIBLE);

                    TextView tvTitle = view.findViewById(R.id.tv_title);
                    tvTitle.setText("充值成功");

                    view.findViewById(R.id.tvCancel).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.tvConfirm).setOnClickListener((view1) -> {
                        DialogUtil.getInstance(getContext()).des();
                    });
                });

                userMess.setDiamond(userMess.getDiamond() + num);
                tvDropNum.setText(String.valueOf(userMess.getDiamond()));
                Log.e("testttt", "" + userMess.getDiamond());
                SPUtils.instance(getContext()).putUser(userMess);
            }

            @Override
            public void error(String errorMessage) {
                DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, view -> {
                    TextView tvTitle = view.findViewById(R.id.tv_title);
                    tvTitle.setText("充值系统正在维护中，不便之处请谅解");
                    view.findViewById(R.id.tvCancel).setVisibility(View.GONE);
                    view.findViewById(R.id.tvConfirm).setOnClickListener((view2) -> {
                        DialogUtil.getInstance(getContext()).des();
                    });
                });
            }

            @Override
            public void cancel() {

            }
        });
        payDialog.showDialog(price);
    }
}
