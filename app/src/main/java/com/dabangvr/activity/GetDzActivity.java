package com.dabangvr.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 获得的评论
 * 获得的赞
 */
public class GetDzActivity extends BaseActivity {

    private String tag;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycle_dz)
    RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<String> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_get_dz;
    }

    @Override
    public void initView() {
        //是评论还是点赞
        tag = getIntent().getStringExtra("tag");
        if (tag.equals("dz")){
            tvTitle.setText("获赞");
        }
        if (tag.equals("comment")){
            tvTitle.setText("评论");
        }

        for (int i = 0; i < 10; i++) {
            mData.add("数据"+i);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter<String>(this,mData,R.layout.item_dz) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {

                if (tag.equals("comment")){
                    TextView tvComment = holder.getView(R.id.tvComment);
                    tvComment.setVisibility(View.VISIBLE);

                    holder.setText(R.id.tvTips,"评论你的动态    18:55  2019/10/25");
                }

                SimpleDraweeView sdvHead =  holder.getView(R.id.sdvHead);
                sdvHead.setImageURI("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3278119589,2651626912&fm=26&gp=0.jpg");
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack})
    public void onclick(View view){
        if (view.getId() == R.id.ivBack){
            AppManager.getAppManager().finishActivity(this);
        }
    }
}
