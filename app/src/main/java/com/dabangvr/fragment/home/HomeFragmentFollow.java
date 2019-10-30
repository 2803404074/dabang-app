package com.dabangvr.fragment.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dabangvr.play.activity.PlayActivity;
import com.dbvr.baselibrary.eventBus.ReadEvent;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.model.HomeFollowMo;
import com.dbvr.baselibrary.utils.StringUtils;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class HomeFragmentFollow extends BaseFragment {

    @BindView(R.id.tvTui)
    TextView tvTui;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @BindView(R.id.recycler_follow)
    RecyclerView recyclerViewFollow;

    @BindView(R.id.recycler_tuijian)
    RecyclerView recyclerViewTui;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private List<HomeFindMo.TowMo> mDataFollow = new ArrayList<>();
    private RecyclerAdapterPosition adapterFollow;

    private List<HomeFindMo.TowMo> mDataTui = new ArrayList<>();
    private RecyclerAdapterPosition adapterTui;

    @Override
    public int layoutId() {
        EventBus.getDefault().register(this); //第1步: 注册
        return R.layout.fragment_home_follow;
    }

    @Override
    public void initView() {
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
        recyclerViewFollow.setNestedScrollingEnabled(false);
        adapterFollow = new RecyclerAdapterPosition<HomeFindMo.TowMo>(getContext(),mDataFollow,R.layout.item_conver_match) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, HomeFindMo.TowMo o) {
                SimpleDraweeView myImageView = holder.getView(R.id.miv_view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                if (position % 2 != 0){
                    params.setMargins(5,5,0,0);//左边的item
                } else{
                    params.setMargins(0, 5, 5, 0);//右边的item
                }
                myImageView.setLayoutParams(params);


                //直播类型
                if (!StringUtils.isEmpty(o.getCoverPath())){
                    holder.getView(R.id.tvTag).setVisibility(View.VISIBLE);
                    holder.setText(R.id.tvTitle,o.getLiveTitle());
                    holder.setImageResource(R.id.ivTable,R.mipmap.see);
                    holder.setText(R.id.zb_likeCounts,o.getLookNum());
                    SimpleDraweeView sdv = holder.getView(R.id.miv_view);
                    sdv.setImageURI(o.getCoverUrl());


                    //短视频类型
                }else {
                    holder.getView(R.id.tvTag).setVisibility(View.GONE);
                    holder.setText(R.id.tvTitle,o.getTitle());
                    holder.setImageResource(R.id.ivTable,R.mipmap.heart_zb);
                    holder.setText(R.id.zb_likeCounts,o.getPraseCount());
                    SimpleDraweeView sdv = holder.getView(R.id.miv_view);
                    sdv.setImageURI(o.getCoverPath());
                }
            }
        };
        recyclerViewFollow.setAdapter(adapterFollow);

        recyclerViewTui.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewTui.setNestedScrollingEnabled(false);
        adapterTui = new RecyclerAdapterPosition<HomeFindMo.TowMo>(getContext(),mDataTui,R.layout.item_conver_match) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, HomeFindMo.TowMo o) {
                SimpleDraweeView myImageView = holder.getView(R.id.miv_view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                if (position % 2 != 0){
                    params.setMargins(5,5,0,0);//左边的item
                } else{
                    params.setMargins(0, 5, 5, 0);//右边的item
                }
                myImageView.setLayoutParams(params);

                //直播类型
                if (!StringUtils.isEmpty(o.getCoverPath())){

                    holder.getView(R.id.tvTag).setVisibility(View.GONE);
                    holder.setText(R.id.tvTitle,o.getTitle());
                    holder.setImageResource(R.id.ivTable,R.mipmap.heart_zb);
                    holder.setText(R.id.zb_likeCounts,o.getPraseCount());
                    SimpleDraweeView sdv = holder.getView(R.id.miv_view);
                    sdv.setImageURI(o.getCoverPath());

                    //短视频类型
                }else {
                    holder.getView(R.id.tvTag).setVisibility(View.VISIBLE);
                    holder.setText(R.id.tvTitle,o.getLiveTitle());
                    holder.setImageResource(R.id.ivTable,R.mipmap.see);
                    holder.setText(R.id.zb_likeCounts,o.getLookNum());
                    SimpleDraweeView sdv = holder.getView(R.id.miv_view);
                    sdv.setImageURI(o.getCoverUrl());
                }
            }
        };
        recyclerViewTui.setAdapter(adapterTui);


        adapterTui.setOnItemClickListener((view, position) -> {
            Map<String,Object>map = new HashMap<>();
            //跳转到直播页面
            if (!StringUtils.isEmpty(mDataTui.get(position).getCoverUrl())){
                map.put("url",mDataTui.get(position).getFname());
                map.put("roomId",mDataTui.get(position).getRoomId());
                map.put("nickName",mDataTui.get(position).getNickName());
                map.put("liveTag",mDataTui.get(position).getLiveTag());
                map.put("lookNum",mDataTui.get(position).getLookNum());
                map.put("headUrl",mDataTui.get(position).getHeadUrl());
                map.put("userId",mDataTui.get(position).getUserId());
                map.put("isFollow",mDataTui.get(position).isFollow());
                goTActivity(PlayActivity.class,map);
            }else {//跳转到短视频页面

            }

        });


        adapterFollow.setOnItemClickListener((view, position) -> {
            Map<String,Object>map = new HashMap<>();
            map.put("url",mDataFollow.get(position).getFname());
            map.put("roomId",mDataFollow.get(position).getRoomId());
            map.put("nickName",mDataFollow.get(position).getNickName());
            map.put("liveTag",mDataFollow.get(position).getLiveTag());
            map.put("lookNum",mDataFollow.get(position).getLookNum());
            map.put("headUrl",mDataFollow.get(position).getHeadUrl());
            map.put("userId",mDataFollow.get(position).getUserId());
            map.put("isFollow",mDataFollow.get(position).isFollow());
            goTActivity(PlayActivity.class,map);
        });
    }

    private int page = 1;
    @Override
    public void initData() {
        setLoaddingView(true);
        Map<String,Object>map = new HashMap<>();
        map.put("page",page);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.indexFollowList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                List<HomeFindMo> list = new Gson().fromJson(result, new TypeToken<List<HomeFindMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    for (int i = 0; i < list.size(); i++) {
                        //关注列表
                        if (list.get(i).getPosition()==1){
                            List<HomeFindMo.TowMo> mo = list.get(i).getTowMos();
                            for (int j = 0; j < mo.size(); j++) {
                                mDataFollow.add(mo.get(j));
                            }
                        }
                        //推荐列表
                        if (list.get(i).getPosition()==4){
                            List<HomeFindMo.TowMo> mo = list.get(i).getFiveMos();
                            for (int j = 0; j < mo.size(); j++) {
                                mDataTui.add(mo.get(j));
                            }
                        }
                    }

                    adapterFollow.updateDataa(mDataFollow);
                    adapterTui.updateDataa(mDataTui);
                }else {

                }
                setLoaddingView(false);
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
            }
        });
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String obj = " ";
            switch (msg.what) {
                case 1:         //刷新加载
                    page = 1;
                    obj = (String) msg.obj;
                    if (refreshLayout!=null){
                        refreshLayout.finishRefresh(true);
                    }
                    initData();
                    break;
                case 2:         //加载更多
                    obj = (String) msg.obj;
                    page++;
                    initData();
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
