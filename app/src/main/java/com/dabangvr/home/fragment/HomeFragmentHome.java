package com.dabangvr.home.fragment;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapterPosition;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.user.activity.UserHomeActivity;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.FansMo;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragmentHome extends BaseFragment {

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.recyclerFollow)
    RecyclerView recyclerView;
    private List<FansMo> mData = new ArrayList<>();
    private RecyclerAdapterPosition adapterPosition;
    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter contentAdapter;

    @Override
    public int layoutId() {
        return R.layout.app_bar_main_copy;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        adapterPosition = new RecyclerAdapterPosition<FansMo>(getContext(), mData, R.layout.item_head) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, FansMo o) {
                holder.setHeadByUrl(R.id.sdvHead, o.getHeadUrl());
                holder.setText(R.id.tv_nickName, o.getNickName());
                if (o.isLive()) {
                    holder.getView(R.id.tvLive).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.tvLive).setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(adapterPosition);

        adapterPosition.setOnItemClickListener((view, position) -> {
            Map<String,Object>map = new HashMap<>();
            map.put("userId",mData.get(position).getUserId());
            goTActivity(UserHomeActivity.class,map);
        });
    }

    @OnClick({R.id.ivSearchToolbar})
    public void onclick(View view){
        if (view.getId() == R.id.ivSearchToolbar){
            goTActivity(SearchActivity.class,null);
        }
    }
    @Override
    public void initData() {
        //获取关注人的列表
        getFollowList();
        //获取标签列表
        getType();
    }

    private void getFollowList() {
        Map<String, Object> map = new HashMap<>();
        map.put("page", 1);
        map.put("limit", 20);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getFocusedsList, map,
                new ObjectCallback<String>(getContext()) {
                    @Override
                    public void onUi(String result) {
                        List<FansMo> list = new Gson().fromJson(result, new TypeToken<List<FansMo>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            mData = list;
                            adapterPosition.updateDataa(mData);
                            AppBarLayout.LayoutParams para1 = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
                            para1.height = 500;
                            collapsingToolbarLayout.setLayoutParams(para1);
                        }
                    }
                    @Override
                    public void onFailed(String msg) {
                        Log.e("result", "返回：" + msg);
                    }
                });
    }

    private void getType() {
        //获取标签
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getLiveCategoryList, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                List<TagMo> list = new Gson().fromJson(result, new TypeToken<List<TagMo>>() {
                }.getType());
                list.add(0, new TagMo("1", "关注"));
                List<String> mTitles = new ArrayList<>();
                if (list != null && list.size() > 0) {
                    mFragments = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        mFragments.add(new HomeFragment(Integer.parseInt(list.get(i).getId())));
                        mTitles.add(list.get(i).getName());
                    }
                    contentAdapter = new ContentPagerAdapter(getChildFragmentManager(), mTitles, mFragments);
                    viewPager.setAdapter(contentAdapter);
                    tabLayout.setViewPager(viewPager);
                    viewPager.setOffscreenPageLimit(1);
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }
}