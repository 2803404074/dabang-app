package com.dabangvr.fragment.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.MoreItemAdapter;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.eventBus.ReadEvent;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.view.BaseFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        return R.layout.fragment_home_follow;
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
