package com.dabangvr.user.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.model.FansMo;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class FollowActivity extends BaseActivity {
    @BindView(R.id.recycle_dz)
    RecyclerView recyclerView;
    private RecyclerAdapterPosition adapter;
    private List<FansMo> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_follow;
    }

    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapterPosition<FansMo>(this, mData, R.layout.item_fans) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, FansMo o) {

                SimpleDraweeView sdvHead = holder.getView(R.id.sdvHead);
                sdvHead.setImageURI(o.getHeadUrl());
                holder.setText(R.id.tvName, o.getNickName());
                holder.getView(R.id.tvGz).setBackgroundResource(R.drawable.shape_gray);
                holder.setText(R.id.tvGz, "已关注");
                holder.getView(R.id.tvGz).setOnClickListener((v) -> {
                    DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, (view) -> {
                        TextView tvTitle = view.findViewById(R.id.tv_title);
                        tvTitle.setText("确定要取消关注" + o.getNickName() + "吗");
                        view.findViewById(R.id.tvCancel).setOnClickListener((view2) -> {
                            DialogUtil.getInstance(getContext()).des();
                        });
                        view.findViewById(R.id.tvConfirm).setOnClickListener((view3) -> {
                            mData.remove(position);
                            adapter.updateDataa(mData);
                            followFunction(o.getUserId());
                            DialogUtil.getInstance(getContext()).des();
                        });
                    });
                    followFunction(o.getUserId());
                });
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", mData.get(position).getUserId());
            goTActivity(UserHomeActivity.class, map);
        });
    }

    @OnClick({R.id.ivBack,R.id.llSearch})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.llSearch:
                goTActivity(SearchActivity.class,null);
                break;
        }
    }

    /**
     * 关注粉丝
     */
    private void followFunction(String userId) {
        setLoaddingView(true);
        Map<String, Object> map = new HashMap<>();
        map.put("fansUserId", userId);
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.updateFans, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                setLoaddingView(false);
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(), msg);
                setLoaddingView(false);
            }
        });
    }

    private int page = 1;

    @Override
    public void initData() {
        String userId = getIntent().getStringExtra("userId");
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("limit", 10);
        if (!StringUtils.isEmpty(userId)){
            map.put("userId", userId);
        }
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getFocusedsList, map,
                new ObjectCallback<String>(getContext()) {
                    @Override
                    public void onUi(String result) {
                        List<FansMo> list = new Gson().fromJson(result, new TypeToken<List<FansMo>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            mData = list;
                            if (page == 1) {
                                adapter.updateDataa(mData);
                            } else {
                                adapter.addAll(mData);
                            }
                        }
                    }

                    @Override
                    public void onFailed(String msg) {
                        Log.e("result", "返回：" + msg);
                    }
                });
    }
}
