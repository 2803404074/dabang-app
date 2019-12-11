package com.dabangvr.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GDMapLocation {
    public static MapEvevt evevt;
    /************定位*********************************************************/
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public String address;//地址信息
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    public static int LOCATION = 300;//定位
    public String mCity;
    public String mDistrict;
    public String mStreet;
    public String mStreetNum;
    //定位成功标志
    public static int LOCATION_OK=400;
    /*************************************************************************/
    public static GDMapLocation instance;
    private Context mContext;
    private Fragment fragment;

    /**
     * Activity中调用fragment为空
     * @param mContext
     * @param fragment
     * @return
     */
    public static GDMapLocation getInstance(Context mContext, Fragment fragment){
        if (instance==null){
            instance=new GDMapLocation(mContext,fragment);
        }
        return instance;
    }

    public GDMapLocation(Context mContext,Fragment fragment) {
        this.mContext = mContext;
        this.fragment=fragment;
    }


    /**
     * 检查定位权限,放在Acivity、Fragment的onCreate或onCreateView方法里
     */
    public void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(findActivity(mContext), Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(mContext,"打开位置权限",Toast.LENGTH_SHORT).show();
            }
            //请求权限
            if(fragment!=null) {
                fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
            }else{
                ActivityCompat.requestPermissions(findActivity(mContext),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
            }
        }else {
            //已经打开定位权限就直接开启定位
            LocationInit();
        }
    }

    /**
     *  申请定位权限结果处理，用在Activity和Fragment的onRequestPermissionsResult方法里
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        if(requestCode==LOCATION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //打开定位权限ok
                Toast.makeText(mContext,"打开定位权限成功",Toast.LENGTH_SHORT).show();
                LocationInit();
            }else {
                //打开定位权限失败
                Toast.makeText(mContext,"打开定位权限失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 通过Context获取Activity
     * @param context
     * @return
     */
    public Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }

    /**
     * 初始化定位
     */
    private void LocationInit(){
        //初始化定位
        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化mLocationOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //启动定位
        mLocationClient.startLocation();
    }



    //声明定位回调监听器
    //可以通过类implement方式实现AMapLocationListener接口，也可以通过创造接口类对象的方法实现
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    //城市信息
                    mCity = amapLocation.getCity();
                    //城区信息
                    mDistrict = amapLocation.getDistrict();
                    //街道信息
                    mStreet = amapLocation.getStreet();
                    //街道门牌号信息
                    mStreetNum = amapLocation.getStreetNum();
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                    amapLocation.getFloor();//获取当前室内定位的楼层
//                    amapLocation.getGpsStatus();//获取GPS的当前状态
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);
                    address = amapLocation.getAddress();
                    if (evevt!=null){
                        evevt.onGetMapInfo(amapLocation);
                    }
                    Log.i("city","" + mCity);

                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    /**
     * 停止定位
     */
    public void onStop() {
        if(mLocationClient!=null) {
            mLocationClient.stopLocation();//停止定位
        }
    }

    /**
     * 开始定位
     */
    public void onStart(){
        if(mLocationClient!=null) {
            mLocationClient.startLocation();//开始定位
        }
    }

    /**
     * 销毁定位
     */
    public void onDestroy() {
        if(mLocationClient!=null) {
            mLocationClient.onDestroy();//销毁定位
        }
    }

    // 自定义接口
    public interface MapEvevt {
        public void onGetMapInfo(AMapLocation amapLocation);
    }

    public static void setEvevt(MapEvevt evevt1) {
        evevt = evevt1;
    }
}
