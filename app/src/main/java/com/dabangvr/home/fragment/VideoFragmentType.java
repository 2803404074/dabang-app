package com.dabangvr.home.fragment;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dbvr.baselibrary.view.BaseFragmentFromType;
import com.tencent.liteav.demo.my.activity.ShortVideoActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VideoFragmentType extends BaseFragmentFromType {
    private TextView textView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerAdapterPosition adapter;
    private List<ThisVideoMo> mData = new ArrayList<>();
    private List<Integer> heights = new ArrayList<>();

    public VideoFragmentType(int cType) {
        super(cType);
    }

    public VideoFragmentType() {
    }

    @Override
    protected int initLayout() {
        return R.layout.recy_demo_load;
    }

    @Override
    protected void initView() {

        recyclerView = (RecyclerView) bindView(R.id.recycler_view);
        textView = (TextView) bindView(R.id.tvRecyclerShow);
        swipeRefreshLayout = (SwipeRefreshLayout) bindView(R.id.swipeRefreshLayout);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager );
        adapter = new RecyclerAdapterPosition<ThisVideoMo>(getContext(),mData,R.layout.item_video_home) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, ThisVideoMo o) {
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.height = heights.get(position);
                holder.itemView.setLayoutParams(layoutParams);
                holder.setImageByUrl(R.id.miv_view,o.getCover());
                holder.setHeadByUrl(R.id.sdvHead, UserHolper.getUserHolper(getContext()).getUserMess().getHeadUrl());
            }
        };
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorDb5,R.color.colorAccentButton,R.color.text8);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            page=1;
            sendMessage();
        });
        adapter.setOnLoadMoreListener(() -> {
            page+=1;
            sendMessage();
        });

        adapter.setOnItemClickListener((view, position) -> {
            Map<String,Object>map = new HashMap<>();
            map.put("url",(Serializable)mData);
            goTActivity(ShortVideoActivity.class,map);
        });
    }

    private int page = 1;
    @Override
    protected void setDate(int cType) {
        isLoading(true);
        String cover01 = "http://news.kaiwind.com/mrmtyl/xwtp/201611/14/W020161114305754543673.jpg";
        String cover02 = "http://img0.imgtn.bdimg.com/it/u=897595640,2423288815&fm=214&gp=0.jpg";
        String cover03 = "http://img.361games.com/file/tu/show/pcooxsol2ah.jpg";
        String cover04 = "http://img3.cache.netease.com/photo/0001/2016-09-02/BVUL9VK400AP0001.jpg";
        String cover05 = "http://hb.cntour2.com/viewnews/2016/08/11/20160811112642_rfk3.jpg";
        String cover06 = "http://s2.lvjs.com.cn/uploads/pc/place2/2015-07-06/5ed8ab88-dd2c-4964-9dc4-56d6e576dfef.jpg";

        String url01 = "http://image.vrzbgw.com/test001.mp4";
        String url02 = "http://image.vrzbgw.com/test002.mp4";
        String url03 = "http://image.vrzbgw.com/test003.mp4";
        String url04 = "http://image.vrzbgw.com/test004.mp4";
        String url05 = "http://image.vrzbgw.com/test005.mp4";
        String url06 = "http://image.vrzbgw.com/test006.mp4";

        for (int i = 0; i < 5; i++) {
            mData.add(new ThisVideoMo(cover01,url01));
            mData.add(new ThisVideoMo(cover02,url02));
            mData.add(new ThisVideoMo(cover03,url03));
            mData.add(new ThisVideoMo(cover04,url04));
            mData.add(new ThisVideoMo(cover05,url05));
            mData.add(new ThisVideoMo(cover06,url06));
        }
        Random random = new Random();
        for (int i = 0; i < mData.size(); i++) {
            int x=random.nextInt(500) + 500;
            heights.add(x);
        }
        adapter.updateDataa(mData);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
            isLoading(false);
        },2000);
    }


    class ThisVideoMo{
        private String url;
        private String cover;

        public ThisVideoMo(String cover, String url) {
            this.cover = cover;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }
    }
}
