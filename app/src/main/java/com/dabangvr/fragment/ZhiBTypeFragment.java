package com.dabangvr.fragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dabangvr.play.activity.PlayActivity;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseFragmentFromType;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class ZhiBTypeFragment extends BaseFragmentFromType {

    @BindView(R.id.ll_load)
    LinearLayout llLoad;

    @BindView(R.id.recycler_head)
    RecyclerView recyclerView;
    private RecyclerAdapterPosition adapter;
    private List<HomeFindMo.TowMo> mData = new ArrayList<>();

    public ZhiBTypeFragment(int cType) {
        super(cType);
    }

    @Override
    protected int initLayout() {
        return R.layout.recy_load;
    }

    @Override
    protected void initView() {
        setLinearLayout(llLoad);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new RecyclerAdapterPosition<HomeFindMo.TowMo>(getContext(),mData,R.layout.item_conver_match) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, HomeFindMo.TowMo o) {
                SimpleDraweeView myImageView = holder.getView(R.id.miv_view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                if (position % 2 != 0){
                    params.setMargins(3,3,0,0);//左边的item
                } else{
                    params.setMargins(0, 3, 3, 0);//右边的item
                }
                myImageView.setLayoutParams(params);

                //直播类型
                if (!StringUtils.isEmpty(o.getCoverUrl())){
                    holder.getView(R.id.tvTag).setVisibility(View.VISIBLE);
                    holder.setText(R.id.tvTitle,o.getLiveTitle());
                    holder.setImageResource(R.id.ivTable,R.mipmap.see);
                    holder.setText(R.id.zb_likeCounts,o.getLookNum());
                    SimpleDraweeView sdv = holder.getView(R.id.miv_view);
                    sdv.setImageURI(o.getCoverUrl());


                    //短视频类型
                }else {
                    holder.getView(R.id.tvTag).setVisibility(View.GONE);
                    holder.setText(R.id.tvTitle,o.getTitle());
                    holder.setImageResource(R.id.ivTable,R.mipmap.heart_zb);
                    holder.setText(R.id.zb_likeCounts,o.getPraseCount());
                    SimpleDraweeView sdv = holder.getView(R.id.miv_view);
                    sdv.setImageURI(o.getCoverPath());
                }
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            HomeFindMo.TowMo mo = (HomeFindMo.TowMo) adapter.getData().get(position);
            Map<String,Object>map = new HashMap<>();
            map.put("url",mo.getFname());
            map.put("roomId",mo.getRoomId());
            map.put("nickName",mo.getNickName());
            map.put("liveTag",mo.getLiveTag());
            map.put("lookNum",mo.getLookNum());
            map.put("headUrl",mo.getHeadUrl());
            map.put("userId",mo.getUserId());
            map.put("isFollow",mo.isFollow());
            goTActivity(PlayActivity.class,map);
        });

    }

    private int page = 1;
    @Override
    protected void setDate() {
        setLoaddingView(true);
        Map<String,Object>map = new HashMap<>();
        map.put("liveTag",getcType());
        map.put("page",page);
        map.put("limit",10);
        map.put("userId", SPUtils.instance(getContext()).getUser().getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getOnlineList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                List<HomeFindMo.TowMo> list = new Gson().fromJson(result, new TypeToken<List<HomeFindMo.TowMo>>() {}.getType());
                if (list!=null){
                    mData = list;
                    if (page==1){
                        adapter.updateDataa(mData);
                    }else {
                        adapter.addAll(mData);
                    }
                }
                setLoaddingView(false);
                Log.e("bbbbb",result);
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),msg);
                setLoaddingView(false);
                Log.e("bbbbb",""+msg);
            }
        });
    }
}
