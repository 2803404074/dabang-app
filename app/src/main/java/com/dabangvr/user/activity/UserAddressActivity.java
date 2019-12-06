package com.dabangvr.user.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.model.AddressMo;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户编辑信息
 */
public class UserAddressActivity extends BaseActivity{

    @BindView(R.id.recy_address)
    RecyclerView recyclerView;

    private RecyclerAdapter adapter;

    private List<AddressMo> mData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_address;
    }

    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapter<AddressMo>(getContext(),mData,R.layout.item_address) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, AddressMo o) {
                holder.getView(R.id.tvEdit).setOnClickListener(view -> {
                    goTActivity(UserSetAddressActivity.class,null);
                });
                if (o.getIsDefault() == 1){
                    holder.getView(R.id.tvDefault).setVisibility(View.VISIBLE);
                }else {
                    holder.getView(R.id.tvDefault).setVisibility(View.GONE);
                }
                holder.setText(R.id.tvName, StringUtils.isEmptyTxt(o.getConsigneeName()));
                holder.setText(R.id.tvPhone,StringUtils.isEmptyTxt(o.getConsigneePhone()));
                holder.setText(R.id.tvAddress,StringUtils.isEmptyTxt(o.getAddress()));


            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getAddressList, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                List<AddressMo> list = new Gson().fromJson(result, new TypeToken<List<AddressMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    mData = list;
                    adapter.updateDataa(mData);
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    @OnClick({R.id.ivBack,R.id.tvAddAdd})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tvAddAdd:
                goTActivity(UserSetAddressActivity.class,null);
                break;

        }
    }
}
