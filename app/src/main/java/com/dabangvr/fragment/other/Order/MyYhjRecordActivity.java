package com.dabangvr.fragment.other.Order;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.fragment.other.Order.order_fragment.MyYhjRceordPagerFragment;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;


import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的优惠-历史记录
 */
public class MyYhjRecordActivity extends BaseActivity {

    @BindView(R.id.tab_viewpager)
    ViewPager vp_pager;
    @BindView(R.id.already_used)
    TextView already_used;
    @BindView(R.id.expired)
    TextView expired;
    private String[] tabString = {"0", "1"};
    private ContentPagerAdapter adapter;
    private MyYhjRceordPagerFragment myYhjRceordPagerFragment;

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
        return R.layout.activity_yhj_record;
    }

    @Override
    public void initView() {
        initFragment();
    }

    private void initFragment() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            myYhjRceordPagerFragment = new MyYhjRceordPagerFragment(0);
            myYhjRceordPagerFragment.setTabPos(i, tabString[i]);//设置第几页，以及每页的id
            mFragments.add(myYhjRceordPagerFragment);

        }
        adapter = new ContentPagerAdapter(getSupportFragmentManager(), mFragments);
        vp_pager.setAdapter(adapter);
        //设置当前显示哪个标签页
        vp_pager.setCurrentItem(0);
        already_used.setSelected(true);
        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //滑动监听加载数据，一次只加载一个标签页                刷新标志     排序
                ((MyYhjRceordPagerFragment) adapter.getItem(position)).sendMessage(0, "1");
                setSelecl(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.back, R.id.already_used, R.id.expired})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.already_used:
                vp_pager.setCurrentItem(0);
                already_used.setSelected(true);
                expired.setSelected(false);
                break;
            case R.id.expired:
                vp_pager.setCurrentItem(1);
                already_used.setSelected(false);
                expired.setSelected(true);
                break;
        }
    }

    private void setSelecl(int i) {
        switch (i) {
            case 0:
                already_used.setSelected(true);
                expired.setSelected(false);

                break;
            case 1:
                already_used.setSelected(false);
                expired.setSelected(true);
                break;

        }

    }
}
