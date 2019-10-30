package com.dabangvr.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.activity.LocationActivity;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.ui.MyImageView;
import com.dbvr.baselibrary.view.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 同城fragment
 */
public class SameCityFragment extends BaseFragment {

    @BindView(R.id.recycler_tc)
    RecyclerView recyclerView;

    @BindView(R.id.tvLocationName)
    TextView tvLocationName;

    private RecyclerAdapterPosition adapter;

    private List<Integer> mData = new ArrayList<>();


    @Override
    public int layoutId() {
        return R.layout.fragment_same_city;
    }

    @Override
    public void initView() {
        for (int i = 0; i < 15; i++) {
            mData.add(R.mipmap.test5);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new RecyclerAdapterPosition<Integer>(getContext(),mData,R.layout.item_conver_match) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, Integer o) {
                holder.setImageResource(R.id.miv_view,o);
                ImageView myImageView = holder.getView(R.id.miv_view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                if (position % 2 != 0){
                    params.setMargins(1,1,0,0);//左边的item
                } else{
                    params.setMargins(0, 1, 1, 0);//右边的item
                }
                myImageView.setLayoutParams(params);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.llLocation})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.llLocation:
                goTActivityForResult(LocationActivity.class,null,101);//101请求码，用于返回设置定位值
                break;
                default:break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 101 && resultCode == 103) {
            String result = data.getStringExtra("result");
            tvLocationName.setText(result);
        }
    }
}
