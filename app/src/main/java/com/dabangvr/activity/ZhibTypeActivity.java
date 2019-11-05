package com.dabangvr.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.fragment.ZhiBTypeFragment;
import com.dabangvr.fragment.home.HomeFragmentFind;
import com.dabangvr.fragment.home.HomeFragmentFollow;
import com.dabangvr.fragment.home.HomeFragmentRecommend;
import com.dabangvr.fragment.home.HomeFragmentTiaoTiao;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 直播分类页面
 */
public class ZhibTypeActivity extends BaseActivity {
    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter contentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_zhib_type;
    }

    @Override
    public void initView() {
        String str = getIntent().getStringExtra("list");
        int position = getIntent().getIntExtra("position",0);
        List<String> mTitles = new ArrayList<>();
        mFragments = new ArrayList<>();
        List<HomeFindMo.FourMo> list = new Gson().fromJson(str, new TypeToken<List<HomeFindMo.FourMo>>() {}.getType());
        for (int i = 0; i < list.size(); i++) {
            mTitles.add(list.get(i).getName());
            mFragments.add(new ZhiBTypeFragment(list.get(i).getId()));
        }
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager(),mTitles,mFragments);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页
                ((ZhiBTypeFragment) contentAdapter.getItem(position)).sendMessage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void initData() {

    }
}
