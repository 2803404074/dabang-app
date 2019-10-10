package com.dabangvr.fragment.home;

import android.content.Context;
import android.graphics.Canvas;
import android.net.sip.SipManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.MoreItemAdapter;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.adapter.RecyclerAdapterTest;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.ui.MyImageView;
import com.dbvr.baselibrary.utils.BannerUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.Banner;

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

    private MoreItemAdapter adapter;

    private List<HomeFindMo> mData = new ArrayList<>();

    @Override
    public int layoutId() {
        return R.layout.fragment_home_find;
    }

    private List<String> dataOn = new ArrayList<>();
    private List<String> dataTow = new ArrayList<>();

    @Override
    public void initView() {
        Log.e("eeee", "HomeFragmentFind----initView");
        for (int i = 0; i < 4; i++) {
            dataOn.add("");
        }
        for (int i = 0; i < 30; i++) {
            dataTow.add("");
        }

        mData.add(new HomeFindMo(0, R.layout.recy_no_bg));//0和2和4的类型都是列表，区别布局管理器
        mData.add(new HomeFindMo(1, R.layout.item_home_find_tow));//一个直播封面视图
        mData.add(new HomeFindMo(2, R.layout.item_home_find_one, dataOn));//0和2和4的类型都是列表，区别布局管理器
        mData.add(new HomeFindMo(3, R.layout.item_home_find_four));//轮播视图

        HomeFindMo homeFindMo = new HomeFindMo(4, R.layout.item_home_find_one);//分类
        List<HomeFindMo.TypeMo> mType = new ArrayList<>();
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_yellow));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_db));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_orag));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_db));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_yellow));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_orag));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_db));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_orag));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_yellow));
        mType.add(new HomeFindMo.TypeMo(R.drawable.shape_gray));
        homeFindMo.setmTypeMo(mType);
        mData.add(homeFindMo);//直播类型分类视图(0和2和4的类型都是列表，区别布局管理器)

        //继续增加视图
        mData.add(new HomeFindMo(2, R.layout.item_home_find_one, dataTow));

        mData.add(new HomeFindMo(1, R.layout.item_home_find_tow));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MoreItemAdapter<HomeFindMo>(getContext(), mData) {
            @Override
            public void convert(BaseRecyclerHolder holder, int position, HomeFindMo s) {
                RecyclerView recyclerView = holder.getView(R.id.recycler_head);
                switch (s.getmType()) {
                    case 0:
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        manager.setOrientation(RecyclerView.HORIZONTAL);
                        recyclerView.setLayoutManager(manager);
                        List<String> data = new ArrayList<>();
                        for (int i = 0; i < 10; i++) {
                            data.add("http://b-ssl.duitang.com/uploads/item/201707/04/20170704113215_uAwk5.jpeg");
                        }
                        RecyclerAdapter adapter = new RecyclerAdapter<String>(getContext(), data, R.layout.item_head) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {
                                SimpleDraweeView sdvHead = holder.getView(R.id.sdvHead);
                                sdvHead.setImageURI(o);

                            }
                        };
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Log.d("luhuas", "onItemClick: item0=="+position);
                            }
                        });
                        break;
                    case 1:

                        break;
                    case 2:
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        RecyclerAdapterTest adapter2 = new RecyclerAdapterTest<String>(getContext(), s.getmResources(), R.layout.item_conver_match) {
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
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(recyclerView.canScrollVertically(1)){
                Log.i(TAG, "direction 1: true");
            }else {
                Log.i(TAG, "direction 1: false");//滑动到底部
                List<HomeFindMo> mData1 = new ArrayList<>();
                mData1.add(new HomeFindMo(2, R.layout.item_home_find_one, dataTow));
                // TODO: 2019/10/9 加载更多
                adapter.addData(mData1);
            }
            if(recyclerView.canScrollVertically(-1)){
                Log.i(TAG, "direction -1: true");
            }else {
                Log.i(TAG, "direction -1: false");//滑动到顶部
            }

        }
    });
    }

    @Override
    public void initData() {

    }
}
