package com.dabangvr.user.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dabangvr.R;
import com.dabangvr.user.fragment.UserLiveFragment;
import com.dabangvr.user.fragment.UserVideoFragment;

import com.dabangvr.im.ChatActivity;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 查看某用户的信息的页面
 */
public class UserHomeActivity extends BaseActivity {

    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;

    @BindView(R.id.ivGrade)
    ImageView ivGrade;
    @BindView(R.id.tvGrade)
    TextView tvGrade;

    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.tvFans)
    TextView tvFans;
    @BindView(R.id.tvDropNom)
    TextView tvDropNom;

    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;

    @BindView(R.id.tvSend)
    TextView tvSend;
    @BindView(R.id.rlTop)
    RelativeLayout rlTop;

    @BindView(R.id.tvLove)
    TextView tvLove;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter contentAdapter;

    @BindView(R.id.tvNickName)
    TextView tvNickName;
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
        List<String> mTitles = new ArrayList<>();
        mTitles.add("回放");
        mTitles.add("作品");
        mFragments = new ArrayList<>();
        mFragments.add(new UserLiveFragment());
        mFragments.add(new UserVideoFragment());
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager(),mTitles,mFragments);
        viewPager.setAdapter(contentAdapter);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    private String id;
    private String url;
    @Override
    public void initData() {
        Map<String,Object>map = new HashMap<>();
        map.put("userId",getIntent().getStringExtra("userId"));
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getUserByUserId, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                UserMess userMess = new Gson().fromJson(result, UserMess.class);
                if (userMess!=null){
                    setUserMess(userMess);
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    private void setUserMess(UserMess userMess) {
        id = String.valueOf(userMess.getId());
        url = userMess.getHeadUrl();
        sdvHead.setImageURI(userMess.getHeadUrl());
        tvNickName.setText(userMess.getNickName());
        tvFollow.setText(userMess.getFollowNumber());
        tvFans.setText(userMess.getFansNumber());
        tvDropNom.setText(String.valueOf(userMess.getDiamond()));
        tvLove.setText(StringUtils.isEmptyTxt(userMess.getAutograph()));
        if (userMess.isMutual()){
            tvSend.setVisibility(View.VISIBLE);
        }else {
            tvSend.setVisibility(View.GONE);
        }
        if (userMess.getGrade() == 1){
            ivGrade.setImageResource(R.mipmap.u_one);
            tvGrade.setText("奋斗白银");
            tvGrade.setTextColor(getResources().getColor(R.color.colorGray4));
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

    }

    @OnClick(R.id.tvSend)
    public void click(View view){
        if (view.getId() == R.id.tvSend){
            Map<String,Object>map = new HashMap<>();
            map.put("hyId",id);//这里传环信的ID
            map.put("dName",tvNickName.getText().toString());//这里传环信的ID
            map.put("dHead",url);
            goTActivity(ChatActivity.class,map);
        }
    }

}
