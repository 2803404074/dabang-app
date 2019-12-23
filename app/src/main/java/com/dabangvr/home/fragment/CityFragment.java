package com.dabangvr.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.amap.api.location.AMapLocation;
import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dabangvr.databinding.FragmentSameCityBinding;
import com.dabangvr.play.activity.verticle.PlayActivity;
import com.dabangvr.util.GDMapLocation;
import com.dbvr.baselibrary.model.MainMo;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.BaseFragmentBinding;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.liteav.demo.my.activity.VideoActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityFragment extends BaseFragmentBinding<FragmentSameCityBinding> implements GDMapLocation.MapEvevt {

    private GDMapLocation gdMapLocation;
    private GDMapLocation.MapEvevt evevt;
    private RecyclerAdapter adapter;
    private List<MainMo>mData = new ArrayList<>();
    @Override
    public int layoutId() {
        return R.layout.fragment_same_city;
    }

    @Override
    public void initView(FragmentSameCityBinding binding) {
        isLoading(true);
        mBinding.recyclerTc.setBackgroundResource(R.color.color_f1);
        mBinding.recyclerTc.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new RecyclerAdapter<MainMo>(getContext(),mData,R.layout.item_main) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, MainMo o) {
                //公共信息
                holder.setImageByUrl(R.id.miv_view,o.getCoverUrl());//封面
                holder.setHeadByUrl(R.id.sdvHead,o.getHeadUrl());//头像
                holder.setText(R.id.tvUserName,o.getNickName());//昵称
                holder.setText(R.id.tvLiveTitle,o.getTitle());//标题
                holder.setText(R.id.tvGoodsTitle,o.getGoodsTitle());//商品标题
                holder.setText(R.id.tvPrice,o.getGoodsPrice());//商品价钱
                holder.setImageByUrl(R.id.mivGoods,o.getGoodsCover());//商品封面
                //直播类型
                if (o.getLive()){
                    holder.getView(R.id.tvTag).setVisibility(View.VISIBLE);//显示"正在直播"字样
                    holder.setImageResource(R.id.ivTable,R.mipmap.see);//改为观看的图片
                    holder.setText(R.id.tvLookNum,o.getLookNum());//观看数量
                }else {
                    holder.getView(R.id.tvTag).setVisibility(View.GONE);//隐藏"正在直播"字样
                    holder.setImageResource(R.id.ivTable,R.mipmap.mess_dz);//把观看的图片改成点赞图片
                    holder.setText(R.id.tvLookNum,o.getPraseCount());
                }
            }
        };
        mBinding.recyclerTc.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            Map<String,Object>map = new HashMap<>();
            MainMo mainMo = (MainMo) adapter.getData().get(position);
            map.put("mainMo",mainMo);
            if (mainMo.getLive()){
                goTActivity(PlayActivity.class, map);
            }else {
                Intent intent = new Intent(getContext(),VideoActivity.class);
                intent.putExtra("mainMo",mainMo);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        //关键代码,初始化
        gdMapLocation=GDMapLocation.getInstance(getContext(),null);
        //检查定位权限
        gdMapLocation.checkLocationPermission();
        evevt=this;
        gdMapLocation.setEvevt(evevt);
    }

    //反馈申请权限处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gdMapLocation.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    //定位成功后处理方法
    @Override
    public void onGetMapInfo(AMapLocation amapLocation) {
        longitude = amapLocation.getLongitude();
        latitude = amapLocation.getLatitude();
        getList();
        gdMapLocation.onStop();
    }

    //停止定位
    @Override
    public void onStop() {
        super.onStop();
        gdMapLocation.onStop();
    }

    //销毁定位
    @Override
    public void onDestroy() {
        super.onDestroy();
        gdMapLocation.onDestroy();
    }

    private int page = 1;

    private double longitude;
    private double latitude;
    private void getList(){
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("longitude",longitude);//经度
        map.put("latitude",latitude);//纬度
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getCityOnlineList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                JSONObject object = new JSONObject(result);
                String locationName = object.optString("locationName");
                mBinding.tvLocationName.setText(StringUtils.isEmptyTxt(locationName));
                String str = object.optString("list");
                List<MainMo> list = new Gson().fromJson(str,
                        new TypeToken<List<MainMo>>() {}.getType());
                if (list != null && list.size()>0){
                    if (page==1){
                        adapter.updateDataa(list);
                    }else {
                        adapter.addAll(list);
                    }
                    mBinding.tvRecyclerShow.setVisibility(View.GONE);
                }else {
                    if (page == 1){
                        //显示没有数据
                        mBinding.tvRecyclerShow.setVisibility(View.VISIBLE);
                    }else {
                        //没有更多了
                        mBinding.tvRecyclerShow.setVisibility(View.GONE);
                        page--;
                    }
                }
                isLoading(false);
            }
            @Override
            public void onFailed(String msg) {
                mBinding.tvRecyclerShow.setVisibility(View.VISIBLE);
                isLoading(false);
            }
        });
    }
}
