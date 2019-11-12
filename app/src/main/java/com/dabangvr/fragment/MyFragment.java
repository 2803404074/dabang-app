package com.dabangvr.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.activity.FansActivity;
import com.dabangvr.activity.FollowActivity;
import com.dabangvr.activity.MainActivity;
import com.dabangvr.activity.MyDropActivity;
import com.dabangvr.activity.SearchActivity;
import com.dabangvr.application.MyApplication;
import com.dabangvr.fragment.inter.OnActivityBackCallBack;
import com.dabangvr.fragment.other.Order.MyOrtherActivity;
import com.dabangvr.fragment.other.Order.MyShoppingCartActivity;
import com.dabangvr.fragment.other.Order.UserAboutActivity;
import com.dabangvr.fragment.other.Order.UserMessActivity;
import com.dabangvr.fragment.other.Order.UserSJRZOneActivity;
import com.dabangvr.fragment.other.Order.UserSettingActivity;
import com.dabangvr.fragment.other.Order.UserZBSQOneActivity;
import com.dabangvr.fragment.other.UserDynamicFragment;
import com.dabangvr.fragment.other.UserLiveFragment;
import com.dabangvr.fragment.other.UserVideoFragment;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.model.AnchorVo;
import com.dbvr.baselibrary.model.DepVo;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 同城fragment
 */
public class MyFragment extends BaseFragment implements OnActivityBackCallBack, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    NavigationView navigationView;

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

    public MyFragment() {
        MainActivity.instance.setCallBack(this);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_main2;
    }

    @Override
    public void initView() {
        navigationView.setNavigationItemSelectedListener(this);
        sdvHead.setImageURI(SPUtils.instance(MyApplication.getInstance()).getUser().getHeadUrl());
        List<String> mTitles = new ArrayList<>();
        mTitles.add("作品");
        mTitles.add("动态");
        mTitles.add("回放");
        mFragments = new ArrayList<>();
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

    @OnClick({R.id.sdvHead,R.id.tvFans,R.id.tvFollow,R.id.tvDropNom,R.id.tvAddFriend,R.id.ivFunction})
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
            case R.id.tvAddFriend:
                goTActivity(SearchActivity.class,null);
                break;
            case R.id.ivFunction:
                drawer.openDrawer(GravityCompat.END);
                break;
                default:break;
        }
    }

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @Override
    public void commit() {

        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
    }

    public boolean isOpenEnd(){
        return drawer.isDrawerOpen(GravityCompat.END);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_qb) {
            // Handle the camera action
        } else if (id == R.id.nav_order) {
            goTActivity(MyOrtherActivity.class, null);
        } else if (id == R.id.nav_cart) {
            goTActivity(MyShoppingCartActivity.class, null);
        } else if (id == R.id.nav_mess) {
            goTActivity(UserMessActivity.class, null);
        } else if (id == R.id.nav_zhub) {
            queryAnchorState();
        } else if (id == R.id.nav_ruz) {
            queryAddDeptState();
        }else if (id == R.id.nav_about){
            goTActivity(UserAboutActivity.class, null);
        }else if (id == R.id.nav_set){
            goTActivity(UserSettingActivity.class, null);
        }
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }


    private void queryAnchorState() {
        Map<String, Object> map = new HashMap<>();
//        map.put("phone", phone);
        OkHttp3Utils.getInstance(getContext()).doPostJson(UserUrl.addAnchorState, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                Log.d("luhuas", "onUi: " + result);
                try {
                    AnchorVo anchorVo = new Gson().fromJson(result, AnchorVo.class);
                    if (anchorVo != null) {
                        if (anchorVo.getAuditStatus() == 0) {
                            DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, holder -> {
                                TextView tvTitle = holder.findViewById(R.id.tv_title);
                                TextView tv_massage = holder.findViewById(R.id.tv_massage);
                                String title = "审核不通过";
                                tv_massage.setVisibility(View.VISIBLE);
                                tv_massage.setText(anchorVo.getAuditDescribe());
                                tvTitle.setText(title);
                                holder.findViewById(R.id.tvCancel).setOnClickListener(view1 -> DialogUtil.getInstance(getContext()).des());
                                holder.findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), UserZBSQOneActivity.class);
                                        intent.putExtra(ParameterContens.AnchorVo, anchorVo);
                                        startActivity(intent);
                                        DialogUtil.getInstance(getContext()).des();
                                    }
                                });
                            });
                        } else {
                            DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, holder -> {
                                TextView tvTitle = holder.findViewById(R.id.tv_title);
                                String title = "";
                                if (anchorVo.getAuditStatus() == -1) {
                                    title = "正在审核中";
                                } else if (anchorVo.getAuditStatus() == 1) {
                                    title = "您已经入驻成功";
                                }
                                tvTitle.setText(title);
                                holder.findViewById(R.id.tvCancel).setOnClickListener(view1 -> DialogUtil.getInstance(getContext()).des());
                                holder.findViewById(R.id.tvConfirm).setOnClickListener(view12 -> DialogUtil.getInstance(getContext()).des());
                            });
                        }
                    } else {
                        Intent intent = new Intent(getContext(), UserZBSQOneActivity.class);
                        intent.putExtra(ParameterContens.AnchorVo, anchorVo);
                        startActivity(intent);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    goTActivity(UserZBSQOneActivity.class, null);
                }

            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(), msg);
            }
        });
    }

    /**
     * 查询商家入驻状态
     */
    private void queryAddDeptState() {
        Map<String, Object> map = new HashMap<>();
//        map.put("phone", phone);
        OkHttp3Utils.getInstance(getContext()).doPostJson(UserUrl.addDeptState, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                Log.d("luhuas", "onUi: " + result);
                try {
                    DepVo depVo = new Gson().fromJson(result, DepVo.class);
                    if (depVo != null) {
                        if (depVo.getAuditStatus() == 0) {
                            DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, holder -> {
                                TextView tvTitle = holder.findViewById(R.id.tv_title);
                                TextView tv_massage = holder.findViewById(R.id.tv_massage);
                                String title = "审核不通过";
                                tv_massage.setVisibility(View.VISIBLE);
                                tv_massage.setText(depVo.getAuditDescribe());
                                tvTitle.setText(title);
                                holder.findViewById(R.id.tvCancel).setOnClickListener(view1 -> DialogUtil.getInstance(getContext()).des());
                                holder.findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), UserSJRZOneActivity.class);
                                        intent.putExtra(ParameterContens.depVo, depVo);
                                        startActivity(intent);
                                        DialogUtil.getInstance(getContext()).des();
                                    }
                                });
                            });
                        } else {
                            DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, holder -> {
                                TextView tvTitle = holder.findViewById(R.id.tv_title);
                                String title = "";
                                if (depVo.getAuditStatus() == -1) {
                                    title = "正在审核中";
                                } else if (depVo.getAuditStatus() == 1) {
                                    title = "您已经入驻成功";
                                }
                                tvTitle.setText(title);
                                holder.findViewById(R.id.tvCancel).setOnClickListener(view1 -> DialogUtil.getInstance(getContext()).des());
                                holder.findViewById(R.id.tvConfirm).setOnClickListener(view12 -> DialogUtil.getInstance(getContext()).des());
                            });
                        }
                    } else {
                        Intent intent = new Intent(getContext(), UserSJRZOneActivity.class);
                        intent.putExtra(ParameterContens.depVo, depVo);
                        startActivity(intent);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    goTActivity(UserSJRZOneActivity.class, null);
                }

            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(), msg);
            }
        });
    }
}
