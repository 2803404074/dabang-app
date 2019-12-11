package com.dabangvr.home.fragment;

import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.comment.activity.LoginActivity;
import com.dabangvr.comment.activity.MainActivity;
import com.dabangvr.comment.service.UserHolper;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.mall.activity.CartActivity;
import com.dabangvr.mall.activity.OrderActivity;
import com.dabangvr.user.activity.UserAboutActivity;
import com.dabangvr.user.activity.UserDropActivity;
import com.dabangvr.user.activity.UserIntroduceActivity;
import com.dabangvr.user.activity.UserMessActivity;
import com.dabangvr.user.activity.UserSettingActivity;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseFragment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class MyFragment extends BaseFragment {

    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;
    @BindView(R.id.tvNickName)
    TextView tvNickName;
    @BindView(R.id.tvDropNumber)
    TextView tvDropNumber;
    @BindView(R.id.tvLove)
    TextView tvLove;
    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.tvFans)
    TextView tvFans;
    @BindView(R.id.tvDropNom)
    TextView tvDropNom;

    @Override
    public int layoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView() {
        UserMess userMess = UserHolper.getUserHolper(getContext()).getUserMess();
        if (userMess == null){
            tvNickName.setText("----");
            tvDropNumber.setText("----");
            tvLove.setText("----");
            showLoginTips();
        }else {
            sdvHead.setImageURI(userMess.getHeadUrl());
            tvNickName.setText(userMess.getNickName());
            tvDropNumber.setText(String.valueOf(userMess.getDiamond()));
            tvLove.setText(StringUtils.isEmpty(userMess.getAutograph())?"你还没有填写个人简介,点击添加+":userMess.getAutograph());
        }
    }

    private void showLoginTips() {
        DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip,true, view -> {
              TextView textView = view.findViewById(R.id.tv_title);
              textView.setText("是否前往登陆？");
              view.findViewById(R.id.tvCancel).setOnClickListener((view1)->{
                  DialogUtil.getInstance(getContext()).des();
              });
            view.findViewById(R.id.tvConfirm).setOnClickListener((view1)->{
                goTActivity(LoginActivity.class,null);
                AppManager.getAppManager().finishActivity(MainActivity.class);
            });
        });
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tvAddFriend,R.id.tvLove,R.id.llMoney,R.id.llOrder,R.id.llCart,R.id.llMessInfo,R.id.llSet,R.id.llAbout})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.tvAddFriend:
                goTActivity(SearchActivity.class,null);
                break;
            case R.id.tvLove:
                Map<String,Object>map = new HashMap<>();
                map.put("str",tvLove.getText().toString());
                goTActivity(UserIntroduceActivity.class,map);
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
                default:break;
        }
    }
}
