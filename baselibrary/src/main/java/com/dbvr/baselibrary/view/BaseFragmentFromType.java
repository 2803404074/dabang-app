package com.dbvr.baselibrary.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dbvr.baselibrary.R;
import com.dbvr.baselibrary.ui.LoadingUtils;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * fragment基类
 * 针对 展示多类型商品的页面
 * 懒加载
 * auth 黄仕豪
 * data 2019/7/8
 */
public abstract class BaseFragmentFromType extends Fragment {
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    private Unbinder unbinder;
    //获取TAG的fragment名称
    protected final String TAG = this.getClass().getSimpleName();
    protected String STATE_SAVE_IS_HIDDEN ;
    public Context context;

    private boolean IS_LOADED = false;
    public static int mSerial = 0;
    private int mTabPos = 0;//第几个类型
    private int cType;//类型id
    private boolean isFirst = true;

    public BaseFragmentFromType(int cType) {
        this.cType = cType;
    }

    public int getcType() {
        return cType;
    }

    public Context getContext(){
        return this.context;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (!IS_LOADED) {
                IS_LOADED = true;
                //这里执行加载数据的操作
                setDate();
            }
            return false;
        }
    });

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

    private View rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(initLayout(), container, false);
        }else {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        unbinder = ButterKnife.bind(this, rootView);
        if (isFirst && mTabPos == mSerial) {
            isFirst = false;
            sendMessage();
        }
        initView();

        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN,isHidden());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){

        }
    }
    /**
     * 请求网络
     */
    public void sendMessage() {
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
    protected abstract void setDate();

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

    private LinearLayout linearLayout;

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }

    public void setLoaddingView(boolean isLoadding){
        if (isLoadding){
            if (linearLayout!=null){
                linearLayout.setVisibility(View.VISIBLE);
            }

        }else {
            if (linearLayout!=null){
                linearLayout.setVisibility(View.GONE);
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
                if (map.get(key) instanceof List){

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
}