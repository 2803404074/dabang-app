package com.dabangvr.user.activity;

import android.view.View;

import com.dabangvr.R;
import com.dabangvr.databinding.ActivityDepListBinding;
import com.dbvr.baselibrary.model.DepMo;
import com.dbvr.baselibrary.model.DepVo;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.baselibrary.view.BaseActivityBinding;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;

import org.json.JSONException;

public class DepListActivity extends BaseActivityBinding<ActivityDepListBinding> {

    @Override
    public int setLayout() {
        return R.layout.activity_dep_list;
    }

    @Override
    public void initView() {
        isLoading(true);

        mBinding.ivBack.setOnClickListener((view)->{
            AppManager.getAppManager().finishActivity(this);
        });
        mBinding.ivAddDep.setOnClickListener(view -> goTActivity(UserSJRZOneActivity.class,null));


    }

    @Override
    public void initData() {
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getDeptState, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                runOnUiThread(() -> {
                    mBinding.tvTipsS.setVisibility(View.GONE);
                    mBinding.ivAddDep.setVisibility(View.GONE);
                    mBinding.include.llDepCon.setVisibility(View.VISIBLE);
                });
                isLoading(false);
                DepVo depVo = new Gson().fromJson(result, DepVo.class);
                if (depVo!=null){
                    mBinding.setDepVo(depVo);
                    if (depVo.getAuditStatus()==-1){//-1未审核 1审核通过 0 审核不通过
                        mBinding.include.tvStatus.setBackgroundResource(R.drawable.shape_gray);
                    }else if (depVo.getAuditStatus()==-0){
                        mBinding.include.tvStatus.setBackgroundResource(R.drawable.shape_db_search);
                    }else {
                        mBinding.include.tvStatus.setBackgroundResource(R.drawable.shape_red);
                    }
                    mBinding.include.tvStatus.setText(depVo.getAuditDescribe());
                }
            }

            @Override
            public void onFailed(String msg) {
                runOnUiThread(() -> {
                    mBinding.include.llDepCon.setVisibility(View.GONE);
                    mBinding.ivAddDep.setVisibility(View.VISIBLE);
                    mBinding.tvTipsS.setVisibility(View.VISIBLE);
                });
                isLoading(false);
            }
        });
    }
}
