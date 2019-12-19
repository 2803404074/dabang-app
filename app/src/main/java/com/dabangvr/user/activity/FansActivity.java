package com.dabangvr.user.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.model.FansMo;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
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
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 粉丝或关注页面
 */
public class FansActivity extends BaseActivity {

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
                holder.setText(R.id.tvName,o.getNickName());
                TextView tvGz = holder.getView(R.id.tvGz);
                if (o.isMutual()){
                    tvGz.setBackgroundResource(R.drawable.shape_gray);
                    tvGz.setText("已互粉");
                }else {
                    tvGz.setBackgroundResource(R.drawable.shape_red);
                    tvGz.setText("关注");
                }

                holder.getView(R.id.tvGz).setOnClickListener(view -> {
                    if (tvGz.getText().toString().equals("关注")){
                        tvGz.setBackgroundResource(R.drawable.shape_gray);
                        tvGz.setText("已互粉");
                        setLoaddingView(true);
                        followFunction(o.getUserId(),true);
                    }else {
                        tvGz.setBackgroundResource(R.drawable.shape_red);
                        tvGz.setText("关注");
                        setLoaddingView(true);
                        followFunction(o.getUserId(),true);
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
        map.put("userId",getIntent().getIntExtra("userId",0));
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

    @OnClick({R.id.ivBack})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            default:break;
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
    private void followFunction(String userId,boolean isFollow) {
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

        new Thread(()->{
            try {
                if (isFollow){
                    EMClient.getInstance().contactManager().addContact(userId, "关注了你");
                }else {
                    EMClient.getInstance().contactManager().deleteContact(userId);
                }
            }catch (HyphenateException e){
                e.getMessage();
            }
        }).start();

    }

}
