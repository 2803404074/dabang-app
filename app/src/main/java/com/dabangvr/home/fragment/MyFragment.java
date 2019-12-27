package com.dabangvr.home.fragment;

import android.view.View;

import com.dabangvr.R;
import com.dabangvr.databinding.FragmentMyBinding;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.mall.activity.CartActivity;
import com.dabangvr.mall.activity.OrderActivity;
import com.dabangvr.user.activity.CollectActivity;
import com.dabangvr.user.activity.DepListActivity;
import com.dabangvr.user.activity.FansActivity;
import com.dabangvr.user.activity.FollowActivity;
import com.dabangvr.user.activity.UserAboutActivity;
import com.dabangvr.user.activity.UserDropActivity;
import com.dabangvr.user.activity.UserEditMessActivity;
import com.dabangvr.user.activity.UserSettingActivity;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dbvr.baselibrary.view.BaseFragmentBinding;

import java.util.HashMap;
import java.util.Map;

public class MyFragment extends BaseFragmentBinding<FragmentMyBinding> implements View.OnClickListener {

    private UserMess userMess;
    @Override
    public int layoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView(FragmentMyBinding binding) {
        userMess = UserHolper.getUserHolper(getContext()).getUserMess();
        binding.setUser(userMess);
        binding.inlcude.sdvHead.setImageURI(userMess.getHeadUrl());
        binding.inlcude.tvAddFriend.setOnClickListener(this);
        binding.inlcude.tvFollowOnclick.setOnClickListener(this);
        binding.inlcude.tvFansOnclick.setOnClickListener(this);
        binding.inlcude.tvDropOnclick.setOnClickListener(this);
        binding.ivSearch.setOnClickListener(this);

        binding.llMoney.setOnClickListener(this);
        binding.llOrder.setOnClickListener(this);
        binding.llCollect.setOnClickListener(this);
        binding.llMessInfo.setOnClickListener(this);
        binding.llSet.setOnClickListener(this);
        binding.llAbout.setOnClickListener(this);
        binding.llDep.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvAddFriend:
                goTActivity(SearchActivity.class,null);
                break;
            case R.id.llMoney:
                goTActivity(UserDropActivity.class,null);
                break;
            case R.id.llOrder:
                goTActivity(OrderActivity.class,null);
                break;
            case R.id.llCollect:
                goTActivity(CollectActivity.class,null);
                break;
            case R.id.llMessInfo:
               goTActivity(UserEditMessActivity.class,null);
                break;
            case R.id.llSet:
                goTActivity(UserSettingActivity.class,null);
                break;
            case R.id.llAbout:
                goTActivity(UserAboutActivity.class,null);
                break;

            case R.id.tvFollowOnclick:
                goTActivity(FollowActivity.class,null);
                break;
            case R.id.tvFansOnclick:
                goTActivity(FansActivity.class,null);
                break;
            case R.id.tvDropOnclick:
                goTActivity(UserDropActivity.class,null);
                break;
            case R.id.ivSearch:
                goTActivity(SearchActivity.class,null);
                break;
            case R.id.llDep:
                Map<String,Object>map = new HashMap<>();
                map.put("userId",userMess.getId());
                goTActivity(DepListActivity.class,map);
                break;
            default:break;
        }
    }
}
