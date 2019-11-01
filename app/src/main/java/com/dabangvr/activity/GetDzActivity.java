package com.dabangvr.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.model.DzAndComment;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.utils.StatusBarUtil;
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
 * 获得的赞
 */
public class GetDzActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycle_dz)
    RecyclerView recyclerView;
    private RecyclerAdapterPosition adapter;
    private List<DzAndComment> mData = new ArrayList<>();

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapterPosition<DzAndComment>(this, mData, R.layout.item_dz) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, DzAndComment o) {
                if (o.getTag() == 0) {//动态
                    holder.setText(R.id.tvTips, "赞了你的动态\t" + o.getAddTime());
                } else {
                    holder.setText(R.id.tvTips, "赞了你的视频\t" + o.getAddTime());
                }
                holder.setText(R.id.tvNickName, o.getNickName());
                SimpleDraweeView sdvHead = holder.getView(R.id.sdvHead);
                sdvHead.setImageURI(o.getHeadUrl());
                holder.setImageByUrl(R.id.mivImg, o.getCoverUrl());

                sdvHead.setOnClickListener(view -> {
                    Map<String,Object>map = new HashMap<>();
                    map.put("userId",o.getUserId());
                    goTActivity(UserHomeActivity.class, map);
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }

    private int page = 1;

    @Override
    public void initData() {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("limit", 10);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getPraisedList, map,
                new ObjectCallback<String>(getContext()) {
                    @Override
                    public void onUi(String result) {
                        List<DzAndComment> list = new Gson().fromJson(result, new TypeToken<List<DzAndComment>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            mData = list;
                            if (page == 1) {
                                adapter.updateDataa(mData);
                            } else {
                                adapter.addAll(mData);
                            }
                        }
                        Log.e("aaaa", result);
                    }

                    @Override
                    public void onFailed(String msg) {
                        Log.e("aaaa", msg);
                    }
                });
    }

    @OnClick({R.id.ivBack})
    public void onclick(View view) {
        if (view.getId() == R.id.ivBack) {
            AppManager.getAppManager().finishActivity(this);
        }
    }
}
