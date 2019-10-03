package com.dabangvr.fragment.home;

import android.util.Log;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.fragment.MessageFragment;
import com.dbvr.baselibrary.view.BaseFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ArrayList<Fragment> mFragments;

    private HomeFragmentRecommend homeFragmentRecommend;

    private ChangeCallBack changeCallBack;
    public void setChangeCallBack(ChangeCallBack changeCallBack) {
        this.changeCallBack = changeCallBack;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView() {
        List<String> mTitles = new ArrayList<>();
        mTitles.add("关注");
        mTitles.add("发现");
        mTitles.add("跳跳");
        mTitles.add("推荐");
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragmentFollow());
        mFragments.add(new HomeFragmentFind());
        mFragments.add(new HomeFragmentFind());
        homeFragmentRecommend = new HomeFragmentRecommend();
        mFragments.add(homeFragmentRecommend);
        ContentPagerAdapter contentAdapter = new ContentPagerAdapter(getFragmentManager(),mTitles);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3){
                    //设置底部透明
                    changeCallBack.change(true);
                }else {
                    changeCallBack.change(false);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {

    }

    public interface ChangeCallBack{
        void change(boolean isCheck);
    }

    class ContentPagerAdapter extends FragmentPagerAdapter {

        private List<String>mTitles;

        public ContentPagerAdapter(FragmentManager fm,List<String>mTitles) {
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
