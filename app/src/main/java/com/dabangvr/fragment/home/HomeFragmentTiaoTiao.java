package com.dabangvr.fragment.home;

import android.content.Context;
import android.util.Log;
import android.view.View;
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
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.utils.ScreenUtils;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class HomeFragmentTiaoTiao extends BaseFragment {

    @BindView(R.id.recycler_find)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int page = 1;

    private List<HomeFindMo.TowMo> mData = new ArrayList<>();
    private RecyclerAdapterPosition adapter;

    @Override
    public int layoutId() {
        return R.layout.fragment_home_find;
    }

    @Override
    public void initView() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //解决item跳动
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(manager);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerAdapterPosition<HomeFindMo.TowMo>(getContext(),mData,R.layout.item_conver_wrap) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, HomeFindMo.TowMo o) {


                holder.getView(R.id.tvTag).setVisibility(View.VISIBLE);
                holder.setText(R.id.tvTitle,o.getLiveTitle());
                holder.setImageResource(R.id.ivTable,R.mipmap.see);
                holder.setText(R.id.zb_likeCounts,o.getLookNum());
                SimpleDraweeView sdv = holder.getView(R.id.miv_view);
                sdv.setImageURI(o.getCoverUrl());

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
        Map<String,Object>map = new HashMap<>();
        map.put("page",page);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.indexTT, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                List<HomeFindMo.TowMo> list = new Gson().fromJson(result, new TypeToken<List<HomeFindMo.TowMo>>() {}.getType());
                if (list!=null&&list.size()>0){
                    mData = list;
                    if (page>1){
                        adapter.addAllFor(mData);
                    }else {
                        adapter.updateDataa(mData);
                    }
                }else {
                    if (page>1){
                        page--;
                    }
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }
}
