package com.dabangvr.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.activity.UserHomeActivity;
import com.dabangvr.fragment.other.UserDynamicFragment;
import com.dbvr.baselibrary.view.BaseFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 纯列表的fragment
 */
public class DynamicFragment extends BaseFragment implements UserDynamicFragment.HeadCallBack {


    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ArrayList<Fragment> mFragments;

    @Override
    public int layoutId() {
        return R.layout.fragment_dynamic;
    }

    @Override
    public void initView() {
        List<String> mTitles = new ArrayList<>();
        mTitles.add("关注");
        mTitles.add("好友");
        mTitles.add("附近");
        mFragments = new ArrayList<>();
        mFragments.add(new UserDynamicFragment(this));
        mFragments.add(new UserDynamicFragment(this));
        mFragments.add(new UserDynamicFragment(this));
        ContentPagerAdapter contentAdapter = new ContentPagerAdapter(getChildFragmentManager(),mTitles);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onclickCallBack() {
        Map<String,Object>map = new HashMap<>();
        map.put("userId","");
        goTActivity(UserHomeActivity.class,map);
    }

    class ContentPagerAdapter extends FragmentPagerAdapter {

        private List<String>mTitles;

        public ContentPagerAdapter(FragmentManager fm, List<String>mTitles) {
            super(fm);
            this.mTitles = mTitles;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

    }
}
