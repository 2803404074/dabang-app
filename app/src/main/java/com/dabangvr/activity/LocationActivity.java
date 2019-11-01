package com.dabangvr.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dabangvr.fragment.SameCityFragment;
import com.dbvr.baselibrary.ui.ShowButtonLayout;
import com.dbvr.baselibrary.ui.ShowButtonLayoutData;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.dbvr.httplibrart.utils.OtherCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class LocationActivity extends BaseActivity {
    private String TAG = "LocationActivity";

    private final int MY_PERMISSIONS_REQUEST_CALL_LOCATION = 200;
    @BindView(R.id.recycle_hots)
    RecyclerView recyclerViewHots;
    @BindView(R.id.recycle_all)
    RecyclerView recyclerViewAll;

    private List<String> listHots = new ArrayList<>();//热门地区
    private List<String> listAll = new ArrayList<>();//所有地区

    private RecyclerAdapterPosition adapterHots;
    private RecyclerAdapterPosition adapterAll;

    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.llLocation)
    LinearLayout llLocation;
    @BindView(R.id.ivLocation)
    ImageView ivLocation;

    private String cityName = "";
    private String provinceName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        //检查版本是否大于M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_CALL_LOCATION);
            } else {
                //"权限已申请";
                startLocaion();
            }
        }
    }

    @Override
    public int setLayout() {
        return R.layout.activity_location;
    }

    @Override
    public void initView() {
        recyclerViewHots.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapterHots = new RecyclerAdapterPosition<String>(getContext(), listHots, R.layout.item_txt) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {
                CheckBox checkBox = holder.getView(R.id.cb_txt);
                checkBox.setText(o);
                if (mPosition == position) {
                    checkBox.setBackgroundResource(R.drawable.shape_db);
                    checkBox.setTextColor(getResources().getColor(R.color.colorWhite));
                } else {
                    checkBox.setBackgroundResource(R.drawable.shape_gray_w);
                    checkBox.setTextColor(getResources().getColor(R.color.transitionSelectorBG));
                }
            }
        };
        recyclerViewHots.setAdapter(adapterHots);


        recyclerViewAll.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapterAll = new RecyclerAdapterPosition<String>(getContext(), listAll, R.layout.item_txt) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {
                CheckBox checkBox = holder.getView(R.id.cb_txt);
                checkBox.setText(o);
                if (mPositionAll == position) {
                    checkBox.setBackgroundResource(R.drawable.shape_db);
                    checkBox.setTextColor(getResources().getColor(R.color.colorWhite));
                } else {
                    checkBox.setBackgroundResource(R.drawable.shape_gray_w);
                    checkBox.setTextColor(getResources().getColor(R.color.transitionSelectorBG));
                }
            }
        };
        recyclerViewAll.setAdapter(adapterAll);


        adapterHots.setOnItemClickListener((view, position) -> {
            mPosition = position;
            mPositionAll = -1;
            adapterHots.updateDataa(listHots);
            adapterAll.updateDataa(listAll);

            llLocation.setBackgroundResource(R.drawable.shape_gray_w);
            tvLocation.setTextColor(getResources().getColor(R.color.textTitle));
            ivLocation.setImageResource(R.mipmap.location);

            mJd = 0;
            mWd = 0;
            finishRes(listHots.get(position), "");
        });

        adapterAll.setOnItemClickListener((view, position) -> {
            mPositionAll = position;
            mPosition = -1;
            adapterHots.updateDataa(listHots);
            adapterAll.updateDataa(listAll);
            llLocation.setBackgroundResource(R.drawable.shape_gray_w);
            tvLocation.setTextColor(getResources().getColor(R.color.textTitle));
            ivLocation.setImageResource(R.mipmap.location);

            mJd = 0;
            mWd = 0;
            finishRes(listAll.get(position), "");
        });
    }

    private double mJd = 0;
    private double mWd = 0;

    private void finishRes(String provinceName, String cityName) {
        Intent i = new Intent();
        i.putExtra("mProvince", provinceName);
        i.putExtra("mCity", cityName);
        i.putExtra("mJd", mJd);
        i.putExtra("mWd", mWd);
        setResult(SameCityFragment.cityFragmentCode, i);
        finish();
    }

    private int mPosition;
    private int mPositionAll = -1;

    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;

    private void startLocaion() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
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
                getLocation(amapLocation.getLongitude() + "," + amapLocation.getLatitude());
                mJd = amapLocation.getLongitude();
                mWd = amapLocation.getLatitude();

                int type = amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                double lat = amapLocation.getLatitude();//获取纬度
                double lon = amapLocation.getLongitude();//获取经度
                float acc = amapLocation.getAccuracy();//获取精度信息

                //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                String add = amapLocation.getAddress();//详细地址
                String Coun = amapLocation.getCountry();//国家信息
                String Pro = amapLocation.getProvince();//省信息
                String tCity = amapLocation.getCity();//城市信息
                String tStr = amapLocation.getStreet();//街道信息
                String tStree = amapLocation.getStreetNum();//街道门牌号信息
                String tCityC = amapLocation.getCityCode();//城市编码
                String tAdCo = amapLocation.getAdCode();//地区编码

                Log.e("LocationActivity",
                        "\n定位类型:" + type
                                + "\n纬度:" + lat
                                + "\n经度:" + lon
                                + "\n精度信息:" + acc
                                + "\n详细地址:" + add
                                + "\n国家信息:" + Coun
                                + "\n省信息:" + Pro
                                + "\n城市信息:" + tCity
                                + "\n街道信息:" + tStr
                                + "\n街道门牌号信息:" + tStree
                                + "\n城市编码:" + tCityC
                                + "\n地区编码:" + tAdCo);

            } else {
                setLoaddingView(false);
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    };

    private void getLocation(String location) {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.getLocation, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                JSONObject object = new JSONObject(result);
                provinceName = object.optString("province");
                cityName = object.optString("city");
                tvLocation.setText(provinceName + "·" + cityName);
                setLoaddingView(false);
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
                runOnUiThread(() -> {
                    tvLocation.setText("定位失败，请确认GPS或数据流量打开");
                });
            }
        });
    }

    @Override
    public void initData() {
        Resources resources = getResources();
        String[] hots = resources.getStringArray(R.array.province_hots);
        String[] all = resources.getStringArray(R.array.province);
        for (int i = 0; i < hots.length; i++) {
            listHots.add(hots[i]);
        }

        for (int i = 0; i < all.length; i++) {
            listAll.add(all[i]);
        }

        adapterHots.updateDataa(listHots);
        adapterAll.updateDataa(listAll);
    }

    @OnClick({R.id.ivBack, R.id.tvFlush, R.id.llLocation})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.llLocation:
                mPosition = -1;
                ivLocation.setImageResource(R.mipmap.location_w);
                tvLocation.setTextColor(getResources().getColor(R.color.colorWhite));
                llLocation.setBackgroundResource(R.drawable.shape_db);
                adapterHots.updateDataa(listHots);
                finishRes(provinceName, cityName);
                break;
            case R.id.tvFlush:
                setLoaddingView(true);
                startLocaion();
                break;
            default:
                break;
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
}
