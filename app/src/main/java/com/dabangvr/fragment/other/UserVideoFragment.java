package com.dabangvr.fragment.other;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.view.BaseFragment;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 纯列表的fragment
 */
public class UserVideoFragment extends BaseFragment {



    @BindView(R.id.recycler_head)
    RecyclerView recyclerView;

    private List<String>mData = new ArrayList<>();
    private RecyclerAdapter adapter;


    @Override
    public int layoutId() {
        return R.layout.recy_no_bg;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void initView() {
        for (int i = 0; i < 10; i++) {
            mData.add("");
        }
        //recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapter<String>(getContext(),mData,R.layout.item_user_video) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("luhuas", "ovider=nItemClick: "+position);
            }
        });



    }

    @Override
    public void initData() {

    }
}
