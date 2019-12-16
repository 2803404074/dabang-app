package com.dbvr.baselibrary.view;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.dbvr.baselibrary.R;
import com.dbvr.baselibrary.ui.LoadingUtils;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.UserHolper;

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
    protected boolean isLoadView = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(layoutId(), container, false);
            isLoadView = true;
        }else {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        unbinder = ButterKnife.bind(this, rootView);
        if (isLoadView){
            initView();
            initData();
        }
        isLoadView = false;
        return  rootView;
    }

    public void isLoading(boolean t){
        if (t){
            DialogUtil.getInstance(getActivity()).showAn(R.layout.loading_layout,false, view -> {
            });
        }else {
            DialogUtil.getInstance(getActivity()).des();
        }
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

    public void goTActivityIntent(final Class T, Intent intent){
        if (T == null)return;
        startActivity(intent);
    }

    public String getToken(){
        return UserHolper.getUserHolper(getContext()).getToken();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
