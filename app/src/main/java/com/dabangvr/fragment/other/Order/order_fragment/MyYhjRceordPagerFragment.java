package com.dabangvr.fragment.other.Order.order_fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dabangvr.R;
import com.dbvr.baselibrary.adapter.BaseLoadMoreHeaderAdapter;
import com.dbvr.baselibrary.model.CouponBean;
import com.dbvr.baselibrary.utils.ThreadUtil;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.OkHttp3Utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.Call;

@SuppressLint("ValidFragment")
public class MyYhjRceordPagerFragment extends Fragment  {

    private Context context;
    private String hoursTime;
    private BaseLoadMoreHeaderAdapter adapter;
    private RecyclerView recyclerView;


    private LocalBroadcastManager broadcastManager;
    private IntentFilter intentFilter;
    private BroadcastReceiver mReceiver;

    private List<CouponBean> DiscontList = new ArrayList<>();
    private boolean IS_LOADED = false;
    private static int mSerial = 0;
    private int mTabPos = 0;//第几个商品类型
    private boolean isFirst = true;
    private int page = 1;

    private long mMorePageNumber = 1;
    private long mNewPageNumber = 1;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                switch (msg.what) {
                    case 100: {
                        int type = msg.getData().getInt("type");
                        String rankingType = msg.getData().getString("rankingType");
                        // TODO: 2019/10/21 刷新
                    }
                }
            }
            return;
        }
    };



    public MyYhjRceordPagerFragment(int serial) {
        mSerial = serial;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = MyYhjRceordPagerFragment.this.getContext();
        View view = inflater.inflate(R.layout.recy_demo_load, container, false);

        initView(view);
        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos == mSerial) {
//            sendMessage(0, "1");
            isFirst = false;
        }

        return view;
    }

    private void initView(View view) {



        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        //View header = LayoutInflater.from(context).inflate(R.layout.ms_tips, null);
        Yhjadapter yhjadapter = new Yhjadapter(getContext());
        //添加adapter
        adapter = yhjadapter.setAdaper(recyclerView, DiscontList,mTabPos);
        recyclerView.setAdapter(adapter);


        for (int i = 0; i < 4; i++) {
            CouponBean couponBean = new CouponBean();
            couponBean.setName("满减卷");
            couponBean.setDetails("全场满200元可立即使用");
            couponBean.setLimit_two("使用规则:该优惠卷仅限于贝壳类产品使用");
            couponBean.setTitle("仅限新用户使用");
            couponBean.setStartDate("2019.9.20");
            couponBean.setEndDate("2019.10.20");
            couponBean.setFavorablePrice("25");
            couponBean.setStartDate("1");
            DiscontList.add(couponBean);
        }
//        adapter.addAll(DiscontList);

    }


    /**
     * 每一页都设置了 时间id
     *
     * @param mTabPos
     * @param hoursTime
     */
    public void setTabPos(int mTabPos, String hoursTime) {
        this.mTabPos = mTabPos;
        this.hoursTime = hoursTime;

    }




    /**
     * @param type        刷新标志
     * @param rankingType 排序 1时间，2销量
     */
    public void sendMessage(int type, String rankingType) {
        Message message = handler.obtainMessage();
        message.what = 100;
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);  //往Bundle中存放数据
        bundle.putString("rankingType", rankingType);
        message.setData(bundle);//mes利用Bundle传递数据
        handler.sendMessage(message);//用activity中的handler发送消息
    }

}
