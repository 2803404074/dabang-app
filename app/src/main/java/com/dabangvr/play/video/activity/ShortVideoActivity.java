package com.dabangvr.play.video.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.MainMo;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.BaseActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.dabangvr.play.video.fragment.ShortVideoFragment;

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
        overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
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

        MainMo person = (MainMo) getIntent().getSerializableExtra("list");
        mFragments.add(new ShortVideoFragment(person));
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scale_in_disappear,R.anim.scale_out_disappear);
    }
}
