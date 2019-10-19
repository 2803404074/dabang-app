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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.adapter.AddHeaderAdapter;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.MoreItemAdapter;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dabangvr.application.MyApplication;
import com.dbvr.baselibrary.eventBus.ReadEvent;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.model.PlayMode;
import com.dbvr.baselibrary.utils.BannerUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.view.BaseFragment;
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
import java.util.List;

import butterknife.BindView;

/**
 * 发现
 */
public class HomeFragmentFind extends BaseFragment {

    private static final String TAG = "luhuas";
    @BindView(R.id.recycler_find)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private MoreItemAdapter adapter;

    private List<HomeFindMo> mData = new ArrayList<>();

    @Override
    public int layoutId() {
        EventBus.getDefault().register(this); //第1步: 注册
        return R.layout.fragment_home_find;
    }

    private List<String> dataOn = new ArrayList<>();
    private List<String> dataTow = new ArrayList<>();

    @Override
    public void initView() {
        setLoaddingView(true);
        Log.e("eeee", "HomeFragmentFind----initView");
        for (int i = 0; i < 4; i++) {
            dataOn.add("");
        }
        for (int i = 0; i < 30; i++) {
            dataTow.add("");
        }
        //橫型主播類型
        String str = (String) SPUtils.instance(getActivity()).getkey("AnchorList", "");
        List<PlayMode> data = new Gson().fromJson(str, new TypeToken<List<PlayMode>>() {
        }.getType());
        if (data == null) {
            data = new ArrayList<>();
        }


        mData.add(new HomeFindMo(0, R.layout.recy_no_bg));//0和2和4的类型都是列表，区别布局管理器
        mData.add(new HomeFindMo(1, R.layout.item_home_find_tow));//一个直播封面视图
        mData.add(new HomeFindMo(2, R.layout.item_home_find_one, dataOn));//0和2和4的类型都是列表，区别布局管理器
        mData.add(new HomeFindMo(3, R.layout.item_home_find_four));//轮播视图

        HomeFindMo homeFindMo = new HomeFindMo(4, R.layout.item_home_find_one);//分类
        List<HomeFindMo.TypeMo> mType = new ArrayList<>();
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_yellow));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_db_search));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_orag));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_db_search));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_yellow));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_orag));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_db_search));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_orag));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_yellow));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_gray));
        homeFindMo.setmTypeMo(mType);
        mData.add(homeFindMo);//直播类型分类视图(0和2和4的类型都是列表，区别布局管理器)

        //继续增加视图
        mData.add(new HomeFindMo(2, R.layout.item_home_find_one, dataTow));

        mData.add(new HomeFindMo(1, R.layout.item_home_find_tow));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<PlayMode> finalData = data;
        adapter = new MoreItemAdapter<HomeFindMo>(getContext(), mData) {
            @Override
            public void convert(BaseRecyclerHolder holder, int position, HomeFindMo s) {
                RecyclerView recyclerView = holder.getView(R.id.recycler_head);
                switch (s.getmType()) {
                    case 0:
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(manager);
                        AddHeaderAdapter addHeaderAdapter = new AddHeaderAdapter(finalData, getContext());
                        View view = View.inflate(getContext(), R.layout.fragment_find_head, null);
                        ImageView headimage = view.findViewById(R.id.v_user_hean);
                        Glide.with(getActivity()).load(R.mipmap.gif_zb).into(headimage);
                        addHeaderAdapter.addHeaderView(view);
                        recyclerView.setAdapter(addHeaderAdapter);
                        addHeaderAdapter.setItemOnClickListener(new AddHeaderAdapter.ItemOnClickListener() {
                            @Override
                            public void onClickListener(int position) {
                                Log.d("luhuas", "onItemClick: item0==" + position + "==" + finalData.get(position).getNickName());
                            }
                        });
                        break;
                    case 1:

                        break;
                    case 2:
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        RecyclerAdapterPosition adapter2 = new RecyclerAdapterPosition<String>(getContext(), s.getmResources(), R.layout.item_conver_match) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {
                                ImageView myImageView = holder.getView(R.id.miv_view);
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
                    case 3:
                        Banner banner = holder.getView(R.id.bannerContainer);
                        List<String> mImage = new ArrayList<>();
                        List<String> mTitle = new ArrayList<>();
                        mImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570000228151&di=cd57a04699c5ce7e7c1f20baa7e0c339&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fdesign%2F00%2F79%2F50%2F39%2F5795f4305917f.jpg");
                        mImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570000311078&di=83b2a6872dedfcff0f72744ff2f21e82&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F2cd80b90eb96853a720fa2e563b42c255824479321f5d-W3eCu8_fw658");
                        mImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570000311077&di=d229092cd117b2cb2a59c4f7092018ba&imgtype=0&src=http%3A%2F%2Fimg10.360buyimg.com%2Fcms%2Fjfs%2Ft286%2F281%2F218389719%2F107730%2Feecfe085%2F540535d9N753a97d3.jpg");
                        mImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570000311077&di=208abab0c2fe1a87cf68cf13dc6b6ee7&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01fadf58fefe33a801214550bbfd09.jpg%401280w_1l_2o_100sh.png");
                        mTitle.add("红酒促销代号100");
                        mTitle.add("红酒促销代号202");
                        mTitle.add("红酒促销代号301");
                        mTitle.add("红酒促销代号188");
                        BannerUtil bannerUtil = new BannerUtil(getContext(), banner, mImage, mTitle);
                        bannerUtil.startBanner();
                        break;
                    case 4:
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
                        RecyclerAdapter adapter4 = new RecyclerAdapter<HomeFindMo.TypeMo>(getContext(), s.getmTypeMo(), R.layout.item_type) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, HomeFindMo.TypeMo o) {
                                TextView textView = holder.getView(R.id.tvType);
                                textView.setBackgroundResource(o.getColor());
                            }
                        };
                        recyclerView.setAdapter(adapter4);
                        adapter4.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Log.d("luhuas", "onItemClick:item4= " + position);
                            }
                        });
                        break;
                }
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MoreItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("luhuas", "onItemClick: " + position);
            }
        });

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

        setLoaddingView(false);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String obj = " ";
            switch (msg.what) {
                case 1:         //刷新加载
                    obj = (String) msg.obj;
                    refreshLayout.finishRefresh(true);
                    break;
                case 2:         //加载更多
                    List<HomeFindMo> mData1 = new ArrayList<>();
                    mData1.add(new HomeFindMo(2, R.layout.item_home_find_one, dataTow));
                    // TODO: 2019/10/9 加载更多
                    adapter.addData(mData1);
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
