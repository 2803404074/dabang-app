package com.dabangvr.mall.activity;

import com.dabangvr.R;
import com.dabangvr.databinding.ActivityBuyStatusBinding;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivityBinding;

public class BuyStatusActivity extends BaseActivityBinding<ActivityBuyStatusBinding> {

    @Override
    public int setLayout() {
        return R.layout.activity_buy_status;
    }

    @Override
    public void initView() {
        if (getIntent().getIntExtra("status",-1) == 0){
            //成功
            mBinding.ivStatus.setImageResource(R.mipmap.dui);
            mBinding.tvStatus.setTextColor(getResources().getColor(R.color.textTitle));
            mBinding.tvStatus.setText("支付成功");

            mBinding.tvSeeOrder.setText("查看订单");
            mBinding.tvSeeOrder.setOnClickListener((view -> {
                goTActivity(OrderActivity.class,null);
                AppManager.getAppManager().finishActivity(this);
            }));
        }else {
            //失败
            mBinding.ivStatus.setImageResource(R.mipmap.cuowu);
            mBinding.tvStatus.setTextColor(getResources().getColor(R.color.red));
            mBinding.tvStatus.setText("支付失败");

            mBinding.tvSeeOrder.setText("重新支付");
            mBinding.tvSeeOrder.setOnClickListener((view -> {
                isLoading(true);
            }));

        }

        mBinding.ivBack.setOnClickListener((view)->{
            AppManager.getAppManager().finishActivity(this);
        });
    }

    @Override
    public void initData() {

    }
}
