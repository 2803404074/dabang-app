package com.dabangvr.fragment.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
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

public class HomeFragmentFollow extends BaseFragment {

    @BindView(R.id.recycler_find)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private List<HomeFindMo> mData = new ArrayList<>();

    private List<String> mData0 = new ArrayList<>();
    private List<String> mData1 = new ArrayList<>();
    private MoreItemAdapter<HomeFindMo> adapter;

    @Override
    public int layoutId() {
        EventBus.getDefault().register(this); //第1步: 注册
        return R.layout.fragment_home_follow;
    }

    @Override
    public void initView() {


        for (int i = 0; i < 6; i++) {
            mData0.add(" ");
        }
        for (int i = 0; i < 10; i++) {
            mData1.add(" ");
        }
        mData.add(new HomeFindMo(0, R.layout.recy_no_bg));
        if (mData0.size() < 10) {

            mData.add(new HomeFindMo(1, R.layout.fragment_follw_recommend));
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MoreItemAdapter<HomeFindMo>(getContext(), mData) {

            @Override
            public void convert(BaseRecyclerHolder holder, int position, HomeFindMo homeFindMo) {
                RecyclerView recyclerView = holder.getView(R.id.recycler_head);
                switch (homeFindMo.getmType()) {
                    case 0:
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        RecyclerAdapterPosition adapter = new RecyclerAdapterPosition<String>(getContext(), mData0, R.layout.item_conver_match) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {
                                SimpleDraweeView myImageView = holder.getView(R.id.miv_view);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                                if (position % 2 != 0) {
                                    params.setMargins(5, 5, 0, 0);//左边的item
                                } else {
                                    params.setMargins(0, 5, 5, 0);//右边的item
                                }
                                myImageView.setLayoutParams(params);
                            }
                        };
                        recyclerView.setAdapter(adapter);
                        break;
                    case 1:  //为你推荐视图
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        RecyclerAdapterPosition adapter2 = new RecyclerAdapterPosition<String>(getContext(), mData1, R.layout.item_conver_match) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {
                                SimpleDraweeView myImageView = holder.getView(R.id.miv_view);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                                if (position % 2 != 0) {
                                    params.setMargins(5, 5, 0, 0);//左边的item
                                } else {
                                    params.setMargins(0, 5, 5, 0);//右边的item
                                }
                                myImageView.setLayoutParams(params);
                            }
                        };
                        recyclerView.setAdapter(adapter2);
                        break;
                }
            }

        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {
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
        recyclerView.scrollToPosition(0); //刷新回到顶部
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
