package com.dabangvr.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dabangvr.R;
import com.dabangvr.activity.LocationActivity;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.dbvr.httplibrart.utils.OtherCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 同城fragment
 */
public class SameCityFragment extends BaseFragment {

    public static final int cityFragmentCode = 103;
    @BindView(R.id.recycler_tc)
    RecyclerView recyclerView;

    @BindView(R.id.tvLocationName)
    TextView tvLocationName;

    private RecyclerAdapterPosition adapter;

    private List<HomeFindMo.TowMo> mData = new ArrayList<>();


    private final int MY_PERMISSIONS_REQUEST_CALL_LOCATION = 200;


    @Override
    public int layoutId() {
        return R.layout.fragment_same_city;
    }

    @Override
    public void initView() {
        //检查版本是否大于M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_CALL_LOCATION);
            } else {
                //"权限已申请";
                setLoaddingView(true);
                startLocaion();
            }
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new RecyclerAdapterPosition<HomeFindMo.TowMo>(getContext(), mData, R.layout.item_conver_match) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, HomeFindMo.TowMo o) {
                holder.setImageByUrl(R.id.miv_view, o.getCoverUrl());
                holder.setText(R.id.tvTitle, o.getLiveTitle());
                holder.setText(R.id.zb_likeCounts, o.getLookNum());

                ImageView myImageView = holder.getView(R.id.miv_view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                if (position % 2 != 0) {
                    params.setMargins(1, 1, 0, 0);//左边的item
                } else {
                    params.setMargins(0, 1, 1, 0);//右边的item
                }
                myImageView.setLayoutParams(params);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.llLocation})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.llLocation:
                goTActivityForResult(LocationActivity.class, null, 101);//101请求码，用于返回设置定位值
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 101 && resultCode == cityFragmentCode) {
            mJd = data.getDoubleExtra("mJd", 0);
            mWd = data.getDoubleExtra("mWd", 0);
            mCity = data.getStringExtra("mCity");
            mProvince = data.getStringExtra("mProvince");
            tvLocationName.setText(StringUtils.isEmpty(mCity) ? mProvince : mCity);
            getList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意权限,执行我们的操作
                startLocaion();//开始定位
            } else {//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                Toast.makeText(getContext(), "未开启定位权限,请手动到设置去开启权限", Toast.LENGTH_LONG).show();
            }
        }
    }

    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;

    private void startLocaion() {
        mLocationClient = new AMapLocationClient(getContext());
        mLocationClient.setLocationListener(mLocationListener);

        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    //声明定位回调监听器
    public AMapLocationListener mLocationListener = amapLocation -> {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mJd = amapLocation.getLongitude();
                mWd = amapLocation.getLatitude();
                getLocation(amapLocation.getLongitude() + "," + amapLocation.getLatitude());
            } else {
                setLoaddingView(false);
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    };

    private String mProvince;
    private String mCity;
    private double mJd;
    private double mWd;

    private void getLocation(String location) {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getLocation, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                JSONObject object = new JSONObject(result);
                mProvince = object.optString("province");
                mCity = object.optString("city");
                tvLocationName.setText(StringUtils.isEmpty(mCity) ? mProvince : mCity);
                //根据省市及经纬度获取列表
                page = 1;
                getList();
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    tvLocationName.setText("定位失败，请确认GPS或数据流量打开");
                });
            }
        });
    }

    private int page = 1;

    private void getList() {
        Map<String, Object> map = new HashMap<>();
        map.put("longitude", mJd);
        map.put("latitude", mWd);
        map.put("province", mProvince);
        map.put("city", mCity);
        map.put("page", page);
        map.put("userId", SPUtils.instance(getContext()).getUser().getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getOnlineList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                List<HomeFindMo.TowMo> list = new Gson().fromJson(result,
                        new TypeToken<List<HomeFindMo.TowMo>>() {}.getType());

                if (list != null && list.size() > 0) {
                    mData = list;
                    if (page > 1) {
                        adapter.addAll(mData);
                    } else {
                        adapter.updateDataa(mData);
                    }
                }
                setLoaddingView(false);
            }
            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
            }
        });
    }

}
