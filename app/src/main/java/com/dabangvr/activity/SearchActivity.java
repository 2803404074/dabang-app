package com.dabangvr.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dbvr.baselibrary.ui.ShowButtonLayout;
import com.dbvr.baselibrary.ui.ShowButtonLayoutData;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.recycle_search)
    RecyclerView recyclerView;
    @BindView(R.id.etInput)
    EditText etInput;

    private RecyclerAdapterPosition adapter;

    private List<String> listHots;//热门
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
        //热门搜索
        listHots = new ArrayList<>();
        listHots.add("搞笑");
        listHots.add("舞蹈");
        listHots.add("户外");
        listHots.add("电台");
        listHots.add("音乐");
        listHots.add("派对");
        listHots.add("校园");
        listHots.add("赶海");
        listHots.add("游戏");
        listHots.add("更多");

        //历史数据
        String history = (String) SPUtils.instance(this).getkey("search_history","");
        listHistory = new Gson().fromJson(history, new TypeToken<List<String>>() {}.getType());
        if(null == listHistory){
            listHistory = new ArrayList<>();
        }


        //热门搜索
        ShowButtonLayoutData data1 = new ShowButtonLayoutData<>(this, hotLayout,
                listHots, (v, txt, arg1, arg2, isCheck) -> {
        });

        //历史搜索
        ShowButtonLayoutData data2 = new ShowButtonLayoutData<>(this,
                historyLayout, listHistory, (v, txt, arg1, arg2, isCheck) -> {

                });
        data1.setData();
        data2.setData();

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!StringUtils.isEmpty(etInput.getText().toString())){
                    hotLayout.setVisibility(View.GONE);
                    historyLayout.setVisibility(View.GONE);
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
             * @return  如果不往下执行到此结束，返回true，否则为false。
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//判断动作标识是否匹配
                    startSearch(v.getText().toString());
                }
                return false;
            }
        });
    }

    @Override
    public void initData() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
//        adapter = new RecyclerAdapterPosition<String>(getContext(),null,0) {
//            @Override
//            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {
//
//            }
//        };
//        recyclerView.setAdapter(adapter);
    }

    @OnClick({R.id.tvCancel})
    public void onclick(View view){
        if (view.getId() ==R.id.tvCancel ){
            AppManager.getAppManager().finishActivity(this);
        }
    }

    private void startSearch(String key){

    }
}
