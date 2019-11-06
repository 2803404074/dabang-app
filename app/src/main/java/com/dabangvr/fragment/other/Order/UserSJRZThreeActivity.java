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
import android.widget.TextView;

import com.addressselection.bean.City;
import com.addressselection.bean.County;
import com.addressselection.bean.Province;
import com.addressselection.bean.Street;
import com.addressselection.widge.AddressSelector;
import com.addressselection.widge.BottomDialog;
import com.addressselection.widge.OnAddressSelectedListener;
import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.application.MyApplication;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.model.DepVo;
import com.dbvr.baselibrary.model.QiniuUploadFile;
import com.dbvr.baselibrary.utils.OnUploadListener;
import com.dbvr.baselibrary.utils.QiniuUploadManager;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.dbvr.imglibrary2.model.Image;
import com.dbvr.imglibrary2.ui.SelectImageActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商家入驻申请第三步商户信息录入
 */
public class UserSJRZThreeActivity extends BaseActivity implements OnAddressSelectedListener, AddressSelector.OnDialogCloseListener, AddressSelector.onSelectorAreaPositionListener {


    @BindView(R.id.etname)
    EditText etname;
    @BindView(R.id.et_project)
    EditText et_project;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.tv_address)
    TextView tv_address;

    @BindView(R.id.et_introduce)
    EditText et_introduce;
    @BindView(R.id.ig_id_front)
    ImageView ig_id_front;

    private static final int PERMISSION_REQUEST_CODE = 0;

    private static final int SELECT_IMAGE_REQUEST_four = 0x0014;

    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private long lastonclickTime = 0;
    private DepVo depVo;
    private BottomDialog dialog;

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
        depVo = (DepVo) getIntent().getSerializableExtra(ParameterContens.depVo);
        if (depVo != null) {
            etname.setText(depVo.getName());
            et_project.setText(depVo.getMainCategory());
            tv_address.setText(depVo.getProductionProvince() + depVo.getProductionCity() + depVo.getProductionCounty());
            et_address.setText(depVo.getProductionAddress());
            et_introduce.setText(depVo.getZipCode());
            Glide.with(this).load(depVo.getThreeCertificates() == null ? R.mipmap.icon_id : depVo.getThreeCertificates()).into(ig_id_front);
        } else {
            depVo = new DepVo();
        }
    }


    @OnClick({R.id.ivBack, R.id.tv_next, R.id.ll_id_front, R.id.rl_address})
    public void onclick(View view) {
        long time = SystemClock.uptimeMillis();//防止多次响应
        if (time - lastonclickTime >= ParameterContens.clickTime) {
            lastonclickTime = time;
        } else {
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
                String tv_addressStr = tv_address.getText().toString().trim();
                String et_introduceStr = et_introduce.getText().toString().trim();
                String threeCertificates = depVo.getThreeCertificates();

                if (StringUtils.isMobileNO(etnameStr) || TextUtils.isEmpty(et_addressStr)) {
                    ToastUtil.showShort(this, "请输入商户称");
                    return;
                }
                if (TextUtils.isEmpty(et_projectStr)) {
                    ToastUtil.showShort(this, "主营项目");
                    return;
                }
                if (TextUtils.isEmpty(tv_addressStr)) {
                    ToastUtil.showShort(this, "请选择店铺区域");
                    return;
                }
                if (TextUtils.isEmpty(et_addressStr)) {
                    ToastUtil.showShort(this, "详细地址");
                    return;
                }
                if (TextUtils.isEmpty(threeCertificates)) {
                    ToastUtil.showShort(this, "请上传营业执照");
                    return;
                }
                depVo.setName(etnameStr);
                depVo.setMainCategory(et_projectStr);
                depVo.setProductionAddress(et_addressStr);
                depVo.setSynopsis(et_introduceStr);
                UserApplyUpload();

                break;
            case R.id.ll_id_front: //
                selectImage(SELECT_IMAGE_REQUEST_four);
                break;
            case R.id.rl_address: //
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

    private void UserApplyUpload() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", depVo.getName());
        map.put("mainCategory", depVo.getMainCategory());
        map.put("username", depVo.getUsername());
        map.put("phone", depVo.getPhone());
        map.put("idcard", depVo.getIdcard());
        map.put("productionProvince", depVo.getProductionProvince());
        map.put("productionCity", depVo.getProductionCity());
        map.put("productionCounty", depVo.getProductionCounty());
        map.put("productionAddress", depVo.getProductionAddress());
        map.put("idcartFacial", depVo.getIdcartFacial());
        map.put("idcartBehind", depVo.getIdcartBehind());
        map.put("threeCertificates", depVo.getThreeCertificates());
        map.put("synopsis", depVo.getSynopsis());
        map.put("agreedAgreement", 1);
        map.put("deptId", depVo.getDeptId());

        OkHttp3Utils.getInstance(getContext()).doPostJson(UserUrl.addDept, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                Intent intent = new Intent(UserSJRZThreeActivity.this, UserApplySuccessActivity.class);
                intent.putExtra("name", "商家入驻");
                startActivity(intent);
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(), msg);
            }
        });

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
            setLoaddingView(true);
            try {
                if (requestCode == SELECT_IMAGE_REQUEST_four && data != null) {
                    ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                    Glide.with(this).load(selectImages.get(0).getPath()).into(ig_id_front);
                    new Thread(() -> {
                        getQiniuToken(selectImages.get(0).getPath(), ParameterContens.threeCertificates);
                    }).start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取七牛token
     *
     * @param path
     */
    private void getQiniuToken(String path, String type) {
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getUploadConfigToken, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                QiniuUploadFile qiniuUploadFile = new Gson().fromJson(result, QiniuUploadFile.class);
                upLoadImg(qiniuUploadFile, path, type);//上传图片
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
            }
        });
    }

    /**
     * 上传营业执照
     *
     * @param qiniuUploadFile
     * @param pathImg
     * @param type
     */
    private void upLoadImg(QiniuUploadFile qiniuUploadFile, String pathImg, String type) {

        QiniuUploadFile qiniuUpload = new QiniuUploadFile(pathImg, type + "-" + UUID.randomUUID(), qiniuUploadFile.getMimeType(), qiniuUploadFile.getUpLoadToken());
        QiniuUploadManager.getInstance(MyApplication.getInstance()).upload(qiniuUpload, new OnUploadListener() {
            @Override
            public void onStartUpload() {

            }

            @Override
            public void onUploadProgress(String key, double percent) {

            }

            @Override
            public void onUploadFailed(String key, String err) {
                setLoaddingView(false);
            }

            @Override
            public void onUploadBlockComplete(String path) {
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith(ParameterContens.idcartFacial)) {
                        depVo.setIdcartFacial(DyUrl.QINIUDOMAN + path);
                    } else if (path.startsWith(ParameterContens.idcartBehind)) {
                        depVo.setIdcartBehind(DyUrl.QINIUDOMAN + path);
                    } else if (path.startsWith(ParameterContens.threeCertificates)) {
                        depVo.setThreeCertificates(DyUrl.QINIUDOMAN + path);
                    }
                }
            }

            @Override
            public void onUploadCompleted() {
                setLoaddingView(false);
            }

            @Override
            public void onUploadCancel() {
                setLoaddingView(false);
            }
        });
    }

    @Override
    public void onAddressSelected(Province province, City city, County county, Street street) {
        String s = (province == null ? "" : province.name) + (city == null ? "" : city.name) + (county == null ? "" : county.name) +
                (street == null ? "" : street.name);
        depVo.setProductionProvince(province == null ? "" : province.name);
        depVo.setProductionCity(city == null ? "" : city.name);
        depVo.setProductionCounty(county == null ? "" : county.name);
        tv_address.setText(s);
    }

    @Override
    public void dialogclose() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void selectorAreaPosition(int provincePosition, int cityPosition, int countyPosition, int streetPosition) {

    }
}
