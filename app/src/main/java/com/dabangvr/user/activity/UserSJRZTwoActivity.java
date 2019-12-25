package com.dabangvr.user.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.util.OpenCameUtil;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.model.DepVo;
import com.dbvr.baselibrary.model.DepVoRz;
import com.dbvr.baselibrary.model.QiniuUploadFile;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.OnUploadListener;
import com.dbvr.baselibrary.utils.QiniuUploadManager;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.dbvr.imglibrary2.model.Image;
import com.dbvr.imglibrary2.ui.SelectImageActivity;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dabangvr.util.OpenCameUtil.REQ_1;
import static com.dabangvr.util.OpenCameUtil.REQ_2;
import static com.dabangvr.util.OpenCameUtil.REQ_4;
import static com.dabangvr.util.OpenCameUtil.imageUri;

/**
 * 商家入驻申请第二步个人身份验证
 */
public class UserSJRZTwoActivity extends BaseActivity {


    @BindView(R.id.etname)
    EditText etname;
    @BindView(R.id.etname_code)
    EditText etname_code;
    @BindView(R.id.ig_id_front)
    ImageView ig_id_front;
    @BindView(R.id.ig_id_back)
    ImageView ig_id_back;

    private File fileFront;
    private File fileBack;
    private long lastonclickTime = 0;
    private DepVoRz depVo;

    private int index;//区别正面或者反面的身份证上传

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_sjrz_two;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        depVo = (DepVoRz) getIntent().getSerializableExtra(ParameterContens.depVo);
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
                if (TextUtils.isEmpty(etnameStr) || TextUtils.isEmpty(etname_codeStr)) {
                    ToastUtil.showShort(this, "请输入姓名和身份证号");
                    return;
                }
                if (fileFront == null || fileBack == null) {
                    ToastUtil.showShort(this, "请补全身份证正反面");
                    return;
                }

                depVo.setUsername(etnameStr);
                depVo.setIdcard(etname_codeStr);
                depVo.setFileFont(fileFront);
                depVo.setFileBack(fileBack);

                Intent intent = new Intent(getContext(), UserSJRZThreeActivity.class);
                intent.putExtra(ParameterContens.depVo, depVo);
                startActivity(intent);
                break;
            case R.id.ll_id_front: //正面
                selectPicture(0);
                break;
            case R.id.ll_id_back://反面
                selectPicture(1);
                break;
        }
    }

    /**
     * @param index 0正面，1反面
     */
    private void selectPicture(int index) {
        this.index = index;

        BottomDialogUtil2.getInstance(this).showLive(R.layout.dialog_came, view1 -> {
            view1.findViewById(R.id.takePhoto).setOnClickListener(view2 -> {
                if (index == 0) {
                    OpenCameUtil.init("zheng");
                } else {
                    OpenCameUtil.init("fan");
                }
                OpenCameUtil.openCamera(this);
                BottomDialogUtil2.getInstance(this).dess();
            });
            view1.findViewById(R.id.choosePhoto).setOnClickListener(view2 -> {
                if (index == 0) {
                    OpenCameUtil.init("zheng");
                } else {
                    OpenCameUtil.init("fan");
                }
                OpenCameUtil.openAlbum(this);
                BottomDialogUtil2.getInstance(this).dess();
            });
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_1: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    if (index == 0) {
                        OpenCameUtil.startPhotoZoom(UserSJRZTwoActivity.this, imageUri, 7, 5);
                    } else {
                        OpenCameUtil.startPhotoZoom(UserSJRZTwoActivity.this, imageUri, 7, 5);
                    }
                }
                break;
            }
            case REQ_2: {
                if (resultCode == RESULT_OK) {
                    if (index == 0) {
                        OpenCameUtil.startPhotoZoom(UserSJRZTwoActivity.this, data.getData(), 7, 5);
                    } else {
                        OpenCameUtil.startPhotoZoom(UserSJRZTwoActivity.this, data.getData(), 7, 5);
                    }

                }
                break;
            }
            case REQ_4: {
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmapCamera = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(OpenCameUtil.imageUris));
                        if (bitmapCamera != null) {
                            try {
                                if (index == 0) {
                                    ig_id_front.setImageBitmap(bitmapCamera);
                                    fileFront = new File(new URI(OpenCameUtil.imageUris.toString()));
                                } else {
                                    ig_id_back.setImageBitmap(bitmapCamera);
                                    fileBack = new File(new URI(OpenCameUtil.imageUris.toString()));
                                }
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
