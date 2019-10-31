package com.dabangvr.fragment.other;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.activity.AddressActivity;
import com.dabangvr.fragment.other.Order.ProblemActivity;
import com.dabangvr.fragment.other.Order.UserAboutActivity;
import com.dabangvr.fragment.other.Order.UserMessActivity;
import com.dabangvr.fragment.other.Order.UserSJRZOneActivity;
import com.dabangvr.fragment.other.Order.UserSettingActivity;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.application.MyApplication;
import com.dabangvr.fragment.other.Order.MyOrtherActivity;
import com.dabangvr.fragment.other.Order.MyShoppingCartActivity;
import com.dabangvr.fragment.other.Order.MyYhjActivity;
import com.dabangvr.fragment.other.Order.UserZBSQOneActivity;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.model.MenuBean;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

/**
 * 纯列表的fragment
 */
public class UserPersonalFragment extends BaseFragment {

    private static final int SELECT_IMAGE_REQUEST = 0x1111;
    @BindView(R.id.recycler_head)
    RecyclerView recyclerView;

    private List<MenuBean> mData = new ArrayList<>();
    private RecyclerAdapter adapter;
    private List<MenuBean> menuBeanList;

    @Override
    public int layoutId() {
        return R.layout.recy_menu;
    }

    @Override
    public void initView() {


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RecyclerAdapter<MenuBean>(getContext(), mData, R.layout.item_user_personal) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, MenuBean menuBean) {
                holder.setText(R.id.row_title, menuBean.getTitle());
                ImageView simpleDraweeView = holder.getView(R.id.row_image);
                Glide.with(mContext).load(menuBean.getIconUrl()).into(simpleDraweeView);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("luhuas", "onItemClick: " + position);
                switch (menuBeanList.get(position).getJumpUrl()) {
                    case ParameterContens.CLIENT_WDDD:  //我的订单
                        goTActivity(MyOrtherActivity.class, null);
                        break;
                    case ParameterContens.CLIENT_YHQ://优惠卷
                        goTActivity(MyYhjActivity.class, null);
                        break;
                    case ParameterContens.CLIENT_WDPJ://我的评价
                        break;
                    case ParameterContens.CLIENT_GWC://购物车
                        goTActivity(MyShoppingCartActivity.class, null);
                        break;
                    case ParameterContens.CLIENT_GRZL://个人资料
                        goTActivity(UserMessActivity.class, null);
                        break;
                    case ParameterContens.CLIENT_SJRZ://商家入驻
                        goTActivity(UserSJRZOneActivity.class, null);
                        break;
                    case ParameterContens.CLIENT_ZBSQ://主播申请
                        goTActivity(UserZBSQOneActivity.class, null);
                        break;
                    case ParameterContens.CLIENT_SZ:  //设置
                        goTActivity(UserSettingActivity.class, null);
                        break;
                    case ParameterContens.CLIENT_FK://反馈
                        goTActivity(ProblemActivity.class, null);
                        break;
                    case ParameterContens.CLIENT_GFKF://官方客服
                        Intent intent = new Intent(getContext(), AddressActivity.class);
                        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
                        break;
                    case ParameterContens.CLIENT_GYWM://关于我们
                        goTActivity(UserAboutActivity.class, null);
                        break;

                }
            }
        });
    }

    @Override
    public void initData() {

        getData();
    }

    private void getData() {

        String menuBeanStr = (String) SPUtils.instance(getContext()).getkey("getChannelMenuList", "");
        if (!TextUtils.isEmpty(menuBeanStr)) {
            try {
                menuBeanList = new Gson().fromJson(menuBeanStr, new TypeToken<List<MenuBean>>() {
                }.getType());
                adapter.updateData(menuBeanList);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                getChannelMenu();
            }
        } else {

            getChannelMenu();
        }
    }

    private void getChannelMenu() {
        Map<String, Object> map = new HashMap<>();
        map.put("mallSpeciesId", 8);
        //获取标签
        OkHttp3Utils.getInstance(MyApplication.getInstance()).doPostJson(DyUrl.getChannelMenuList, map, new ObjectCallback<String>(MyApplication.getInstance()) {
            @Override
            public void onUi(String result) throws JSONException {

                menuBeanList = new Gson().fromJson(result, new TypeToken<List<MenuBean>>() {
                }.getType());
                adapter.updateData(menuBeanList);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                String address = data.getStringExtra("address");
                Log.d("luhuas", "onActivityResultaddress=: " + address);
            }
        }
    }
}
