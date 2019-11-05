package com.dabangvr.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.model.FansMo;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.ui.ShowButtonLayout;
import com.dbvr.baselibrary.ui.ShowButtonLayoutData;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索页面
 */
public class SearchActivity extends BaseActivity {

    @BindView(R.id.sblHots)
    ShowButtonLayout hotLayout;
    @BindView(R.id.sblHistory)
    ShowButtonLayout historyLayout;

    @BindView(R.id.v_line01)
    View view01;
    @BindView(R.id.v_line02)
    View view02;


    @BindView(R.id.recycle_search)
    RecyclerView recyclerView;
    @BindView(R.id.etInput)
    EditText etInput;

    @BindView(R.id.tvHots)
    TextView tvHots;
    @BindView(R.id.tvHis)
    TextView tvHis;


    private List<FansMo> mData = new ArrayList<>();

    private RecyclerAdapterPosition adapter;

    private List<HomeFindMo.FourMo> listHots;//热门标签
    private List<String> listHistory;//历史


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }


    @Override
    public int setLayout() {
        return R.layout.activity_search;
    }

    @Override
    public void initView() {

        //历史数据
        String history = (String) SPUtils.instance(this).getkey("search_history", "");
        listHistory = new Gson().fromJson(history, new TypeToken<List<String>>() {
        }.getType());
        if (null == listHistory) {
            listHistory = new ArrayList<>();
        }

        //历史搜索
        ShowButtonLayoutData data2 = new ShowButtonLayoutData<>(this,
                historyLayout, listHistory, (v, txt, posi, arg1, arg2, isCheck) -> {
                startSearch(txt);
        });

        data2.setData();

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!StringUtils.isEmpty(etInput.getText().toString())) {
                    hotLayout.setVisibility(View.GONE);
                    historyLayout.setVisibility(View.GONE);
                    tvHis.setVisibility(View.GONE);
                    tvHots.setVisibility(View.GONE);
                    view01.setVisibility(View.GONE);
                    view02.setVisibility(View.GONE);
                    startSearch(etInput.getText().toString());
                } else {
                    hotLayout.setVisibility(View.VISIBLE);
                    historyLayout.setVisibility(View.VISIBLE);
                    tvHis.setVisibility(View.VISIBLE);
                    tvHots.setVisibility(View.VISIBLE);
                    view01.setVisibility(View.VISIBLE);
                    view02.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /****
             *
             * @param v 可以理解为是向上转型的EditText，可以用来操作当前的EditText
             * @param actionId 动作标识，是跟EditorInfo.IME_**这里的值对比可以判断执行了什么动作
             * @param event  跟KeyEvent.ACTION_**比较值判断它的事件
             * @return 如果不往下执行到此结束，返回true，否则为false。
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//判断动作标识是否匹配
                    startSearch(v.getText().toString());
                }
                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapterPosition<FansMo>(getContext(),mData,R.layout.item_fans) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, FansMo o) {
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

                //等级
                if (o.getGrade() == 1){
                    holder.setImageResource(R.id.ivGrade,R.mipmap.u_one);
                }
                if (o.getGrade() == 2){
                    holder.setImageResource(R.id.ivGrade,R.mipmap.u_tow);
                }
                if (o.getGrade() == 3){
                    holder.setImageResource(R.id.ivGrade,R.mipmap.u_three);
                }
                if (o.getGrade() == 4){
                    holder.setImageResource(R.id.ivGrade,R.mipmap.u_four);
                }
                if (o.getGrade() == 5){
                    holder.setImageResource(R.id.ivGrade,R.mipmap.u_five);
                }

                //性别
                if (StringUtils.isEmpty(o.getSex())){
                    holder.setImageResource(R.id.ivSex,R.mipmap.nan_nv);
                }else if (o.getSex().equals("男")){
                    holder.setImageResource(R.id.ivSex,R.mipmap.sex_na);
                }else {
                    holder.setImageResource(R.id.ivSex,R.mipmap.sex_nv);
                }

                holder.getView(R.id.tvGz).setOnClickListener(view -> {
                    if (!o.isMutual()){
                        holder.getView(R.id.tvGz).setBackgroundResource(R.drawable.shape_gray);
                        holder.setText(R.id.tvGz,"已关注");
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

    @Override
    public void initData() {
        //获取热门标签
        Map<String,Object>map = new HashMap<>();
        map.put("page",1);
        map.put("limit",20);
        //获取标签
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.getLiveCategoryList, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result)throws JSONException{
                JSONObject object = new JSONObject(result);
                String records = object.optString("records");
                List<HomeFindMo.FourMo> list = new Gson().fromJson(records, new TypeToken<List<HomeFindMo.FourMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    listHots = list;
                    //热门标签
                    ShowButtonLayoutData data1 = new ShowButtonLayoutData<>(getContext(), hotLayout,
                            listHots, (v, txt, arg1, posi, arg2, isCheck) -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("list", new Gson().toJson(listHots));
                        map.put("position", posi);
                        goTActivity(ZhibTypeActivity.class, map);
                    });
                    data1.setDataType();
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    @OnClick({R.id.tvCancel})
    public void onclick(View view) {
        if (view.getId() == R.id.tvCancel) {
            AppManager.getAppManager().finishActivity(this);
        }
    }

    private int page = 1;

    private void startSearch(String key) {
        Map<String,Object>map = new HashMap<>();
        map.put("page",page);
        map.put("limit",10);
        map.put("key",key);
        map.put("userId",SPUtils.instance(getContext()).getUser().getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.queryUser, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                Log.e("ttttt",result);
                List<FansMo> list = new Gson().fromJson(result, new TypeToken<List<FansMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    mData = list;
                    adapter.updateDataa(mData);
                }
            }

            @Override
            public void onFailed(String msg) {
                Log.e("ttttt",msg);
            }
        });
    }
}
