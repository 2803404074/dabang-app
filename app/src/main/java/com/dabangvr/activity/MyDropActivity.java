package com.dabangvr.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.ui.PayDialog;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 跳币中心
 */
public class MyDropActivity extends BaseActivity {

    @BindView(R.id.tvVip)
    TextView tvVip;
    @BindView(R.id.tvDrop)
    TextView tvDrop;

    @BindView(R.id.ivClose)
    ImageView ivClose;

    //跳币视图
    @BindView(R.id.llDrop)
    LinearLayout llDrop;

    //vip视图
    @BindView(R.id.llContent)
    LinearLayout llContent;

    @BindView(R.id.etInput)
    EditText etInput;
    @BindView(R.id.tvZdy)
    TextView tvZdy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_my_drop;
    }

    @Override
    public void initView() {
        showUI(false);
        ivClose.setVisibility(View.GONE);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!StringUtils.isEmpty(etInput.getText().toString())){
                    price = Integer.parseInt(etInput.getText().toString())/10;
                }else {
                    price = 0;
                }
                tvZdy.setText("¥"+price);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected int price;
    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack,R.id.tvVip,R.id.tvDrop,R.id.tvZdy,R.id.tvS,R.id.tvEs,R.id.tvWs,R.id.tvYb,R.id.tvEb})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tvVip:
                tvVip.setTextColor(getResources().getColor(R.color.colorAccentButton));
                tvDrop.setTextColor(getResources().getColor(R.color.colorGray4));
                showUI(true);
                break;
            case R.id.tvDrop:
                tvDrop.setTextColor(getResources().getColor(R.color.colorAccentButton));
                tvVip.setTextColor(getResources().getColor(R.color.colorGray4));
                showUI(false);
                break;
            case R.id.tvZdy:
                if (price<1){
                    ToastUtil.showShort(getContext(),"至少充值10个");
                    return;
                }
                paymentDialog(price);
                break;
            case R.id.tvS:
                price = 10;
                paymentDialog(price);
                break;
            case R.id.tvEs:
                price = 20;
                paymentDialog(price);
                break;
            case R.id.tvWs:
                price = 50;
                paymentDialog(price);
                break;
            case R.id.tvYb:
                price = 100;
                paymentDialog(price);
                break;
            case R.id.tvEb:
                price = 200;
                paymentDialog(price);
                break;
                default:break;
        }
    }

    /**
     * 支付弹窗
     */
    private void paymentDialog(final double price) {
        PayDialog payDialog = new PayDialog(getContext());
        payDialog.showDialog(price);
    }

    /**
     * 显示控件
     * @param isVip true 显示充值vip UI
     */
    private void showUI(boolean isVip){
        if (isVip){
           llContent.setVisibility(View.VISIBLE);
           llDrop.setVisibility(View.GONE);
        }else {
            llContent.setVisibility(View.GONE);
            llDrop.setVisibility(View.VISIBLE);
        }
    }
}
