package com.dabangvr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.fragment.home.HomeFragmentFollow;
import com.dabangvr.fragment.other.UserVideoFragment;
import com.dabangvr.util.ShareUtils;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MusicActivity extends BaseActivity {

    @BindView(R.id.ivCollection)
    ImageView ivCollection;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;
    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter contentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_music;
    }

    @Override
    public void initView() {
        List<String> mTitles = new ArrayList<>();
        mTitles.add("作品");
        mTitles.add("最新");
        mFragments = new ArrayList<>();
        mFragments.add(new UserVideoFragment());
        mFragments.add(new UserVideoFragment());
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager(),mTitles,mFragments);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(2);
    }

    @OnClick({R.id.ivBack,R.id.ivShare,R.id.ivCollection,R.id.ivPlay})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.ivShare:
                ShareUtils.getInstance(getContext()).startShare("音乐","谁的音乐最好听?","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573495921128&di=16cc8f37d011c05d25ddb32438a2a8d8&imgtype=0&src=http%3A%2F%2Fimg.9ku.com%2Fgeshoutuji%2Fsingertuji%2F1%2F17169%2F17169_1.jpg","www.baidu.com");
                break;
            case R.id.ivCollection:
                ivCollection.setImageResource(R.mipmap.collect);
                break;
            case R.id.ivPlay:
                ToastUtil.showShort(getContext(),"该音乐已受到原创保护");
                break;
                default:break;
        }
    }

    @Override
    public void initData() {

    }
}
