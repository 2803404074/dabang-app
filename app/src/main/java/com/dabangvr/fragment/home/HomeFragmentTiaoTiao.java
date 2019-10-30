package com.dabangvr.fragment.home;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.utils.ScreenUtils;
import com.dbvr.baselibrary.view.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeFragmentTiaoTiao extends BaseFragment {

    @BindView(R.id.recycler_find)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int page = 1;

    private List<String> mData = new ArrayList<>();
    private RecyclerAdapterPosition adapter;

    @Override
    public int layoutId() {
        return R.layout.fragment_home_find;
    }

    @Override
    public void initView() {
        Log.e("chaungjian","HomeFragmentTiaoTiao 执行initView()");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325429786&di=5d20d7a5e004af1457883d5db5d6588e&imgtype=0&src=http%3A%2F%2Fe0.ifengimg.com%2F03%2F2019%2F0325%2FD59CEAD76863F8B89E5EA23A39EF16E3901F3C8E_size84_w960_h1280.jpeg");
        mData.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1660628661,1155873544&fm=26&gp=0.jpg");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325429788&di=cc00542d967af0758bb6a19898cf79f2&imgtype=0&src=http%3A%2F%2Fimg1.cache.netease.com%2Fcatchpic%2F9%2F94%2F945DD206FF0AA657988181221284FF26.jpg");
        mData.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2415409497,762894304&fm=26&gp=0.jpg");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325487396&di=1117e8ad6d00b9a301986a621a281afa&imgtype=0&src=http%3A%2F%2Fgb.cri.cn%2Fmmsource%2Fimages%2F2015%2F09%2F24%2F71%2F7286095884116725195.jpg");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325487395&di=f3b55a473274c8e4284c3b9d86d3200b&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fpic%2F2%2F53%2Ff4071211731.jpg");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325487394&di=c309065e8db87853d3cbca7d58c35480&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn13%2F793%2Fw719h874%2F20180919%2Fc36d-hkhfqns8295691.jpg");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325166243&di=78b8939de2c2f1c4393c2b42e231a08b&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fforum%2Fw%253D580%2Fsign%3D5969cd703c01213fcf334ed464e636f8%2F7adbdd2a6059252d0053e4ad319b033b5ab5b9f2.jpg");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325166324&di=6d35bf478bf83756fc4ee79eefae967d&imgtype=0&src=http%3A%2F%2Fi4.265g.com%2Fimages%2F201607%2F201607211051334273.jpg");

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //解决item跳动
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(manager);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerAdapterPosition<String>(getContext(),mData,R.layout.item_conver_wrap) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, String o) {

                holder.setImageByUrl(R.id.miv_view,o);
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


        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            page++;
            initData();
            refreshLayout.finishLoadMore(true);//加载完成
        });
        //刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            page=1;
            initData();
            refreshLayout.finishRefresh(true);//刷新完成
        });

    }

    @Override
    public void initData() {
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325429786&di=5d20d7a5e004af1457883d5db5d6588e&imgtype=0&src=http%3A%2F%2Fe0.ifengimg.com%2F03%2F2019%2F0325%2FD59CEAD76863F8B89E5EA23A39EF16E3901F3C8E_size84_w960_h1280.jpeg");
        mData.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1660628661,1155873544&fm=26&gp=0.jpg");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325429788&di=cc00542d967af0758bb6a19898cf79f2&imgtype=0&src=http%3A%2F%2Fimg1.cache.netease.com%2Fcatchpic%2F9%2F94%2F945DD206FF0AA657988181221284FF26.jpg");
        mData.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2415409497,762894304&fm=26&gp=0.jpg");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325487396&di=1117e8ad6d00b9a301986a621a281afa&imgtype=0&src=http%3A%2F%2Fgb.cri.cn%2Fmmsource%2Fimages%2F2015%2F09%2F24%2F71%2F7286095884116725195.jpg");
        mData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572325487395&di=f3b55a473274c8e4284c3b9d86d3200b&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fpic%2F2%2F53%2Ff4071211731.jpg");
        adapter.addAllFor(mData);
    }
}
