package com.dabangvr.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.model.AddressMo;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户编辑信息
 */
public class UserAddressActivity extends BaseActivity{

    @BindView(R.id.recy_address)
    RecyclerView recyclerView;
    @BindView(R.id.tvTip)
    TextView tvTips;

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

    private boolean isGoodsCom = false;
    @Override
    public void initView() {
        boolean isGoods = getIntent().getBooleanExtra("isGoods",false);
        if (isGoods){
            isGoodsCom = true;
        }

        isLoading(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapter<AddressMo>(getContext(),mData,R.layout.item_address) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, AddressMo o) {
                if (o.getIsDefault() == 1){
                    holder.getView(R.id.tvDefault).setVisibility(View.VISIBLE);
                }else {
                    holder.getView(R.id.tvDefault).setVisibility(View.GONE);
                }
                if (o.getAddressTag() == 0){
                    holder.setText(R.id.tvHome,"家");
                }
                if (o.getAddressTag() == 1){
                    holder.setText(R.id.tvHome,"公司");
                }
                if (o.getAddressTag() == 2){
                    holder.setText(R.id.tvHome,"学校");
                }
                holder.setText(R.id.tvName, StringUtils.isEmptyTxt(o.getConsigneeName()));
                holder.setText(R.id.tvPhone,StringUtils.isEmptyTxt(o.getConsigneePhone()));
                holder.setText(R.id.tvAddress,StringUtils.isEmptyTxt(o.getAddress()));

                holder.getView(R.id.tvEdit).setOnClickListener((view)->{
                    Intent intent = new Intent(getContext(),UserSetAddressActivity.class);
                    intent.putExtra("address",o);
                    startActivity(intent);
                });
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            AddressMo addressMo = (AddressMo) adapter.getData().get(position);
            if (isGoodsCom){
                Intent intent = new Intent();
                intent.putExtra("str",addressMo.getConsigneeName()+"-"+addressMo.getConsigneePhone()+"-"+addressMo.getProvince()+addressMo.getCity()+addressMo.getArea()+"-"+addressMo.getAddress());
                intent.putExtra("addressId",addressMo.getId());
                setResult(101,intent);
                finish();
            }
        });
        adapter.setonLongItemClickListener((view, postion) -> {
            DialogUtil.getInstance(this).show(R.layout.dialog_tip, view13 -> {
                TextView title = view13.findViewById(R.id.tv_title);
                title.setText("删除该地址信息？");
                view13.findViewById(R.id.tvCancel).setOnClickListener(view1 -> {
                    DialogUtil.getInstance(UserAddressActivity.this).des();
                });
                view13.findViewById(R.id.tvConfirm).setOnClickListener(view12 -> {
                    AddressMo addressMo = (AddressMo) adapter.getData().get(postion);
                    deleteAddress(addressMo.getId(),postion);
                    DialogUtil.getInstance(UserAddressActivity.this).des();
                });
            });
        });
    }

    private void deleteAddress(int id,int position) {
        Map<String,Object>map = new HashMap<>();
        map.put("id",id);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.deleteAddress, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                mData.remove(position);
                adapter.updateDataa(mData);
            }
            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),StringUtils.isEmptyTxt(msg));
            }
        });
    }

    @Override
    public void initData() {
        getAddressDataList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddressDataList();
    }

    private void getAddressDataList(){
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getAddressList, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                List<AddressMo> list = new Gson().fromJson(result, new TypeToken<List<AddressMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    mData = list;
                    adapter.updateDataa(mData);
                    if (tvTips!=null){
                        runOnUiThread(()->tvTips.setVisibility(View.GONE));
                    }
                }else {
                    if (tvTips!=null){
                        runOnUiThread(()->tvTips.setVisibility(View.VISIBLE));
                    }
                }
                isLoading(false);

            }

            @Override
            public void onFailed(String msg) {
                if (tvTips!=null){
                    runOnUiThread(()->tvTips.setVisibility(View.VISIBLE));
                }
                isLoading(false);
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
