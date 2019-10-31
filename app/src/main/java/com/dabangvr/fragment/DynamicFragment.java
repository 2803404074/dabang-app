package com.dabangvr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.activity.UserHomeActivity;
import com.dabangvr.fragment.other.UserDynamicFragment;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.DynamicMo;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.imglibrary2.model.Image;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        initBra();
        List<String> mTitles = new ArrayList<>();
        mTitles.add("关注");
        mTitles.add("好友");
        mTitles.add("附近");
        mFragments = new ArrayList<>();
        mFragments.add(new UserDynamicFragment(this,0));
        mFragments.add(new UserDynamicFragment(this,1));
        mFragments.add(new UserDynamicFragment(this,2));
        ContentPagerAdapter contentAdapter = new ContentPagerAdapter(getChildFragmentManager(),mTitles,mFragments);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(3);
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

    private void initBra() {
        IntentFilter filter = new IntentFilter("android.intent.action.DYNAMIC");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
    }
    private MessageBroadcastReceiver receiver = new MessageBroadcastReceiver();
    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            viewPager.setCurrentItem(0);
        }
    }

}
