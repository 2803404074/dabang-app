package com.dabangvr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.addressselection.bean.City;
import com.addressselection.bean.County;
import com.addressselection.bean.Province;
import com.addressselection.bean.Street;
import com.addressselection.widge.AddressSelector;
import com.addressselection.widge.BottomDialog;
import com.addressselection.widge.OnAddressSelectedListener;
import com.dabangvr.R;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户编辑信息
 */
public class AddressActivity extends BaseActivity implements OnAddressSelectedListener, AddressSelector.OnDialogCloseListener, AddressSelector.onSelectorAreaPositionListener {

    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.tv_selector_area)
    TextView tv_selector_area;
    private AddressSelector selector;
    private BottomDialog dialog;
    public static final String EXTRA_RESULT = "EXTRA_RESULT";
    private String addressStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_address;
    }

    @Override
    public void initView() {
        selector = new AddressSelector(this);
        selector.setOnAddressSelectedListener(new OnAddressSelectedListener() {
            @Override
            public void onAddressSelected(Province province, City city, County county, Street street) {
//                Log.d("luhuas", "selector省份id=" + province.name);
//                Log.d("luhuas", "selector城市id=" + city.name);
//                Log.d("luhuas", "selector乡镇id=" + county.name);

                addressStr = (province == null ? "" : province.name) + (city == null ? "" : city.name) + (county == null ? "" : county.name) +
                        (street == null ? "" : street.name);
                tv_selector_area.setText(addressStr);
                resultActivity();
            }
        });
        View view = selector.getView();
        content.addView(view);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack,R.id.tv_selector_area})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tv_selector_area:

                if (dialog != null) {
                    dialog.show();
                } else {
                    dialog = new BottomDialog(this);
                    dialog.setOnAddressSelectedListener(this);
                    dialog.setDialogDismisListener(this);
                    dialog.setTextSize(14);//设置字体的大小
                    dialog.setIndicatorBackgroundColor(android.R.color.holo_orange_light);//设置指示器的颜色
                    dialog.setTextSelectedColor(android.R.color.holo_orange_light);//设置字体获得焦点的颜色
                    dialog.setTextUnSelectedColor(android.R.color.holo_blue_light);//设置字体没有获得焦点的颜色
//            dialog.setDisplaySelectorArea("31",1,"2704",1,"2711",0,"15582",1);//设置已选中的地区
                    dialog.setSelectorAreaPositionListener(this);
                    dialog.show();
                }

                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void resultActivity(){
        Intent intent = new Intent();
        if (TextUtils.isEmpty(addressStr)){
            return;
        }
        intent.putExtra("address", addressStr);
        setResult(RESULT_OK, intent);
    }


    @Override
    public void onAddressSelected(Province province, City city, County county, Street street) {

        String s = (province == null ? "" : province.name) + (city == null ? "" : city.name) + (county == null ? "" : county.name) +
                (street == null ? "" : street.name);
        tv_selector_area.setText(s);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void dialogclose() {
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    @Override
    public void selectorAreaPosition(int provincePosition, int cityPosition, int countyPosition, int streetPosition) {

    }
}
