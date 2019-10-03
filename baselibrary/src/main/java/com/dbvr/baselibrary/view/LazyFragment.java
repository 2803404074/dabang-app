package com.dbvr.baselibrary.view;

import android.os.Bundle;

/**
 * 懒加载fragment
 * 只有看见这个fragment才加载数据
 * loadData() 只执行一次
 */
public abstract class LazyFragment extends BaseFragment {

    protected boolean isViewInitiated; //控件是否初始化完成
    protected boolean isVisibleToUser; //页面是否可见
    protected boolean isDataInitiated; //数据是否加载


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        isVisibleToUserFunction(isVisibleToUser);
        prepareFetchData(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareFetchData(false);
    }

    public abstract void loadData();

    public abstract void isVisibleToUserFunction(boolean isDataInitiated);


    protected void prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            loadData();
            isDataInitiated = true;
        }
    }
}
