package com.dabangvr.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.activity.MainActivity;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.model.MenuMo;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @BindView(R.id.tv_name)
    TextView tvName;
    //动态数量
    @BindView(R.id.tv_dyNum)
    TextView tvDyNum;
    //关注数量
    @BindView(R.id.tv_gzNum)
    TextView tvGzNum;
    //粉丝数量
    @BindView(R.id.tv_fsNum)
    TextView tvFsNum;
    //跳币数量
    @BindView(R.id.tv_tbNum)
    TextView tvTbNum;

    private List<MenuMo> serverData = new ArrayList<>();

    @Override
    public int layoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView() {

//                switch (serverData.get(position).getJumpUrl()) {
//                    case ParameterContens.yhq:
////                        ToastUtil.showShort(getContext(), "优惠券更新中");
//                        startActivity(new Intent(getContext(), MyYhjActivity.class));
//                        break;
//                    case ParameterContens.spsc:
//                        startActivity(new Intent(getContext(), MyScActivity.class));
//                        break;
//                    case ParameterContens.wdpt:
//                        ToastUtil.showShort(getContext(), "我的拼团更新中");
//                        break;
//                    case ParameterContens.wdqb:
//                        startActivity(new Intent(getContext(), SbActivity.class));
//                        break;
//                    case ParameterContens.sjrz:
//                        startActivity(new Intent(getContext(), StartOpenShopActivity.class));
//                        break;//商家入驻
//                    case ParameterContens.zbsq:
//                        startActivity(new Intent(getContext(), StartOpenZhuBoActivity.class));
//                        break;
//                    case ParameterContens.kf:
//                        ToastUtil.showShort(getContext(), "服务更新中");
//                        break;
//                }

    }

    @Override
    public void initData() {


        //初始化用户信息
        UserMess userMess = SPUtils.instance(getContext()).getUser();
        if (userMess != null) {
            sdvHead.setImageURI(userMess.getHeadUrl());
            tvName.setText(userMess.getNickName());
        } else {
            ToastUtil.showShort(getContext(), "获取用户信息失败，请重新登陆");
            //startActivity(new Intent(getContext(), LoginActivity.class));
            //AppManager.getAppManager().finishActivity(MainActivity.class);
            return;
        }

    }

    @OnClick({  R.id.tv_name, R.id.sdvHead})
    public void onTouchClick(View view) {
//        switch (view.getId()) {
//            case R.id.tv_order:
//                startActivity(new Intent(getContext(), MyOrtherActivity.class));
//                break;//全部订单
//            case R.id.tv_dfk:
//                Intent intent = new Intent(getContext(), MyOrtherActivity.class);
//                intent.putExtra("position", 1);
//                startActivity(intent);
//                break;//待付款
//            case R.id.tv_dfh:
//                Intent intent1 = new Intent(getContext(), MyOrtherActivity.class);
//                intent1.putExtra("position", 2);
//                startActivity(intent1);
//                break;//待发货
//            case R.id.tv_dsh:
//                Intent intent2 = new Intent(getContext(), MyOrtherActivity.class);
//                intent2.putExtra("position", 3);
//                startActivity(intent2);
//                break;//待收货
//            case R.id.tv_dpj:
//                Intent intent3 = new Intent(getContext(), MyOrtherActivity.class);
//                intent3.putExtra("position", 4);
//                startActivity(intent3);
//                break;//待评价
//            case R.id.tv_tkth:
//                Intent intent4 = new Intent(getContext(), MyOrtherActivity.class);
//                intent4.putExtra("position", 5);
//                startActivity(intent4);
//                break;//退款退货
//            case R.id.tv_name:
//                startActivity(new Intent(getContext(), UserMessActivity.class));
//                break;//昵称点击
//            case R.id.sdvHead:
//                startActivity(new Intent(getContext(), UserMessActivity.class));
//                break;//头像点击
//            case R.id.tv_set:
//                startActivity(new Intent(getContext(), UserMessActivity.class));
//                break;//设置点击
//            case R.id.iv_message:
//                startActivity(new Intent(getContext(), MyMessageActivity.class));
//                break;//消息点击
//            default:break;
//        }
    }
}
