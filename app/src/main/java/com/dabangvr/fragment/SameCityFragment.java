package com.dabangvr.fragment;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.adapter.RecyclerAdapterTest;
import com.dbvr.baselibrary.ui.MyImageView;
import com.dbvr.baselibrary.view.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 同城fragment
 */
public class SameCityFragment extends BaseFragment {

    @BindView(R.id.recycler_tc)
    RecyclerView recyclerView;

    @BindView(R.id.mivIcon)
    MyImageView myImageView;

    private RecyclerAdapterTest adapter;

    private List<Integer> mData = new ArrayList<>();


    @Override
    public int layoutId() {
        return R.layout.fragment_same_city;
    }

    @Override
    public void initView() {
        myImageView.setImageResource(R.mipmap.test15);
        for (int i = 0; i < 15; i++) {
            mData.add(R.mipmap.test5);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new RecyclerAdapterTest<Integer>(getContext(),mData,R.layout.item_conver_match) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, Integer o) {
                holder.setImageResource(R.id.miv_view,o);
                ImageView myImageView = holder.getView(R.id.miv_view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                if (position % 2 != 0){
                    params.setMargins(2,2,0,0);//左边的item
                } else{
                    params.setMargins(0, 2, 2, 0);//右边的item
                }
                myImageView.setLayoutParams(params);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }
}
