package com.dbvr.baselibrary.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.dbvr.baselibrary.R;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.UserHolper;

import java.util.List;
import java.util.Map;

/**
 *      BaseFragment 新加
 *      2019、6、24
 */

public abstract class BaseFragmentBinding<DB extends ViewDataBinding> extends Fragment {
    public DB mBinding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mBinding == null){
            mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false);
            initView(mBinding);
            initData();
        }else {
            ViewGroup parent = (ViewGroup) mBinding.getRoot().getParent();
            if (parent != null) {
                parent.removeView(mBinding.getRoot());
            }
        }
        return  mBinding.getRoot();
    }

    public void isLoading(boolean t){
        if (t){
            DialogUtil.getInstance(getActivity()).showAn(R.layout.loading_layout,false, view -> {});
        }else {DialogUtil.getInstance(getActivity()).des(); }
    }

    public abstract int layoutId();

    public abstract void initView(DB db);

    public abstract void  initData();

    public String getToken(){
        return UserHolper.getUserHolper(getContext()).getToken();
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

}
