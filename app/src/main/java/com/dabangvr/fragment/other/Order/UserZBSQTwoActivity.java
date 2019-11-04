package com.dabangvr.fragment.other.Order;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.dbvr.imglibrary2.model.Image;
import com.dbvr.imglibrary2.ui.SelectImageActivity;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商家入驻申请第二步个人身份验证
 */
public class UserZBSQTwoActivity extends BaseActivity {


    @BindView(R.id.etname)
    EditText etname;
    @BindView(R.id.etname_code)
    EditText etname_code;
    @BindView(R.id.ig_id_front)
    ImageView ig_id_front;
    @BindView(R.id.ig_id_back)
    ImageView ig_id_back;
    private static final int PERMISSION_REQUEST_CODE = 0;

    private static final int SELECT_IMAGE_REQUEST_one = 0x0012;
    private static final int SELECT_IMAGE_REQUEST_two = 0x0013;
    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private long lastonclickTime = 0;
    private DepVo depVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_zbsq_two;
    }

    @Override
    public void initView() {
        depVo = (DepVo) getIntent().getSerializableExtra(ParameterContens.depVo);
        if (depVo != null) {
            etname.setText(depVo.getUsername());
            etname_code.setText(depVo.getIdcard());
            Glide.with(this).load(depVo.getIdcartFacial() == null ? R.mipmap.icon_id : depVo.getIdcartFacial()).into(ig_id_front);
            Glide.with(this).load(depVo.getIdcartBehind() == null ? R.mipmap.icon_id : depVo.getIdcartBehind()).into(ig_id_back);
        }
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack, R.id.tv_next, R.id.ll_id_front, R.id.ll_id_back})
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
                String etname_codeStr = etname_code.getText().toString().trim();
                if (StringUtils.isMobileNO(etnameStr) || TextUtils.isEmpty(etname_codeStr)) {
                    ToastUtil.showShort(this, "请输入姓名和身份证号");
                    return;
                }
                depVo.setUsername(etnameStr);
                depVo.setIdcard(etname_codeStr);
                UserApplyUpload();


                break;
            case R.id.ll_id_front: //正面
                selectImage(SELECT_IMAGE_REQUEST_one);
                break;
            case R.id.ll_id_back://反面
                selectImage(SELECT_IMAGE_REQUEST_two);
                break;
        }
    }

    private void UserApplyUpload() {
        Intent intent = new Intent(this, UserApplySuccessActivity.class);
        intent.putExtra("name", "主播申请");
        startActivity(intent);
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
                if (requestCode == SELECT_IMAGE_REQUEST_one && data != null) {
                    ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                    Glide.with(this).load(selectImages.get(0).getPath()).into(ig_id_front);
                    //

                    new Thread(() -> {

                        getQiniuToken(selectImages.get(0).getPath(), ParameterContens.idcartFacial);
                    }).start();
                }
                if (requestCode == SELECT_IMAGE_REQUEST_two && data != null) {
                    ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                    Glide.with(this).load(selectImages.get(0).getPath()).into(ig_id_back);

                    new Thread(() -> {

                        getQiniuToken(selectImages.get(0).getPath(), ParameterContens.idcartBehind);
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
                Log.d("luhuas", "onUploadBlockComplete: " + path);
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith(ParameterContens.idcartFacial)) {
                        depVo.setIdcartFacial(DyUrl.QINIUDOMAN + path);
                    } else if (path.startsWith(ParameterContens.idcartBehind)) {
                        depVo.setIdcartBehind(DyUrl.QINIUDOMAN + path);
                    }
                }
            }

            @Override
            public void onUploadCompleted() {
                Log.d("luhuas", "onUploadCompleted:sha'上传完成 ");
                setLoaddingView(false);
            }

            @Override
            public void onUploadCancel() {
                setLoaddingView(false);
            }
        });
    }
}
