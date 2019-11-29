package com.dabangvr.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.activity.RechargeDetailedActivity;
import com.dabangvr.comment.activity.StrategyActivity;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapterPosition;
import com.dabangvr.comment.PayDialog;
import com.dabangvr.wxapi.WXPlayCallBack;
import com.dbvr.baselibrary.model.CzMo;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 跳币中心
 */
public class UserDropActivity extends BaseActivity {

    @BindView(R.id.recycle_cz)
    RecyclerView recyclerView;
    private RecyclerAdapterPosition adapter;
    private List<CzMo>mData;

    @BindView(R.id.tvDropNum)
    TextView tvDropNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }
    private UserMess userMess;
    @Override
    public int setLayout() {
        return R.layout.activity_my_drop;
    }

    @Override
    public void initView() {
        userMess = SPUtils.instance(getContext()).getUser();
        tvDropNum.setText("可用跳币："+userMess.getDiamond());
    }

    protected int price;
    private int num;
    @Override
    public void initData() {
        mData = CzMo.getData();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter = new RecyclerAdapterPosition<CzMo>(getContext(),mData,R.layout.item_cz) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, CzMo o) {
                TextView tvDropNum = holder.getView(R.id.tvDropNum);
                TextView tvPrice = holder.getView(R.id.tvPrice);
                tvDropNum.setText(o.getDropNum()+"跳币");
                tvPrice.setText(o.getMoney()+".00元");

                if (o.isYou()){//是否优惠
                    holder.getView(R.id.ivYou).setVisibility(View.VISIBLE);
                }else {
                    holder.getView(R.id.ivYou).setVisibility(View.INVISIBLE);
                }
                if (o.isCheck()){
                    price = o.getMoney();
                    num = o.getDropNum();
                    holder.getView(R.id.llSun).setBackgroundResource(R.drawable.shape_db_stroke);
                    holder.getView(R.id.ivCheck).setVisibility(View.VISIBLE);
                    tvDropNum.setTextColor(getResources().getColor(R.color.colorDb5));
                    tvPrice.setTextColor(getResources().getColor(R.color.colorDb5));
                }else {
                    holder.getView(R.id.ivCheck).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.llSun).setBackgroundResource(R.drawable.shape_gra_stroke);
                    tvDropNum.setTextColor(getResources().getColor(R.color.textTitle));
                    tvPrice.setTextColor(getResources().getColor(R.color.default_bg));
                }
            }
        };
        adapter.setOnItemClickListener((view, position) -> {
            for (int i = 0; i < mData.size(); i++) {
                if (i == position){
                    mData.get(i).setCheck(true);
                }else {
                    mData.get(i).setCheck(false);
                }
            }
            adapter.updateDataa(mData);
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishRes();
    }

    private void finishRes() {
        Intent i = new Intent();
        i.putExtra("diamond",userMess.getDiamond());
        setResult(99, i);
        finish();
        AppManager.getAppManager().finishActivity(this);
    }

    @OnClick({R.id.tvDetailed,R.id.ivBack,R.id.tvConfirm,R.id.tvSeeServer})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tvDetailed:
                goTActivity(RechargeDetailedActivity.class,null);
                break;
            case R.id.tvConfirm:
                paymentDialog();
                break;
            case R.id.tvSeeServer:
                goTActivity(StrategyActivity.class,null);
                break;
                default:break;
        }
    }
    /**
     * 支付弹窗
     */
    private void paymentDialog() {
        PayDialog payDialog = new PayDialog(getContext());
        payDialog.setWxPlayCallBack(new WXPlayCallBack() {
            @Override
            public void success() {
                DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, view -> {
                    TextView tvShow = view.findViewById(R.id.tvShow);
                    tvShow.setVisibility(View.VISIBLE);

                    TextView tvTitle = view.findViewById(R.id.tv_title);
                    tvTitle.setText("充值成功");

                    view.findViewById(R.id.tvCancel).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.tvConfirm).setOnClickListener((view1)->{
                        DialogUtil.getInstance(getContext()).des();
                    });
                });

                userMess.setDiamond(userMess.getDiamond()+num);
                tvDropNum.setText(String.valueOf(userMess.getDiamond()));
                Log.e("testttt",""+userMess.getDiamond());
                SPUtils.instance(getContext()).putUser(userMess);
            }

            @Override
            public void error(String errorMessage) {
                DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, view -> {
                    TextView tvTitle = view.findViewById(R.id.tv_title);
                    tvTitle.setText("充值系统正在维护中，不便之处请谅解");
                    view.findViewById(R.id.tvCancel).setOnClickListener((view1)->{
                        DialogUtil.getInstance(getContext()).des();
                    });
                    view.findViewById(R.id.tvConfirm).setOnClickListener((view2)->{
                        DialogUtil.getInstance(getContext()).des();
                    });
                });
            }

            @Override
            public void cancel() {

            }
        });
        payDialog.showDialog(price);
    }
}
