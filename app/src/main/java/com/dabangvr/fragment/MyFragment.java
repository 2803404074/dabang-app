package com.dabangvr.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dabangvr.R;
import com.dabangvr.activity.FansActivity;
import com.dabangvr.activity.FollowActivity;
import com.dabangvr.activity.MyDropActivity;
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

    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.tvFans)
    TextView tvFans;
    @BindView(R.id.tvDropNom)
    TextView tvDropNom;
    @BindView(R.id.ivGrade)
    ImageView ivGrade;

    @BindView(R.id.tvGrade)
    TextView tvGrade;

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

    private UserMess userMess;
    @Override
    public void onResume() {
        super.onResume();
        setUserMess();
    }
    private void setUserMess(){
        userMess = SPUtils.instance(getContext()).getUser();
        if (userMess != null) {
            sdvHead.setImageURI(userMess.getHeadUrl());
            tvNickName.setText(userMess.getNickName());
            tvFollow.setText(userMess.getFollowNumber());
            tvFans.setText(userMess.getFansNumber());
            tvDropNom.setText(String.valueOf(userMess.getDiamond()));

            if (userMess.getGrade() == 1){
                ivGrade.setImageResource(R.mipmap.u_one);
                tvGrade.setText("奋斗白银");
                tvGrade.setTextColor(getResources().getColor(R.color.colorHs2));
            }
            if (userMess.getGrade() == 2){
                ivGrade.setImageResource(R.mipmap.u_tow);
                tvGrade.setText("出道黄金");
                tvGrade.setTextColor(getResources().getColor(R.color.lsq_filter_title_color));
            }
            if (userMess.getGrade() == 3){
                ivGrade.setImageResource(R.mipmap.u_three);
                tvGrade.setTextColor(getResources().getColor(R.color.text2));
                tvGrade.setText("舞台铂金");
            }
            if (userMess.getGrade() == 4){
                ivGrade.setImageResource(R.mipmap.u_four);
                tvGrade.setTextColor(getResources().getColor(R.color.colorZi));
                tvGrade.setText("幕后砖石");
            }
            if (userMess.getGrade() == 5){
                ivGrade.setImageResource(R.mipmap.u_five);
                tvGrade.setTextColor(getResources().getColor(R.color.red));
                tvGrade.setText("孤独王者");
            }


        } else {
            ToastUtil.showShort(getContext(), "获取用户信息失败，请重新登陆");
            //startActivity(new Intent(getContext(), LoginActivity.class));
            //AppManager.getAppManager().finishActivity(MainActivity.class);
            return;
        }
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.sdvHead,R.id.tvFans,R.id.tvFollow,R.id.tvDropNom})
    public void onTouchClick(View view) {
        switch (view.getId()){
            case R.id.tvFans:
                goTActivity(FansActivity.class,null);
                break;
            case R.id.tvFollow:
                goTActivity(FollowActivity.class,null);
                break;
            case R.id.tvDropNom:
                goTActivity(MyDropActivity.class,null);
                break;
        }

    }
}
