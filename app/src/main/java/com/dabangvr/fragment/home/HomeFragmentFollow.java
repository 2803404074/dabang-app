package com.dabangvr.fragment.home;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.activity.VideoActivity;
import com.dabangvr.activity.VideoActivityTest;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.view.BaseFragment;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeFragmentFollow extends BaseFragment {

    @BindView(R.id.recycler_follow)
    RecyclerView recyclerView;

    private RecyclerAdapter adapter;

    private List<String>mData = new ArrayList<>();

    @Override
    public int layoutId() {
        return R.layout.fragment_home_follow;
    }

    @Override
    public void initView() {
        Log.e("eeee","HomeFragmentFollow----initView");
        for (int i = 0; i < 10; i++) {
            mData.add("");
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new RecyclerAdapter<String>(getContext(),mData,R.layout.item_conver) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {

            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goTActivity(VideoActivityTest.class,null);
            }
        });
    }

    @Override
    public void initData() {

    }
}
