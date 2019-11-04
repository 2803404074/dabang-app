package com.dabangvr.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.ui.PayDialog;
import com.dbvr.baselibrary.model.FansMo;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
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
 * 粉丝或关注页面
 */
public class FansAndFollowActivity extends BaseActivity {

    @BindView(R.id.recycle_dz)
    RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<FansMo> mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }
    @Override
    public int setLayout() {
        return R.layout.activity_fans;
    }

    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter<FansMo>(this,mData,R.layout.item_fans) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, FansMo o) {

                SimpleDraweeView sdvHead =  holder.getView(R.id.sdvHead);
                sdvHead.setImageURI(o.getHeadUrl());
                sdvHead.setOnClickListener(view -> goTActivity(UserHomeActivity.class,null));
                holder.setText(R.id.tvName,o.getNickName());
                if (o.isMutual()){
                    holder.getView(R.id.tvGz).setBackgroundResource(R.drawable.shape_gray);
                    holder.setText(R.id.tvGz,"已互粉");
                }else {
                    holder.getView(R.id.tvGz).setBackgroundResource(R.drawable.shape_red);
                    holder.setText(R.id.tvGz,"关注");
                }

                holder.getView(R.id.tvGz).setOnClickListener(view -> {
                    if (!o.isMutual()){
                        holder.getView(R.id.tvGz).setBackgroundResource(R.drawable.shape_gray);
                        holder.setText(R.id.tvGz,"已互粉");
                        setLoaddingView(true);
                        followFunction(o.getUserId());
                    }else {
                        holder.getView(R.id.tvGz).setBackgroundResource(R.drawable.shape_red);
                        holder.setText(R.id.tvGz,"关注");
                        setLoaddingView(true);
                        followFunction(o.getUserId());
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            Map<String,Object>map = new HashMap<>();
            map.put("userId",mData.get(position).getUserId());
            goTActivity(UserHomeActivity.class,map);
        });
    }

    private int page = 1;
    @Override
    public void initData() {
        Map<String,Object>map = new HashMap<>();
        map.put("page",page);
        map.put("limit",10);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getFansList, map,
                new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                List<FansMo> list = new Gson().fromJson(result, new TypeToken<List<FansMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    mData = list;
                    if (page==1){
                        adapter.updateDataa(mData);
                    }else {
                        adapter.addAll(mData);
                    }
                }
            }

            @Override
            public void onFailed(String msg) {
                Log.e("result","返回："+msg);
            }
        });
    }

    @OnClick({R.id.ivBack,R.id.tvCreateVIP})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tvCreateVIP:
                BottomDialogUtil2.getInstance(this).showLive(R.layout.dialog_vip, view1 -> {
                    TextView tvYear = view1.findViewById(R.id.tvYear);
                    TextView tvThreeMon = view1.findViewById(R.id.tvThreeMon);
                    TextView tvOneMon = view1.findViewById(R.id.tvOneMon);
                    view1.findViewById(R.id.tvYear).setOnClickListener(view2 -> {
                        BottomDialogUtil2.getInstance(this).dess();
                        PayDialog dialog = new PayDialog(getContext(),"","orderSnTotal",null);
                        dialog.showDialog(tvYear.getText().toString());
                    });
                    view1.findViewById(R.id.tvThreeMon).setOnClickListener(view2 -> {
                        BottomDialogUtil2.getInstance(this).dess();
                        PayDialog dialog = new PayDialog(getContext(),"","orderSnTotal",null);
                        dialog.showDialog(tvThreeMon.getText().toString());
                    });
                    view1.findViewById(R.id.tvOneMon).setOnClickListener(view2 -> {
                        BottomDialogUtil2.getInstance(this).dess();
                        PayDialog dialog = new PayDialog(getContext(),"","orderSnTotal",null);
                        dialog.showDialog(tvOneMon.getText().toString());
                    });

                    view1.findViewById(R.id.tvSeeServer).setOnClickListener(view2 -> {

                    });
                    view1.findViewById(R.id.tvSeeDrop).setOnClickListener(view2 -> {
                        goTActivity(MyDropActivity.class,null);
                    });
                });
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BottomDialogUtil2.getInstance(this).dess();
    }

    /**
     * 关注粉丝
     */
    private void followFunction(String userId) {
        setLoaddingView(true);
        Map<String,Object>map = new HashMap<>();
        map.put("fansUserId",userId);
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.updateFans, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result){
                setLoaddingView(false);
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),msg);
                setLoaddingView(false);
            }
        });
    }

}
