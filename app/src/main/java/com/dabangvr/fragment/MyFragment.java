package com.dabangvr.fragment;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dabangvr.R;
import com.dabangvr.activity.UserMessActivity;
import com.dabangvr.application.MyApplication;
import com.dabangvr.fragment.other.UserPersonalFragment;
import com.dabangvr.fragment.other.UserDynamicFragment;
import com.dabangvr.fragment.other.UserLiveFragment;
import com.dabangvr.fragment.other.UserVideoFragment;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 同城fragment
 */
public class MyFragment extends BaseFragment {
    //头像
    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;

    //昵称
    @BindView(R.id.tvNickName)
    TextView tvNickName;

    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;

    @BindView(R.id.rlTop)
    RelativeLayout rlTop;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ArrayList<Fragment> mFragments;

    @Override
    public int layoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView() {
        sdvHead.setImageURI(SPUtils.instance(MyApplication.getInstance()).getUser().getHeadUrl());
        List<String> mTitles = new ArrayList<>();
        mTitles.add("个人");
        mTitles.add("作品");
        mTitles.add("动态");
        mTitles.add("回放");
        mFragments = new ArrayList<>();
        mFragments.add(new UserPersonalFragment());
        mFragments.add(new UserVideoFragment());
        mFragments.add(new UserDynamicFragment());
        mFragments.add(new UserLiveFragment());
        ContentPagerAdapter contentAdapter = new ContentPagerAdapter(getChildFragmentManager(), mTitles, mFragments);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(0);

    }

    @Override
    public void initData() {

        //初始化用户信息
        UserMess userMess = SPUtils.instance(getContext()).getUser();
        if (userMess != null) {
            sdvHead.setImageURI(userMess.getHeadUrl());
            tvNickName.setText(userMess.getNickName());
        } else {
            ToastUtil.showShort(getContext(), "获取用户信息失败，请重新登陆");
            //startActivity(new Intent(getContext(), LoginActivity.class));
            //AppManager.getAppManager().finishActivity(MainActivity.class);
            return;
        }

    }

    @OnClick({R.id.sdvHead,R.id.tv_edit})
    public void onTouchClick(View view) {
        switch (view.getId()){
            case R.id.tv_edit:
                goTActivity(UserMessActivity.class,null);
                break;
        }

    }
}
