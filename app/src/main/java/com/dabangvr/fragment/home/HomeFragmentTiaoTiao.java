package com.dabangvr.fragment.home;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.view.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeFragmentTiaoTiao extends BaseFragment {

    @BindView(R.id.recycler_find)
    RecyclerView recyclerView;

    private List<Integer> mData = new ArrayList<>();
    private RecyclerAdapterPosition adapter;

    @Override
    public int layoutId() {
        return R.layout.fragment_home_find;
    }

    @Override
    public void initView() {
        for (int i = 0; i < 15; i++) {
            mData.add(R.mipmap.test5);
        }
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new RecyclerAdapterPosition<Integer>(getContext(),mData,R.layout.item_conver_match) {
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
