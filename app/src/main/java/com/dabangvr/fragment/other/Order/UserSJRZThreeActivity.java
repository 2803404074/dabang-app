package com.dabangvr.fragment.other.Order;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.imglibrary2.model.Image;
import com.dbvr.imglibrary2.ui.SelectImageActivity;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商家入驻申请第三步商户信息录入
 */
public class UserSJRZThreeActivity extends BaseActivity {


    @BindView(R.id.etname)
    EditText etname;
    @BindView(R.id.et_project)
    EditText et_project;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.et_introduce)
    EditText et_introduce;
    @BindView(R.id.ig_id_front)
    ImageView ig_id_front;

    private static final int PERMISSION_REQUEST_CODE = 0;

    private static final int SELECT_IMAGE_REQUEST_four = 0x0014;

    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private long lastonclickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_sjrz_three;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.ivBack, R.id.tv_next, R.id.ll_id_front})
    public void onclick(View view) {
        long time = SystemClock.uptimeMillis();//防止多次响应
        if (time - lastonclickTime >= ParameterContens.clickTime) {
            lastonclickTime = time;
        }else {
            return;
        }
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;

            case R.id.tv_next:
                String etnameStr = etname.getText().toString().trim();
                String et_projectStr = et_project.getText().toString().trim();
                String et_addressStr = et_address.getText().toString().trim();
                String et_introduceStr = et_introduce.getText().toString().trim();
                if (StringUtils.isMobileNO(etnameStr) || TextUtils.isEmpty(et_projectStr) || TextUtils.isEmpty(et_addressStr)) {
                    ToastUtil.showShort(this, "请输入商户称和主营项目以及详细地址");
                    return;
                }
                Intent intent = new Intent(this, UserApplySuccessActivity.class);
                intent.putExtra("name", "商家入驻");
                startActivity(intent);
                break;
            case R.id.ll_id_front: //正面
                selectImage(SELECT_IMAGE_REQUEST_four);
                break;

        }
    }

    private void selectImage(int code) {
        int isPermission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int isPermission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (isPermission1 == PackageManager.PERMISSION_GRANTED && isPermission2 == PackageManager.PERMISSION_GRANTED) {
            startActivity(code);
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void startActivity(int code) {
        Intent intent = new Intent(this, SelectImageActivity.class);
        intent.putExtra("size", 1);
        intent.putParcelableArrayListExtra("selected_images", mSelectImages);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            try {
                if (requestCode == SELECT_IMAGE_REQUEST_four && data != null) {
                    ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                    Glide.with(this).load(selectImages.get(0).getPath()).into(ig_id_front);
                    //
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
