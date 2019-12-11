package com.tencent.liteav.demo.my.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tencent.liteav.demo.my.fragment.ShortVideoFragment;
import com.tencent.liteav.demo.play.R;

import java.util.ArrayList;
import java.util.List;

public class ShortVideoActivity extends BaseActivity {

    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter contentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_short_video;
    }

    @Override
    public void initView() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        mFragments = new ArrayList<>();
        mFragments.add(new ShortVideoFragment());
        mFragments.add(new ShortVideoFragment());
        List<String> mTitles = new ArrayList<>();
        mTitles.add("推荐");
        mTitles.add("附近");
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);
    }

    @Override
    public void initData() {

    }
}
