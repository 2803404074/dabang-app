package com.dabangvr.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.model.FansMo;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 粉丝页面
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
        String str = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3278119589,2651626912&fm=26&gp=0.jpg";
        mData.add(new FansMo("用户1",str,"08:47",false));
        mData.add(new FansMo("用户2",str,"08:47",true));
        mData.add(new FansMo("用户2",str,"08:47",false));
        mData.add(new FansMo("用户3",str,"08:47",true));
        mData.add(new FansMo("用户4",str,"08:47",true));
        mData.add(new FansMo("用户5",str,"08:47",false));
        mData.add(new FansMo("用户6",str,"08:47",true));
        mData.add(new FansMo("用户7",str,"08:47",false));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter<FansMo>(this,mData,R.layout.item_fans) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, FansMo o) {

                SimpleDraweeView sdvHead =  holder.getView(R.id.sdvHead);
                sdvHead.setImageURI(o.getHead());
                sdvHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goTActivity(UserHomeActivity.class,null);
                    }
                });

                if (o.isFollow()){
                    holder.getView(R.id.tvGz).setBackgroundResource(R.drawable.shape_gray);
                    holder.setText(R.id.tvGz,"已关注");
                }else {
                    holder.getView(R.id.tvGz).setBackgroundResource(R.drawable.shape_red);
                }
                holder.setText(R.id.tvName,o.getName());
                holder.setText(R.id.tvTips,"关注了你\t"+o.getDate());

            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }
    @OnClick({R.id.ivBack})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
        }
    }
}
