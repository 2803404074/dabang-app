package com.dabangvr.comment.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapterPosition;
import com.dabangvr.im.MessageActivity;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RechargeDetailedActivity extends BaseActivity {

    @BindView(R.id.recycle_recharge)
    RecyclerView recyclerView;
    private RecyclerAdapterPosition adapter;
    private List<String>mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_recharge_detailed;
    }

    @Override
    public void initView() {

        for (int i = 0; i < 10; i++) {
            mData.add("");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapterPosition<String>(getContext(),mData,R.layout.item_recharge) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {

            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack,R.id.ivServer})
    public void click(View view){
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.ivServer:
                goTActivity(MessageActivity.class,null);
                break;
        }
    }
}
