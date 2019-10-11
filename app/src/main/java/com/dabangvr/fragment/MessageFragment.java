package com.dabangvr.fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.view.BaseFragment;

import butterknife.BindView;

/**
 * 消息fragment
 */
public class MessageFragment extends BaseFragment {

    @BindView(R.id.recycler_mess)
    RecyclerView recyclerView;

    private RecyclerAdapter adapter;

    @Override
    public int layoutId() {
        return R.layout.fragment_message;
    }

    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void initData() {

    }
}
