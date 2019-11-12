package com.dabangvr.fragment.home;


import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.activity.MessageActivity;
import com.dabangvr.activity.SearchActivity;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.eventBus.ReadEvent;
import com.dbvr.baselibrary.view.BaseFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    private ContentPagerAdapter contentAdapter;

    public void setChangeCallBack(ChangeCallBack changeCallBack) {
        this.changeCallBack = changeCallBack;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView() {
        setLoaddingView(true);
        List<String> mTitles = new ArrayList<>();
        mTitles.add("关注");
        mTitles.add("唱歌");
        mTitles.add("跳舞");
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragmentFollow());
        mFragments.add(new HomeFragmentFind());
        homeFragmentRecommend = new HomeFragmentRecommend();
        mFragments.add(homeFragmentRecommend);
        contentAdapter = new ContentPagerAdapter(getChildFragmentManager(),mTitles,mFragments);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(4);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if (position == 3){
                    //设置底部透明
                    changeCallBack.change(true,false);
                }else {
                    changeCallBack.change(false,false);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setOnTabClickListener(position -> {
            Log.d("luhuas", "onTabClicked: "+position);
            EventBus.getDefault().post(new ReadEvent("1001", 1001, String.valueOf(position)));
        });
        setLoaddingView(false);
    }

    @OnClick({R.id.ivSearch})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivSearch:
                goTActivity(SearchActivity.class,null);
                break;
                default:break;
        }
    }

    @Override
    public void initData() {

    }

    public interface ChangeCallBack{
        void change(boolean isCheck,boolean isClickMess);//isClickMess是否点击到消息
    }

}
