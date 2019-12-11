package com.dbvr.baselibrary.view;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.dbvr.baselibrary.ui.LoadingUtils;
import com.dbvr.baselibrary.utils.SPUtils;

import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *      BaseFragment 新加
 *      2019、6、24
 */

public abstract class BaseFragment extends Fragment {

    private View rootView;
    protected Unbinder unbinder;

    public View getRootView() {
        return rootView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(layoutId(), container, false);
        }else {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return  rootView;
    }

    private LoadingUtils mLoaddingUtils;

    public void setLoaddingView(boolean isLoadding){
        if(mLoaddingUtils==null){
            mLoaddingUtils=new LoadingUtils(BaseFragment.this.getContext());
        }
        if(isLoadding){
            mLoaddingUtils.show();
        }else{
            mLoaddingUtils.dismiss();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public abstract int layoutId();

    public abstract void initView();

    public abstract void  initData();

    public Context getContext(){
        return rootView.getContext();
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

    public void goTActivityForResult(final Class T, Map<String,Object> map,int requestCode){
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
            }
        }
        startActivityForResult(intent,requestCode);
    }
    public String getToken(){
        return (String) SPUtils.instance(BaseFragment.this.getContext()).getkey("token","");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
