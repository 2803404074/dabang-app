package com.dabangvr.home.fragment;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dabangvr.play.activity.verticle.PlayActivityCoPy;
import com.dabangvr.util.GDMapLocation;
import com.dbvr.baselibrary.model.MainMo;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class CityFragment extends BaseFragment implements GDMapLocation.MapEvevt {

    private GDMapLocation gdMapLocation;
    private GDMapLocation.MapEvevt evevt;

    @BindView(R.id.tvLocationName)
    TextView tvLocationName;

    @BindView(R.id.tvRecyclerShow)
    TextView tvShow;
    @BindView(R.id.llLoading)
    LinearLayout llLoading;

    @BindView(R.id.recycler_tc)
    RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<MainMo>mData = new ArrayList<>();

    @Override
    public int layoutId() {
        return R.layout.fragment_same_city;
    }

    @Override
    public void initView() {
        recyclerView.setBackgroundResource(R.color.color_f1);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new RecyclerAdapter<MainMo>(getContext(),mData,R.layout.item_main) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, MainMo o) {
                holder.setImageByUrl(R.id.miv_view,o.getCoverUrl());
                holder.setText(R.id.tvLookNum,o.getLookNum());
                SimpleDraweeView sdvHead = holder.getView(R.id.sdvHead);
                sdvHead.setImageURI(o.getHeadUrl());
                holder.setText(R.id.tvUserName,o.getNickName());
                holder.setText(R.id.tvLiveTitle,o.getLiveTitle());
                holder.setText(R.id.tvGoodsTitle,o.getGoodsTitle());
                holder.setText(R.id.tvPrice,o.getGoodsPrice());
                holder.setImageByUrl(R.id.mivGoods,o.getGoodsCover());
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            Map<String,Object>map = new HashMap<>();
            map.put("typeId",2);
            map.put("position",position);
            goTActivity(PlayActivityCoPy.class, map);
        });
    }


    @Override
    public void initData() {
        llLoading.setVisibility(View.VISIBLE);
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
                tvLocationName.setText(StringUtils.isEmptyTxt(locationName));
                String str = object.optString("list");
                List<MainMo> list = new Gson().fromJson(str,
                        new TypeToken<List<MainMo>>() {}.getType());
                if (list != null && list.size()>0){
                    if (page==1){
                        adapter.updateDataa(list);
                    }else {
                        adapter.addAll(list);
                    }
                    if (tvShow!=null){tvShow.setVisibility(View.GONE);}
                }else {
                    if (page == 1){
                        //显示没有数据
                        if (tvShow!=null){tvShow.setVisibility(View.VISIBLE);}
                    }else {
                        //没有更多了
                        if (tvShow!=null){tvShow.setVisibility(View.GONE);}
                        page--;
                    }
                }
                llLoading.setVisibility(View.GONE);
            }
            @Override
            public void onFailed(String msg) {
                tvShow.setVisibility(View.VISIBLE);
                llLoading.setVisibility(View.GONE);
            }
        });
    }
}
