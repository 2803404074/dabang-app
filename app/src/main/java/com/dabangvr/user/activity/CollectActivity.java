package com.dabangvr.user.activity;

import android.content.Context;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapterPosition;
import com.dabangvr.databinding.ActivityCollectBinding;
import com.dabangvr.mall.activity.GoodsActivity;
import com.dbvr.baselibrary.model.LiveGoods;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivityBinding;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectActivity extends BaseActivityBinding<ActivityCollectBinding> {

    private List<LiveGoods>mData = new ArrayList<>();
    private RecyclerAdapterPosition adapterPosition;

    @Override
    public int setLayout() {
        return R.layout.activity_collect;
    }

    @Override
    public void initView() {
        mBinding.ivBack.setOnClickListener((view)->{
            AppManager.getAppManager().finishActivity(this);
        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterPosition = new RecyclerAdapterPosition<LiveGoods>(getContext(),mData,R.layout.item_goods) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, LiveGoods o) {
                holder.setImageByUrl(R.id.ivContent,o.getListUrl());
                holder.setText(R.id.tvContent,o.getName());
                holder.setText(R.id.tvTitle,o.getTitle());
                holder.setText(R.id.tvPrice,o.getRetailPrice());
            }
        };
        mBinding.recyclerView.setAdapter(adapterPosition);


        mBinding.swipeRefreshLayout.setColorSchemeResources(R.color.colorDb5,R.color.colorAccentButton,R.color.text8);
        mBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            page=1;
            initData();
        });

        adapterPosition.setOnLoadMoreListener(() -> {
            page++;
            initData();
        });

        adapterPosition.setOnItemClickListener((view, position) -> {
            LiveGoods liveGoods = (LiveGoods) adapterPosition.getData().get(position);
            Map<String,Object>map = new HashMap<>();
            map.put("goodsId",liveGoods.getId());
            goTActivity(GoodsActivity.class,map);
        });
    }

    private int page = 1;
    @Override
    public void initData() {
        isLoading(true);
        Map<String,Object>map = new HashMap<>();
        map.put("page",page);
        map.put("limit","20");
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getCollectGoodsList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                mData = new Gson().fromJson(result, new TypeToken<List<LiveGoods>>() {
                }.getType());
                if (mData!=null && mData.size()>0){
                    if (page == 1){
                        adapterPosition.updateDataa(mData);
                    }else {
                        adapterPosition.addAll(mData);
                    }
                }else {
                    if (page>1){
                        page--;
                    }
                }
                if (mBinding.swipeRefreshLayout.isRefreshing()){
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }

                isLoading(false);
            }

            @Override
            public void onFailed(String msg) {
                if (mBinding.swipeRefreshLayout.isRefreshing()){
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
                isLoading(false);
            }
        });
    }
}
