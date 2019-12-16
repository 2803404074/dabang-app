package com.dbvr.baselibrary.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dbvr.baselibrary.R;
import com.dbvr.baselibrary.model.MainMo;
import com.dbvr.baselibrary.utils.DialogUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * fragment基类
 * 针对 展示多类型商品的页面
 * 懒加载
 * auth 黄仕豪
 * 超过3gefragment 绑定控件用findviewbyid，原因butterknife滑动过多的话，基类的onDestoryView()执行unbinder.unbind();
 * data 2019/7/8
 */
public abstract class BaseFragmentFromType<T> extends Fragment {
    protected Activity mActivity;
    public Context context;
    private int cType;//每个页卡的ID
    private View rootView;
    private boolean isLoadData = false;//是否加载过数据

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    public BaseFragmentFromType(int cType) {
        this.cType = cType;
    }

    private Handler handler = new Handler(message -> {
        setDate(cType);
        return false;
    });

    protected BaseFragmentFromType() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (!isLoadData){//如果没有加载过数据
                sendMessage();
            }
        }
    }

    public Context getContext() {
        return this.context;
    }

    public int getcType() {
        return cType;
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }
    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        //do something
        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(initLayout(), container, false);
            initView();
        }else {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    public void isLoading(boolean t){
        if (t){
            if (DialogUtil.getInstance(getActivity()).isShow())return;
            DialogUtil.getInstance(getActivity()).showAn(R.layout.loading_layout,false, view -> {
            });
        }else {
            DialogUtil.getInstance(getActivity()).des();
        }
    }

    /**
     * 请求网络
     */
    public void sendMessage() {
        isLoadData =true;
        Message message = handler.obtainMessage();
        message.sendToTarget();
    }

    /**
     * 初始化布局
     *
     * @return 布局id
     */
    protected abstract int initLayout();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 加载数据
     */
    protected abstract void setDate(int cType);

    /**
     * 保证同一按钮在1秒内只响应一次点击事件
     */
    public abstract class OnSingleClickListener implements View.OnClickListener {
        //两次点击按钮的最小间隔，目前为1000
        private static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime;

        public abstract void onSingleClick(View view);

        @Override
        public void onClick(View v) {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                lastClickTime = curClickTime;
                onSingleClick(v);
            }
        }
    }

    /**
     * 同一按钮在短时间内可重复响应点击事件
     */
    public abstract class OnMultiClickListener implements View.OnClickListener {
        public abstract void onMultiClick(View view);
        @Override
        public void onClick(View v) {
            onMultiClick(v);
        }
    }

    public void goTActivity(final Class T, Map<String,Object> map){
        if (T == null)return;
        Intent intent = new Intent(getContext(),T);
        if (map!=null){
            for (String key : map.keySet()) {
                if (map.get(key) instanceof Boolean){
                    intent.putExtra(key,(boolean)map.get(key));
                }
                if (map.get(key) instanceof String){
                    intent.putExtra(key,(String)map.get(key));
                }
                if (map.get(key) instanceof Integer){
                    intent.putExtra(key,(Integer)map.get(key));
                }
                if (map.get(key) instanceof MainMo){
                    intent.putExtra("list",(MainMo)map.get(key) );
                }
            }
        }
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        onDetach();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }
    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        context = ctx;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    protected T bindView(int resId){
        return (T) rootView.findViewById(resId);
    }
}