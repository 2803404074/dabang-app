package com.dabangvr.home.fragment;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dabangvr.play.activity.verticle.PlayActivityCoPy;
import com.dbvr.baselibrary.model.MainMo;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.view.BaseFragmentFromType;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MainFragment extends BaseFragmentFromType {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<MainMo>mData = new ArrayList<>();

    @BindView(R.id.tvRecyclerShow)
    TextView tvShow;
    @BindView(R.id.llLoading)
    LinearLayout llLoading;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    public MainFragment(int cType) {
        super(cType);
    }

    @Override
    protected int initLayout() {
        return R.layout.recy_demo_load;
    }

    @Override
    protected void initView() {
        recyclerView.setBackgroundResource(R.color.color_f1);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new RecyclerAdapter<MainMo>(getContext(),mData,R.layout.item_main) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, MainMo o) {
                    holder.setImageByUrl(R.id.miv_view,o.getCoverUrl());
                    holder.setText(R.id.tvLookNum,o.getLookNum());
                    SimpleDraweeView sdvHead = holder.getView(R.id.sdvHead);
                    sdvHead.setImageURI(o.getHeadUrl());
                    holder.setText(R.id.tvUserName,o.getNickName());
                    holder.setText(R.id.tvLiveTitle,o.getLiveTitle());
                    holder.setText(R.id.tvGoodsTitle,o.getGoodsTitle());
                    holder.setText(R.id.tvPrice,o.getGoodsPrice());
                    holder.setImageByUrl(R.id.mivGoods,o.getGoodsCover());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            Map<String,Object>map = new HashMap<>();
            map.put("typeId",getcType());
            map.put("position",position);
            goTActivity(PlayActivityCoPy.class, map);
        });

        refreshLayout.setColorSchemeResources(R.color.colorDb5,R.color.colorAccentButton,R.color.text8);
        refreshLayout.setOnRefreshListener(() -> {
            page=1;
            sendMessage();
        });
        adapter.setOnLoadMoreListener(() -> {
            page+=1;
            sendMessage();
        });
    }

    private int page = 1;
    @Override
    protected void setDate(int cType) {
        llLoading.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("liveTag", cType);
        map.put("page", page);
        map.put("userId", SPUtils.instance(getContext()).getUser().getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getOnlineList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                List<MainMo> list = new Gson().fromJson(result,
                        new TypeToken<List<MainMo>>() {}.getType());
                if (list != null && list.size()>0){
                    if (page==1){
                        adapter.updateDataa(list);
                    }else {
                        adapter.addAll(list);
                    }
                    if (tvShow!=null){tvShow.setVisibility(View.GONE);}
                }else {
                    if (page == 1){
                        //显示没有数据
                        if (tvShow!=null){tvShow.setVisibility(View.VISIBLE);}
                    }else {
                        //没有更多了
                        if (tvShow!=null){tvShow.setVisibility(View.GONE);}
                        page--;
                    }
                }
                llLoading.setVisibility(View.GONE);
                if (refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onFailed(String msg) {
                tvShow.setVisibility(View.VISIBLE);
                llLoading.setVisibility(View.GONE);
            }
        });
    }
}
