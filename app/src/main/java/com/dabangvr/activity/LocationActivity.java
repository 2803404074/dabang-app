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
import com.dbvr.baselibrary.ui.ShowButtonLayout;
import com.dbvr.baselibrary.ui.ShowButtonLayoutData;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class LocationActivity extends BaseActivity{

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
        recyclerViewHots.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapterHots = new RecyclerAdapterPosition<String>(getContext(),listHots,R.layout.item_txt) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {
                holder.setText(R.id.cb_txt,o);
            }
        };
        recyclerViewHots.setAdapter(adapterHots);


        recyclerViewAll.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapterAll = new RecyclerAdapterPosition<String>(getContext(),listAll,R.layout.item_txt) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {
                holder.setText(R.id.cb_txt,o);
            }
        };
        recyclerViewAll.setAdapter(adapterAll);

    }

    private void startLocaion() {

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

    @OnClick({R.id.ivBack})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            default:
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){//用户同意权限,执行我们的操作
                startLocaion();//开始定位
            }else{//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                Toast.makeText(getContext(),"未开启定位权限,请手动到设置去开启权限",Toast.LENGTH_LONG).show();
            }
        }
    }
}
