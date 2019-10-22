package com.dabangvr.fragment.other.Order;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.RelativeLayout;

import com.dabangvr.R;

import com.dabangvr.fragment.other.Order.MyYhjRecordActivity;
import com.dabangvr.fragment.other.Order.order_fragment.Yhjadapter;
import com.dbvr.baselibrary.adapter.BaseLoadMoreHeaderAdapter;
import com.dbvr.baselibrary.model.CouponBean;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ThreadUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;


import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * 个人中心-我的优惠卷
 */
public class MyYhjActivity extends BaseActivity {

    @BindView(R.id.sc_recycler)
    RecyclerView recyclerView;

    private List<CouponBean> DiscontList = new ArrayList<>();
    private BaseLoadMoreHeaderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_yhj;
    }


    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Yhjadapter yhjadapter = new Yhjadapter(this);
        //添加adapter
        adapter = yhjadapter.setAdaper(recyclerView, DiscontList, 0);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

        for (int i = 0; i < 4; i++) {
            CouponBean couponBean = new CouponBean();
            couponBean.setName("满减卷");
            couponBean.setDetails("全场满200元可立即使用");
            couponBean.setLimit_two("使用规则:该优惠卷仅限于贝壳类产品使用");
            couponBean.setTitle("仅限新用户使用");
            couponBean.setStartDate("2019.9.20");
            couponBean.setEndDate("2019.10.20");
            couponBean.setFavorablePrice("25");
            couponBean.setState("0");
            DiscontList.add(couponBean);
        }


    }

    @OnClick({R.id.back, R.id.historical_record})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.historical_record:
                goTActivity(MyYhjRecordActivity.class,null);
                break;
        }
    }
}
