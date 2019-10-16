package com.dabangvr.activity;

import android.os.Bundle;
import android.view.View;

import com.dabangvr.R;
import com.dbvr.baselibrary.ui.ShowButtonLayout;
import com.dbvr.baselibrary.ui.ShowButtonLayoutData;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
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
        ShowButtonLayoutData data1 = new ShowButtonLayoutData<String>(this, hotLayout,
                listHots, new ShowButtonLayoutData.MyClickListener() {
            @Override
            public void clickListener(View v,String txt, double arg1, double arg2 , boolean isCheck) {

            }
        });

        //历史搜索
        ShowButtonLayoutData data2 = new ShowButtonLayoutData<String>(this,
                historyLayout, listHistory, new ShowButtonLayoutData.MyClickListener() {
            @Override
            public void clickListener(View v,String txt,   double arg1,double arg2 ,boolean isCheck) {

            }
        });
        data1.setData();
        data2.setData();

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tvCancel})
    public void onclick(View view){
        if (view.getId() ==R.id.tvCancel ){
            AppManager.getAppManager().finishActivity(this);
        }
    }
}
