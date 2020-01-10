package com.dabangvr.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapterPosition;
import com.dabangvr.play.activity.verticle.PlayActivity;
import com.dbvr.baselibrary.model.MainMo;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.view.BaseFragmentFromType;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.dabangvr.play.video.activity.VideoActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends BaseFragmentFromType {

    private RecyclerView recyclerView;
    private RecyclerAdapterPosition adapter;
    private List<MainMo>mData = new ArrayList<>();
    private TextView tvShow;
    private SwipeRefreshLayout refreshLayout;

    public HomeFragment() {}

    public HomeFragment(int cType) {
        super(cType);
    }

    @Override
    protected int initLayout() {
        return R.layout.recy_demo_load;
    }

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) bindView(R.id.recycler_view);
        tvShow = (TextView) bindView(R.id.tvRecyclerShow);
        refreshLayout = (SwipeRefreshLayout) bindView(R.id.swipeRefreshLayout);

        recyclerView.setBackgroundResource(R.color.color_f1);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new RecyclerAdapterPosition<MainMo>(getContext(),mData,R.layout.item_main) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, MainMo o) {
                //公共信息
                holder.setImageByUrl(R.id.miv_view,o.getCoverUrl());//封面
                holder.setHeadByUrl(R.id.sdvHead,o.getHeadUrl());//头像
                holder.setText(R.id.tvUserName,o.getNickName());//昵称
                holder.setText(R.id.tvLiveTitle,o.getTitle());//标题
                holder.setText(R.id.tvGoodsTitle,o.getGoodsTitle());//商品标题
                holder.setText(R.id.tvPrice,o.getGoodsPrice());//商品价钱
                holder.setImageByUrl(R.id.mivGoods,o.getGoodsCover());//商品封面
                holder.setText(R.id.tvLiveTitle,o.getTitle());//标题
                //直播类型
                if (o.getLive()){
                    holder.getView(R.id.tvTag).setVisibility(View.VISIBLE);//显示"正在直播"字样
                    holder.setImageResource(R.id.ivTable,R.mipmap.see);//改为观看的图片
                    holder.setText(R.id.tvLookNum,o.getLookNum());//观看数量
                }else {
                    holder.getView(R.id.tvTag).setVisibility(View.GONE);//隐藏"正在直播"字样
                    holder.setImageResource(R.id.ivTable,R.mipmap.mess_dz);//把观看的图片改成点赞图片
                    holder.setText(R.id.tvLookNum,o.getPraseCount());
                }
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            Map<String,Object>map = new HashMap<>();
            MainMo mainMo = (MainMo) adapter.getData().get(position);
            map.put("mainMo",mainMo);
            if (mainMo.getLive()){
                goTActivity(PlayActivity.class, map);
            }else {
                Intent intent = new Intent(getContext(), VideoActivity.class);
                intent.putExtra("mainMo",mainMo);
                startActivity(intent);
            }
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
        isLoading(true);
        Map<String, Object> map = new HashMap<>();
        map.put("liveTag", cType);
        map.put("page", page);
        map.put("userId", SPUtils.instance(getContext()).getUser().getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.indexTT, map, new ObjectCallback<String>(getContext()) {
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
                if (refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
                isLoading(false);
            }
            @Override
            public void onFailed(String msg) {
                if (mData!=null && mData.size()>0){
                    tvShow.setVisibility(View.GONE);
                }else {
                    tvShow.setVisibility(View.VISIBLE);
                }
                if (refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
                isLoading(false);
            }
        });
    }
}
