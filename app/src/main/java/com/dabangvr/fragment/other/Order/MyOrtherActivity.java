package com.dabangvr.fragment.other.Order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dabangvr.R;
import com.dabangvr.fragment.other.Order.order_fragment.MyOrtherPageFragment;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.OrtherStatic;
import com.dbvr.baselibrary.model.TabAndViewPagerMo;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人中心-我的订单
 */
public class MyOrtherActivity extends BaseActivity implements MyOrtherPageFragment.LoadingCallBack {


    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter adapter;


    @BindView(R.id.tablayout)
    SmartTabLayout tabLayout;
    @BindView(R.id.tab_viewpager)
    ViewPager vp_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);

    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_orther;
    }

    @Override
    public void initView() {

        mFragments = new ArrayList<>();
    }

    @Override
    public void initData() {
        List<TabAndViewPagerMo> list = OrtherStatic.setData();
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MyOrtherPageFragment fragment = new MyOrtherPageFragment();
            fragment.setOrderStatus(list.get(i).getId());
            fragment.setCallBack(this);
            mFragments.add(fragment);
            titleList.add(list.get(i).getTitle());
        }
        adapter = new ContentPagerAdapter(getSupportFragmentManager(), titleList, mFragments);
        vp_pager.setAdapter(adapter);
        vp_pager.setCurrentItem(0);
        vp_pager.setAdapter(adapter);
        tabLayout.setViewPager(vp_pager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void show() {
        setLoaddingView(true);
    }

    @Override
    public void hide() {
        setLoaddingView(false);
    }

    @OnClick({R.id.backe})
    public void onTouchClick(View view) {
        switch (view.getId()) {
            case R.id.backe:
                AppManager.getAppManager().finishActivity(this);
                break;
            default:
                break;
        }
    }
}
