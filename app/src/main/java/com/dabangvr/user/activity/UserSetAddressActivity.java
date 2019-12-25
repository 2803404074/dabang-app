package com.dabangvr.user.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dabangvr.databinding.ActivityUserSetAddressBinding;
import com.dbvr.baselibrary.model.AddressMo;
import com.dbvr.baselibrary.model.ProvinceMo;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.baselibrary.view.BaseActivityBinding;
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

public class UserSetAddressActivity extends BaseActivityBinding<ActivityUserSetAddressBinding> {

    private RecyclerView recycler_01;
    private RecyclerView recycler_02;
    private RecyclerView recycler_03;

    private RecyclerAdapter adapter01;
    private RecyclerAdapter adapter02;
    private RecyclerAdapter adapter03;

    private TextView tvSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_set_address;
    }

    private String addressId;
    @Override
    public void initView() {
        mBinding.ivBack.setOnClickListener((view)->AppManager.getAppManager().finishActivity(this));

        AddressMo addressMo = (AddressMo) getIntent().getSerializableExtra("address");
        if (addressMo!=null){
            isSelectAll = true;
            addressId = String.valueOf(addressMo.getId());
            mBinding.setMAddress(addressMo);
            if (addressMo.getIsDefault() == 1){
                mBinding.scDef.setChecked(true);
            }else {
                mBinding.scDef.setChecked(false);
            }
            if (addressMo.getAddressTag() == 0){
                addressTag = 0;
                mBinding.rbHome.setChecked(true);
            }
            if (addressMo.getAddressTag() == 1){
                addressTag = 1;
                mBinding.rbHome.setChecked(true);
            }
            if (addressMo.getAddressTag() == 2){
                addressTag = 2;
                mBinding.rbHome.setChecked(true);
            }

            province = addressMo.getProvince();
            city = addressMo.getCity();
            area = addressMo.getArea();

            mBinding.tvLocation.setText(province+"-"+city+"-"+area);

        }

        mBinding.tvSave.setOnClickListener(view -> {
            save();
        });
        mBinding.tvLocation.setOnClickListener((view -> {
            showLocation();
        }));

        mBinding.rGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.rbHome:
                    addressTag = 0;
                    break;
                case R.id.rbCompany:
                    addressTag = 1;
                    break;
                case R.id.rbSchool:
                    addressTag = 2;
                    break;
                default:
                    break;
            }
        });
    }

    private void save() {
        if (StringUtils.isEmpty(mBinding.etName.getText().toString())){
            ToastUtil.showShort(getContext(),"请填写收货人");
            return;
        }
        if (StringUtils.isEmpty(mBinding.etPhone.getText().toString())){
            ToastUtil.showShort(getContext(),"请填写手机号");
            return;
        }
        if (!isSelectAll){
            ToastUtil.showShort(getContext(),"请选择区域");
            return;
        }
        if (StringUtils.isEmpty(mBinding.etAddress.getText().toString())){
            ToastUtil.showShort(getContext(),"请填写详细信息");
            return;
        }
        Map<String,Object>map = new HashMap<>();
        map.put("province",province);
        map.put("city",city);
        map.put("area",area);
        map.put("address",mBinding.etAddress.getText().toString());
        map.put("consigneeName",mBinding.etName.getText().toString());
        map.put("consigneePhone",mBinding.etPhone.getText().toString());
        map.put("isDefault",mBinding.scDef.isChecked()?1:0);
        map.put("addressTag",addressTag);
        if (!StringUtils.isEmpty(addressId)){
            map.put("id",addressId);
        }
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.addAddress, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                AppManager.getAppManager().finishActivity(UserSetAddressActivity.class);
            }

            @Override
            public void onFailed(String msg) {
                runOnUiThread(()->{
                    ToastUtil.showShort(getContext(),StringUtils.isEmptyTxt(msg));
                });
            }
        });
    }

    private List<ProvinceMo> mData01 = new ArrayList<>();
    private List<ProvinceMo.CityMo> mData02 = new ArrayList<>();
    private List<ProvinceMo.XianMo> mData03 = new ArrayList<>();
    private boolean isSelectAll = false;
    private String selectStr = "";

    private int addressTag = 0;//标签，0家,1公司,2学校
    private String province;
    private String city;
    private String area;
    private String provinceRe;
    private String cityRe;
    private String areaRe;
    private void showLocation() {
        BottomDialogUtil2.getInstance(UserSetAddressActivity.this).showLive(R.layout.dialog_address, view1 -> {
            TextView tvSelectName = view1.findViewById(R.id.tvSelectName);
            tvSelect = view1.findViewById(R.id.tvSelect);
            tvSelect.setBackgroundResource(R.color.colorGray7);
            tvSelect.setClickable(false);
            tvSelect.setOnClickListener((view)->{
                province = provinceRe;
                city = cityRe;
                area = areaRe;
                isSelectAll = true;
                mBinding.tvLocation.setText(selectStr);
                BottomDialogUtil2.getInstance(UserSetAddressActivity.this).dess();
            });
            if (!StringUtils.isEmpty(mBinding.tvLocation.getText().toString())) {
                tvSelectName.setText(mBinding.tvLocation.getText().toString());
            }
            recycler_01 = view1.findViewById(R.id.recycler_01);
            recycler_02 = view1.findViewById(R.id.recycler_02);
            recycler_03 = view1.findViewById(R.id.recycler_03);
            recycler_01.setLayoutManager(new LinearLayoutManager(getParent()));
            recycler_02.setLayoutManager(new LinearLayoutManager(getParent()));
            recycler_03.setLayoutManager(new LinearLayoutManager(getParent()));
            adapter01 = new RecyclerAdapter<ProvinceMo>(getContext(), mData01, R.layout.item_txt) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder, ProvinceMo o) {
                    TextView cb_txt = holder.getView(R.id.cb_txt);
                    cb_txt.setText(o.getName());
                    if (o.isCheck()) {
                        cb_txt.setBackgroundResource(R.drawable.shape_db);
                    } else {
                        cb_txt.setBackgroundResource(R.drawable.shape_gray_w);
                    }
                }
            };
            recycler_01.setAdapter(adapter01);

            adapter01.setOnItemClickListener((view2, position) -> {

                for (int i = 0; i < mData01.size(); i++) {
                    if (i == position) {
                        provinceRe = mData01.get(position).getName();
                        mData01.get(i).setCheck(true);
                    } else {
                        mData01.get(i).setCheck(false);
                    }
                }
                adapter01.updateDataa(mData01);

                mData02 = mData01.get(position).getDistricts();
                adapter02.updateDataa(mData02);

                selectStr = mData01.get(position).getName() + "-";
                tvSelectName.setText(selectStr);

            });

            adapter02 = new RecyclerAdapter<ProvinceMo.CityMo>(getContext(), mData02, R.layout.item_txt) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder, ProvinceMo.CityMo o) {
                    TextView cb_txt = holder.getView(R.id.cb_txt);
                    cb_txt.setText(o.getName());
                    if (o.isCheck()) {
                        cb_txt.setBackgroundResource(R.drawable.shape_db);
                    } else {
                        cb_txt.setBackgroundResource(R.drawable.shape_gray_w);
                    }
                }
            };
            recycler_02.setAdapter(adapter02);

            adapter02.setOnItemClickListener((view2, position) -> {

                for (int i = 0; i < mData02.size(); i++) {
                    if (i == position) {
                        cityRe = mData02.get(position).getName();
                        mData02.get(i).setCheck(true);
                    } else {
                        mData02.get(i).setCheck(false);
                    }
                }
                adapter02.updateDataa(mData02);

                mData03 = mData02.get(position).getDistricts();
                adapter03.updateDataa(mData03);


                selectStr = selectStr + mData02.get(position).getName() + "-";
                tvSelectName.setText(selectStr);
            });

            adapter03 = new RecyclerAdapter<ProvinceMo.XianMo>(getContext(), mData03, R.layout.item_txt) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder, ProvinceMo.XianMo o) {
                    TextView cb_txt = holder.getView(R.id.cb_txt);
                    cb_txt.setText(o.getName());
                    if (o.isCheck()) {
                        cb_txt.setBackgroundResource(R.drawable.shape_db);
                    } else {
                        cb_txt.setBackgroundResource(R.drawable.shape_gray_w);
                    }
                }
            };
            recycler_03.setAdapter(adapter03);

            adapter03.setOnItemClickListener((view2, position) -> {

                for (int i = 0; i < mData03.size(); i++) {
                    if (i == position) {
                        tvSelect.setBackgroundResource(R.color.colorDb5);
                        tvSelect.setClickable(true);
                        areaRe = mData03.get(position).getName();
                        mData03.get(i).setCheck(true);
                    } else {
                        mData03.get(i).setCheck(false);
                    }
                }
                adapter03.updateDataa(mData03);

                selectStr = selectStr + mData03.get(position).getName();
                tvSelectName.setText(selectStr);


            });
        });
    }

    @Override
    public void initData() {
        Map<String, Object> map = new HashMap<>();
        map.put("subdistrict", 3);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getAmapDistrict, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                List<ProvinceMo> list = new Gson().fromJson(result, new TypeToken<List<ProvinceMo>>() {
                }.getType());
                if (list != null && list.size() > 0) {
                    mData01 = list;
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }
}
