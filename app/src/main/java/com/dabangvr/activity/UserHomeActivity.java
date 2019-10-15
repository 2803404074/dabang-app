package com.dabangvr.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.fragment.other.UserDynamicFragment;
import com.dabangvr.fragment.other.UserLiveFragment;
import com.dabangvr.fragment.other.UserVideoFragment;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.AppBarLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 查看某用户的信息的页面
 */
public class UserHomeActivity extends BaseActivity {

    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;
    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;

    @BindView(R.id.rlTop)
    RelativeLayout rlTop;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_home;
    }

    @OnClick({R.id.ivBack})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
                default:break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void initView() {
        sdvHead.setImageURI(SPUtils.instance(this).getUser().getHeadUrl());
        List<String> mTitles = new ArrayList<>();
        mTitles.add("回放");
        mTitles.add("作品");
        mTitles.add("动态");
        mFragments = new ArrayList<>();
        mFragments.add(new UserLiveFragment());
        mFragments.add(new UserVideoFragment());
        mFragments.add(new UserDynamicFragment());
        ContentPagerAdapter contentAdapter = new ContentPagerAdapter(getSupportFragmentManager(),mTitles);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void initData() {

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
