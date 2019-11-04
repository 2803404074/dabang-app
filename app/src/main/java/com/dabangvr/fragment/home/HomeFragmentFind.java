package com.dabangvr.fragment.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.play.activity.PlayActivity;
import com.dabangvr.adapter.AddHeaderAdapter;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.HomeAdapter;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.eventBus.ReadEvent;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.model.PlayMode;
import com.dbvr.baselibrary.utils.BannerUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
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
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 发现
 */
public class HomeFragmentFind extends BaseFragment {

    @BindView(R.id.recycler_find)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private HomeAdapter adapter;
    private List<HomeFindMo>mData = new ArrayList<>();
    private int page = 1;

    @Override
    public int layoutId() {
        EventBus.getDefault().register(this); //第1步: 注册
        return R.layout.fragment_home_find;
    }
    @Override
    public void initView() {
        Log.e("chaungjian","HomeFragmentFind 执行initView()");
        setLoaddingView(true);
        refreshLayout.setPrimaryColorsId(R.color.colorTM, android.R.color.white);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            Message message = new Message();
            message.what = 1;
            message.obj = "刷新";
            mHandler.sendMessageDelayed(message, 2000);
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            Message message = new Message();
            message.what = 2;
            message.obj = "加载更多";
            mHandler.sendMessageDelayed(message, 1000);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HomeAdapter(getContext()) {
            @Override
            public void convert(Context mContext, final BaseRecyclerHolder holder, int mType) {
                //顶部头像
                if (adapter.getViewTypeForMyTask(mType) == adapter.mTypeZero) {
                    List<PlayMode> list = mData.get(mType).getOneMos();
                    RecyclerView recyclerViewx = holder.getView(R.id.recycler_head);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    manager.setOrientation(RecyclerView.HORIZONTAL);
                    recyclerViewx.setLayoutManager(manager);
                    AddHeaderAdapter addHeaderAdapter = new AddHeaderAdapter(list, getContext());
                    View view = View.inflate(getContext(), R.layout.fragment_find_head, null);
                    ImageView headimage = view.findViewById(R.id.v_user_hean);
                    Glide.with(getActivity()).load(R.mipmap.gif_zb).into(headimage);
                    addHeaderAdapter.addHeaderView(view);
                    recyclerViewx.setAdapter(addHeaderAdapter);

                //1个大封面和4个封面
                } else if (adapter.getViewTypeForMyTask(mType) == adapter.mTypeOne) {
                    List<HomeFindMo.TowMo> list = mData.get(mType).getTowMos();
                    String liveId;
                    String roomId;
                    String nickName;
                    String liveTag;
                    String lookNum;
                    String headUrl;
                    boolean isFollow;
                    int userId;
                    for (int i = 0; i < list.size(); i++) {
                        if (!StringUtils.isEmpty(list.get(i).getCoverUrl())){
                            holder.setImageByUrl(R.id.ivTitle,list.get(i).getCoverUrl());
                            holder.setText(R.id.tvName,list.get(i).getNickName());
                            holder.setText(R.id.tvTitle,list.get(i).getLiveTitle());
                            liveId = list.get(i).getFname();
                            roomId = list.get(i).getRoomId();
                            nickName = list.get(i).getNickName();
                            liveTag = list.get(i).getLiveTag();
                            lookNum = list.get(i).getLookNum();
                            headUrl = list.get(i).getHeadUrl();
                            userId = list.get(i).getUserId();
                            isFollow = list.get(i).isFollow();
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map<String,Object>map = new HashMap<>();
                                    map.put("url",liveId);
                                    map.put("roomId",roomId);
                                    map.put("nickName",nickName);
                                    map.put("liveTag",liveTag);
                                    map.put("lookNum",lookNum);
                                    map.put("headUrl",headUrl);
                                    map.put("userId",userId);
                                    map.put("isFollow",isFollow);
                                    goTActivity(PlayActivity.class,map);
                                }
                            });
                            list.remove(i);
                            break;
                        }
                    }

                    RecyclerView recyclerTow =  holder.getView(R.id.recycle_tow);
                    recyclerTow.setLayoutManager(new GridLayoutManager(getContext(),2));
                    RecyclerAdapterPosition adapter = new RecyclerAdapterPosition<HomeFindMo.TowMo>(getContext(),list,R.layout.item_conver_match) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, int position,HomeFindMo.TowMo o) {

                            SimpleDraweeView myImageView = holder.getView(R.id.miv_view);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                            if (position % 2 != 0){
                                params.setMargins(5,5,0,0);//左边的item
                            } else{
                                params.setMargins(0, 5, 5, 0);//右边的item
                            }
                            myImageView.setLayoutParams(params);

                            //直播类型
                            if (!StringUtils.isEmpty(o.getCoverUrl())){
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
                    recyclerTow.setAdapter(adapter);
                    adapter.setOnItemClickListener((view, position) -> {
                        Map<String,Object>map = new HashMap<>();
                        map.put("url",list.get(position).getFname());
                        map.put("roomId",list.get(position).getRoomId());
                        map.put("nickName",list.get(position).getNickName());
                        map.put("liveTag",list.get(position).getLiveTag());
                        map.put("lookNum",list.get(position).getLookNum());
                        map.put("headUrl",list.get(position).getHeadUrl());
                        map.put("userId",list.get(position).getUserId());
                        map.put("isFollow",list.get(position).isFollow());
                        ToastUtil.showShort(getContext(),"是否已经关注="+list.get(position).isFollow());
                        goTActivity(PlayActivity.class,map);
                    });
                //轮播图
                } else if (adapter.getViewTypeForMyTask(mType) == adapter.mTypeTow) {
                    List<HomeFindMo.ThreeMo> list = mData.get(mType).getThreeMos();
                    Banner banner = holder.getView(R.id.bannerContainer);
                    List<String> mImage = new ArrayList<>();
                    List<String> mTitle = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        mImage.add(list.get(i).getChartUrl());
                        mTitle.add(list.get(i).getTitle());
                    }
                    BannerUtil bannerUtil = new BannerUtil(getContext(), banner, mImage, mTitle);
                    bannerUtil.startBanner();

                //分类
                } else if (adapter.getViewTypeForMyTask(mType) == adapter.mTypeThree) {
                    List<HomeFindMo.FourMo> list = mData.get(mType).getFourMos();
                    RecyclerView recyclerView = holder.getView(R.id.recycler_head);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(),5));
                    RecyclerAdapter adapter = new RecyclerAdapter<HomeFindMo.FourMo>(getContext(),list,R.layout.item_type) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, HomeFindMo.FourMo o) {
                            TextView textView = holder.getView(R.id.tvType);
                            textView.setBackgroundResource(R.drawable.shape_yellow);//shape_db_search,shape_orag,shape_gray
                        }
                    };
                    recyclerView.setAdapter(adapter);

                //底部列表
                } else if (adapter.getViewTypeForMyTask(mType) == adapter.mTypeFour) {
                    List<HomeFindMo.TowMo> list = mData.get(mType).getFiveMos();
                    RecyclerView recyclerTow =  holder.getView(R.id.recycler_head);
                    recyclerTow.setLayoutManager(new GridLayoutManager(getContext(),2));
                    RecyclerAdapterPosition adapter = new RecyclerAdapterPosition<HomeFindMo.TowMo>(getContext(),list,R.layout.item_conver_match) {
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
                            if (!StringUtils.isEmpty(o.getCoverUrl())){
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
                    recyclerTow.setAdapter(adapter);
                }
            }
        };
        adapter.setmData(mData);
        recyclerView.setAdapter(adapter);
    }

    private Map<String,Object> map = new HashMap<>();
    @Override
    public void initData() {
        map.put("page",page);
        map.put("userId", SPUtils.instance(getContext()).getUser().getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.indexFind, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                List<HomeFindMo> list = new Gson().fromJson(result, new TypeToken<List<HomeFindMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    mData = list;
                    adapter.update(mData);
                    Log.e("ssss",new Gson().toJson(mData));
                }else {
                    page--;
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
                    obj = (String) msg.obj;
                    initData();
                    refreshLayout.finishRefresh(true);
                    break;
                case 2:         //加载更多
                    // TODO: 2019/10/9 加载更多
                    //adapter.addData(mData1);
                    map.put("limit",10);
                    page++;
                    initData();
                    obj = (String) msg.obj;
                    refreshLayout.finishLoadMore(true);
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
        if ((!TextUtils.isEmpty(info) && TextUtils.equals(info1, "0")) || (!TextUtils.isEmpty(info1) && TextUtils.equals("1", info1))) {
            if (TextUtils.equals(info1, "1")) {
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
