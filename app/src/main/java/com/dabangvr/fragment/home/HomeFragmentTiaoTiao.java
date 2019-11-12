package com.dabangvr.fragment.home;

import android.content.Context;
import android.graphics.Rect;
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
import com.dbvr.baselibrary.model.AllTypeMo;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.utils.SPUtils;
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

    private List<AllTypeMo> mData = new ArrayList<>();
    private TiaoTiaoAdapter adapter;

    @Override
    public int layoutId() {
        return R.layout.fragment_home_find;
    }

    @Override
    public void initView() {
        RecyclerView.ItemDecoration gridItemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
                final StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                final int spanCount = layoutManager.getSpanCount();
                int layoutPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
                if (lp.getSpanIndex() != spanCount) {
                    //左边间距
                    if (layoutPosition % 2 == 1) {
                        outRect.left = 2 / 2;
                        outRect.right = 2;
                    } else {
                        outRect.left = 2;
                        outRect.right = 2 / 2;
                    }
                }
                outRect.top = 2;
            }
        };

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //解决item跳动
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(gridItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new TiaoTiaoAdapter<AllTypeMo>(getContext(),mData) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, AllTypeMo o, int position) {
                if (o.getPosition() == 0){//直播类型
                    holder.getView(R.id.tvTag).setVisibility(View.VISIBLE);
                    holder.setText(R.id.tvTitle,o.getOneMos().getLiveTitle());
                    holder.setImageResource(R.id.ivTable,R.mipmap.see);
                    holder.setText(R.id.zb_likeCounts,o.getOneMos().getLookNum());
                    holder.setImageByUrl(R.id.miv_view,o.getOneMos().getCoverUrl());
                }else if (o.getPosition() == 1){//短视频类型
                    holder.setText(R.id.tvTitle,o.getTowMos().getTitle());
                    holder.setImageResource(R.id.ivTable,R.mipmap.heart_zb);
                    holder.setText(R.id.zb_likeCounts,o.getTowMos().getPraseCount());
                    holder.setImageByUrl(R.id.miv_view,o.getTowMos().getCoverPath());
                }else {//商品类型
                    holder.setText(R.id.tvTitle,o.getThreeMos().getName());
                    holder.setImageByUrl(R.id.miv_view,o.getThreeMos().getListUrl());
                }
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
                List<AllTypeMo> list = new Gson().fromJson(result, new TypeToken<List<AllTypeMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    mData = list;
                    if (page>1){
                        adapter.addData(mData);
                    }else {
                        adapter.upData(mData);
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
