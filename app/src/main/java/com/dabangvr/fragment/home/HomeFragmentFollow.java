package com.dabangvr.fragment.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.eventBus.ReadEvent;
import com.dbvr.baselibrary.model.HomeFollowMo;
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

public class HomeFragmentFollow extends BaseFragment {

    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @BindView(R.id.recycler_follow)
    RecyclerView recyclerViewFollow;

    @BindView(R.id.recycler_tuijian)
    RecyclerView recyclerViewTui;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private List<HomeFollowMo> mDataFollow = new ArrayList<>();
    private RecyclerAdapterPosition adapterFollow;

    private List<HomeFollowMo> mDataTui = new ArrayList<>();
    private RecyclerAdapterPosition adapterTui;

    @Override
    public int layoutId() {
        EventBus.getDefault().register(this); //第1步: 注册
        return R.layout.fragment_home_follow;
    }

    @Override
    public void initView() {

        for (int i = 0; i < 5; i++) {
            mDataFollow.add(new HomeFollowMo());
            mDataTui.add(new HomeFollowMo());
        }


        refreshLayout.setPrimaryColorsId(R.color.colorTM, android.R.color.white);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                Message message = new Message();
                message.what = 1;
                message.obj = "刷新";
                mHandler.sendMessageDelayed(message, 2000);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                Message message = new Message();
                message.what = 2;
                message.obj = "加载更多";
                mHandler.sendMessageDelayed(message, 2000);
            }
        });

        recyclerViewFollow.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapterFollow = new RecyclerAdapterPosition<HomeFollowMo>(getContext(),mDataFollow,R.layout.item_conver_match) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, HomeFollowMo o) {
                SimpleDraweeView myImageView = holder.getView(R.id.miv_view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                if (position % 2 != 0){
                    params.setMargins(5,5,0,0);//左边的item
                } else{
                    params.setMargins(0, 5, 5, 0);//右边的item
                }
                myImageView.setLayoutParams(params);
            }
        };
        recyclerViewFollow.setAdapter(adapterFollow);

        recyclerViewTui.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapterTui = new RecyclerAdapterPosition<HomeFollowMo>(getContext(),mDataTui,R.layout.item_conver_match) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, HomeFollowMo o) {
                SimpleDraweeView myImageView = holder.getView(R.id.miv_view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                if (position % 2 != 0){
                    params.setMargins(5,5,0,0);//左边的item
                } else{
                    params.setMargins(0, 5, 5, 0);//右边的item
                }
                myImageView.setLayoutParams(params);
            }
        };
        recyclerViewTui.setAdapter(adapterTui);
    }

    @Override
    public void initData() {

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String obj = " ";
            switch (msg.what) {
                case 1:         //刷新加载
                    obj = (String) msg.obj;
                    if (refreshLayout!=null){
                        refreshLayout.finishRefresh(true);
                    }

                    break;
                case 2:         //加载更多
                    obj = (String) msg.obj;
                    if (refreshLayout!=null){
                        refreshLayout.finishLoadMore(true);
                    }
                    break;
            }
            Log.d("luhuas", "onRefresh: " + obj);
            return false;
        }
    });
    private String info = null;
    private String info1 = null;

    //EventBus主线程接收消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(ReadEvent event) {
        String state = event.getState();
        refreshLayout.setEnableHeaderTranslationContent(true);//内容跟着下拉

        //刷新回到顶部
        nestedScrollView.fling(0);
        nestedScrollView.smoothScrollTo(0, 0);

        //如果多个消息，可在实体类中添加type区分消息
        switch (event.getType()) {
            case 1000:
                info = event.getInfo();
                break;

            case 1001:
                info1 = event.getInfo();
                break;
        }
        if ((!TextUtils.isEmpty(info) && TextUtils.equals(info, "0")) || (!TextUtils.isEmpty(info1) && TextUtils.equals("0", info1))) {
            if (TextUtils.equals(info1, "0")) {
                refreshLayout.autoRefresh();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
