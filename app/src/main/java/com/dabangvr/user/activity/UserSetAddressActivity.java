package com.dabangvr.user.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.model.AddressMo;
import com.dbvr.baselibrary.model.ProvinceMo;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
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

public class UserSetAddressActivity extends BaseActivity {

    @BindView(R.id.tvLocation)
    TextView tvLocation;

    private RecyclerView recycler_01;
    private RecyclerView recycler_02;
    private RecyclerView recycler_03;

    private RecyclerAdapter adapter01;
    private RecyclerAdapter adapter02;
    private RecyclerAdapter adapter03;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_set_address;
    }

    @Override
    public void initView() {
        findViewById(R.id.tvSave).setOnClickListener(view -> {
               save();
        });
    }

    private void save() {

    }
    private List<ProvinceMo>mData01 = new ArrayList<>();
    private List<ProvinceMo.CityMo> mData02 = new ArrayList<>();
    private List<ProvinceMo.XianMo> mData03 = new ArrayList<>();
    private boolean isSelectAll = false;
    private String selectStr = "";

    @OnClick({R.id.tvLocation})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.tvLocation:
                BottomDialogUtil2.getInstance(UserSetAddressActivity.this).show(R.layout.dialog_address,1.7, view1 -> {
                    TextView tvSelectName = view1.findViewById(R.id.tvSelectName);
                    if (!StringUtils.isEmpty(tvLocation.getText().toString())){
                        tvSelectName.setText(tvLocation.getText().toString());
                    }

                    recycler_01 = view1.findViewById(R.id.recycler_01);
                    recycler_02 = view1.findViewById(R.id.recycler_02);
                    recycler_03 = view1.findViewById(R.id.recycler_03);
                    recycler_01.setLayoutManager(new LinearLayoutManager(getParent()));
                    recycler_02.setLayoutManager(new LinearLayoutManager(getParent()));
                    recycler_03.setLayoutManager(new LinearLayoutManager(getParent()));
                    adapter01 = new RecyclerAdapter<ProvinceMo>(getContext(),mData01,R.layout.item_txt) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, ProvinceMo o) {
                            TextView cb_txt = holder.getView(R.id.cb_txt);
                            cb_txt.setText(o.getName());
                            if (o.isCheck()){
                                cb_txt.setBackgroundResource(R.drawable.shape_db);
                            }else {
                                cb_txt.setBackgroundResource(R.drawable.shape_gray_w);
                            }
                        }
                    };
                    recycler_01.setAdapter(adapter01);

                    adapter01.setOnItemClickListener((view2, position) -> {

                        for (int i = 0; i < mData01.size(); i++) {
                            if (i == position){
                                mData01.get(i).setCheck(true);
                            }else {
                                mData01.get(i).setCheck(false);
                            }
                        }
                        adapter01.updateDataa(mData01);

                        mData02 = mData01.get(position).getDistricts();
                        adapter02.updateDataa(mData02);

                        selectStr = mData01.get(position).getName()+"-";
                        tvSelectName.setText(selectStr);

                    });

                    adapter02 = new RecyclerAdapter<ProvinceMo.CityMo>(getContext(),mData02,R.layout.item_txt) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, ProvinceMo.CityMo o) {
                            TextView cb_txt = holder.getView(R.id.cb_txt);
                            cb_txt.setText(o.getName());
                            if (o.isCheck()){
                                cb_txt.setBackgroundResource(R.drawable.shape_db);
                            }else {
                                cb_txt.setBackgroundResource(R.drawable.shape_gray_w);
                            }
                        }
                    };
                    recycler_02.setAdapter(adapter02);

                    adapter02.setOnItemClickListener((view2, position) -> {

                        for (int i = 0; i < mData02.size(); i++) {
                            if (i == position){
                                mData02.get(i).setCheck(true);
                            }else {
                                mData02.get(i).setCheck(false);
                            }
                        }
                        adapter02.updateDataa(mData02);

                        mData03 = mData02.get(position).getDistricts();
                        adapter03.updateDataa(mData03);


                        selectStr = selectStr+mData02.get(position).getName()+"-";
                        tvSelectName.setText(selectStr);
                    });

                    adapter03 = new RecyclerAdapter<ProvinceMo.XianMo>(getContext(),mData03,R.layout.item_txt) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, ProvinceMo.XianMo o) {
                            TextView cb_txt = holder.getView(R.id.cb_txt);
                            cb_txt.setText(o.getName());
                            if (o.isCheck()){
                                cb_txt.setBackgroundResource(R.drawable.shape_db);
                            }else {
                                cb_txt.setBackgroundResource(R.drawable.shape_gray_w);
                            }
                        }
                    };
                    recycler_03.setAdapter(adapter03);

                    adapter03.setOnItemClickListener((view2, position) -> {

                        for (int i = 0; i < mData03.size(); i++) {
                            if (i == position){
                                mData03.get(i).setCheck(true);
                            }else {
                                mData03.get(i).setCheck(false);
                            }
                        }
                        adapter03.updateDataa(mData03);

                        selectStr = selectStr+mData03.get(position).getName();
                        tvSelectName.setText(selectStr);

                        isSelectAll = true;
                        tvLocation.setText(selectStr);
                    });
                });
                break;
                default:break;
        }
    }

    @Override
    public void initData() {
        Map<String,Object>map = new HashMap<>();
        map.put("subdistrict",3);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getAmapDistrict, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                List<ProvinceMo> list = new Gson().fromJson(result, new TypeToken<List<ProvinceMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    mData01 = list;
                }
            }
            @Override
            public void onFailed(String msg) {

            }
        });
    }
}
