package com.dabangvr.user.activity;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.dabangvr.R;
import com.dabangvr.databinding.ActivityUserHomeBinding;
import com.dabangvr.im.ChatActivity;
import com.dabangvr.user.fragment.UserLiveFragment;
import com.dabangvr.user.fragment.UserVideoFragment;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivityBinding;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看某用户的信息的页面
 */
public class UserHomeActivity extends BaseActivityBinding<ActivityUserHomeBinding> implements View.OnClickListener {

    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter contentAdapter;

    private UserMess userMess = new UserMess();

    @Override
    public int setLayout() {
        return R.layout.activity_user_home;
    }

    @Override
    public void initView() {
        mBinding.setUser(userMess);
        List<String> mTitles = new ArrayList<>();
        mTitles.add("直播");
        mTitles.add("视频");
        mFragments = new ArrayList<>();
        mFragments.add(new UserLiveFragment());
        mFragments.add(new UserVideoFragment());
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager(),mTitles,mFragments);
        mBinding.viewPager.setAdapter(contentAdapter);
        mBinding.tabLayout.setViewPager(mBinding.viewPager);
        mBinding.viewPager.setCurrentItem(1);

        mBinding.ivBack.setOnClickListener(this);
        mBinding.inlcude.tvSend.setOnClickListener(this);
        mBinding.inlcude.tvAddFriend.setOnClickListener(this);
        mBinding.inlcude.tvFollowOnclick.setOnClickListener(this);
        mBinding.inlcude.tvFansOnclick.setOnClickListener(this);

        mBinding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                if (verticalOffset == 0){
                    mBinding.appBarLayout.setBackgroundResource(R.drawable.shape_style_user);
                }else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()){

                }else {
                    mBinding.appBarLayout.setBackgroundResource(R.color.colorDb3);
                }
        });
    }

    private void setUserInfo(){
        mBinding.inlcude.sdvHead.setImageURI(userMess.getHeadUrl());
        mBinding.inlcude.tvNickName.setText(userMess.getNickName());
        mBinding.inlcude.tvFollow.setText(userMess.getFollowNumber());
        mBinding.inlcude.tvFans.setText(userMess.getFansNumber());
        mBinding.inlcude.tvDropNom.setText(String.valueOf(userMess.getDiamond()));
        mBinding.tvToolBarNickName.setText(userMess.getNickName());
        if (userMess.getGrade() == 1){ mBinding.inlcude.ivVip.setImageResource(R.mipmap.u_one); }
        if (userMess.getGrade() == 2){ mBinding.inlcude.ivVip.setImageResource(R.mipmap.u_tow); }
        if (userMess.getGrade() == 3){ mBinding.inlcude.ivVip.setImageResource(R.mipmap.u_three); }
        if (userMess.getGrade() == 4){ mBinding.inlcude.ivVip.setImageResource(R.mipmap.u_four); }
        if (userMess.getGrade() == 5){ mBinding.inlcude.ivVip.setImageResource(R.mipmap.u_five); }
        if (userMess.isMutual()){
            mBinding.inlcude.tvSend.setVisibility(View.VISIBLE);
            mBinding.inlcude.tvAddFriend.setVisibility(View.GONE);
        }else {
            mBinding.inlcude.tvSend.setVisibility(View.GONE);
            mBinding.inlcude.tvAddFriend.setText("+ 关注");
            mBinding.inlcude.tvAddFriend.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void initData() {
        Map<String,Object>map = new HashMap<>();
        map.put("userId",getIntent().getStringExtra("userId"));
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getUserByUserId, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                userMess = new Gson().fromJson(result, UserMess.class);
                if (userMess!=null){
                    userMess.setNickName("海跳跳");
                    setUserInfo();
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tvSend:
                Map<String,Object>map = new HashMap<>();
                map.put("hyId",userMess.getId());//这里传环信的ID
                map.put("dName",userMess.getNickName());//这里传环信的ID
                map.put("dHead",userMess.getHeadUrl());
                goTActivity(ChatActivity.class,map);
                break;
            case R.id.tvAddFriend:
                addFriend();
                break;
            case R.id.tvFollowOnclick:
                Map<String,Object>map1 = new HashMap<>();
                map1.put("userId",userMess.getId());
                goTActivity(FollowActivity.class,map1);
                break;
            case R.id.tvFansOnclick:
                Map<String,Object>map2 = new HashMap<>();
                map2.put("userId",userMess.getId());
                goTActivity(FansActivity.class,map2);
                break;
            default:break;
        }
    }

    /**
     * 关注
     */
    private void addFriend() {
        Map<String,Object>map = new HashMap<>();
        map.put("userId",userMess.getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.updateFans, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                ToastUtil.showShort(getContext(),"关注成功");
                mBinding.inlcude.tvSend.setVisibility(View.VISIBLE);
                mBinding.inlcude.tvAddFriend.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }
}
