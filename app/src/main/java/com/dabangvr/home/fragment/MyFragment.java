package com.dabangvr.home.fragment;

import android.view.View;
import android.widget.TextView;
import com.dabangvr.R;
import com.dabangvr.comment.activity.LoginActivity;
import com.dabangvr.comment.activity.MainActivity;
import com.dabangvr.databinding.FragmentMyBinding;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.mall.activity.CartActivity;
import com.dabangvr.mall.activity.OrderActivity;
import com.dabangvr.user.activity.FansActivity;
import com.dabangvr.user.activity.FollowActivity;
import com.dabangvr.user.activity.UserAboutActivity;
import com.dabangvr.user.activity.UserDropActivity;
import com.dabangvr.user.activity.UserIntroduceActivity;
import com.dabangvr.user.activity.UserMessActivity;
import com.dabangvr.user.activity.UserSettingActivity;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseFragmentBinding;
import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

public class MyFragment extends BaseFragmentBinding<FragmentMyBinding> implements View.OnClickListener {

    @Override
    public int layoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView(FragmentMyBinding binding) {
        UserMess userMess = UserHolper.getUserHolper(getContext()).getUserMess();
        binding.setUser(userMess);
        binding.inlcude.sdvHead.setImageURI(userMess.getHeadUrl());
        binding.inlcude.tvAddFriend.setOnClickListener(this);
        binding.inlcude.tvFollowOnclick.setOnClickListener(this);
        binding.inlcude.tvFansOnclick.setOnClickListener(this);
        binding.inlcude.tvDropOnclick.setOnClickListener(this);
        binding.ivSearch.setOnClickListener(this);

        binding.llMoney.setOnClickListener(this);
        binding.llOrder.setOnClickListener(this);
        binding.llCart.setOnClickListener(this);
        binding.llMessInfo.setOnClickListener(this);
        binding.llSet.setOnClickListener(this);
        binding.llAbout.setOnClickListener(this);
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
            case R.id.llCart:
                goTActivity(CartActivity.class,null);
                break;
            case R.id.llMessInfo:
                goTActivity(UserMessActivity.class,null);
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
            default:break;
        }
    }
}
