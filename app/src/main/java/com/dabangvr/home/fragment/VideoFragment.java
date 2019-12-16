package com.dabangvr.home.fragment;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.view.BaseFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class VideoFragment extends BaseFragment {

    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter contentAdapter;
    @Override
    public int layoutId() {
        return R.layout.fragment_video;
    }

    @Override
    public void initView() {
        isLoading(true);
        List<String> list = new ArrayList<>();
        list.add("关注");
        list.add("附近");
        list.add("推荐");
        if (list != null && list.size() > 0) {
            mFragments = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                mFragments.add(new VideoFragmentType(i));
            }
            contentAdapter = new ContentPagerAdapter(getChildFragmentManager(), list, mFragments);
            viewPager.setAdapter(contentAdapter);
            tabLayout.setViewPager(viewPager);
            viewPager.setOffscreenPageLimit(1);
        }
        isLoading(false);
    }

    @Override
    public void initData() {

    }
}
