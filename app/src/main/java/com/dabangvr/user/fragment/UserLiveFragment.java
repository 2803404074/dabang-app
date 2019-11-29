package com.dabangvr.user.fragment;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.view.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 纯列表的fragment 回放
 */
public class UserLiveFragment extends BaseFragment {

    @BindView(R.id.recycler_head)
    RecyclerView recyclerView;

    private List<String>mData = new ArrayList<>();
    private RecyclerAdapter adapter;

    @Override
    public int layoutId() {
        return R.layout.recy_no_bg;
    }

    @Override
    public void initView() {
        for (int i = 0; i < 10; i++) {
            mData.add("");
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new RecyclerAdapter<String>(getContext(),mData,R.layout.item_user_live) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {

            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }
}
