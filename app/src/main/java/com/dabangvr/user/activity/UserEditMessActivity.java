package com.dabangvr.user.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dabangvr.R;
import com.dabangvr.databinding.ActivityUserMessModifyBinding;
import com.dabangvr.util.OpenCameUtil;
import com.dbvr.baselibrary.model.QiniuUploadFile;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.OnUploadListener;
import com.dbvr.baselibrary.utils.QiniuUploadManager;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivityBinding;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.dabangvr.util.OpenCameUtil.REQ_1;
import static com.dabangvr.util.OpenCameUtil.REQ_2;
import static com.dabangvr.util.OpenCameUtil.REQ_4;
import static com.dabangvr.util.OpenCameUtil.imageUri;

/**
 * 用户编辑信息
 */
public class UserEditMessActivity extends BaseActivityBinding<ActivityUserMessModifyBinding> implements View.OnClickListener {
    private static final int SELECT_IMAGE_REQUEST_statement = 1000;
    private UserMess userMess;

    @Override
    public int setLayout() {
        return R.layout.activity_user_mess_modify;
    }

    @Override
    protected void onResume() {
        super.onResume();
        userMess = UserHolper.getUserHolper(getContext()).getUserMess();
        mBinding.setUser(userMess);

        mBinding.sdvHead.setImageURI(userMess.getHeadUrl());

        mBinding.tvNickName.setText(userMess.getNickName());
        mBinding.tvUserId.setText(String.valueOf(userMess.getId()));

        mBinding.tvSex.setText(StringUtils.isEmpty(userMess.getSex()) ? "未知" : userMess.getSex());
        mBinding.tvDate.setText(StringUtils.isEmpty(userMess.getBirthday()) ? "未设置生日" : userMess.getBirthday());
        mBinding.tvIntroduce.setText(StringUtils.isEmpty(userMess.getAutograph()) ? "设置签名更吸引粉丝哦~" : userMess.getAutograph());
        mBinding.tvPhone.setText(StringUtils.isEmpty(userMess.getMobile()) ? " 未绑定手机" : userMess.getMobile());
    }

    @Override
    public void initView() {


        mBinding.tvNickName.setOnClickListener((view)->{
            DialogUtil.getInstance(this).show(R.layout.dialog_tip_input, view1 -> {
                EditText etInput = view1.findViewById(R.id.et_title);
                etInput.setText(userMess.getNickName());
                view1.findViewById(R.id.tvConfirm).setOnClickListener(view2 -> {
                    if (etInput.getText().toString().equals(userMess.getNickName()))return;
                    mBinding.tvNickName.setText(etInput.getText().toString());
                    updataMess("nickName", etInput.getText().toString());
                    DialogUtil.getInstance(this).des();
                });
                view1.findViewById(R.id.tvCancel).setOnClickListener((view2)->{
                    DialogUtil.getInstance(this).des();
                });
            });
        });

        mBinding.sdvHead.setOnClickListener(this);
        mBinding.llSex.setOnClickListener(this);
        mBinding.llDate.setOnClickListener(this);
        mBinding.llIntroduce.setOnClickListener(this);
        mBinding.llLocation.setOnClickListener(this);
        mBinding.llPhone.setOnClickListener(this);
        mBinding.ivBack.setOnClickListener(this);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sdvHead:
                BottomDialogUtil2.getInstance(this).showLive(R.layout.dialog_came, view1 -> {
                    view1.findViewById(R.id.takePhoto).setOnClickListener(view2 -> {
                        OpenCameUtil.init("head");
                        OpenCameUtil.openCamera(this);
                        BottomDialogUtil2.getInstance(this).dess();
                    });
                    view1.findViewById(R.id.choosePhoto).setOnClickListener(view2 -> {
                        OpenCameUtil.init("head");
                        OpenCameUtil.openAlbum(this);
                        BottomDialogUtil2.getInstance(this).dess();
                    });
                });
                break;
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.llSex://性别
                showSexDialog();
                break;
            case R.id.llDate://日期选择
                showDateDialog();
                break;
            case R.id.llLocation:
                goTActivity(UserAddressActivity.class, null);
                break;
            case R.id.llIntroduce:
                goTActivityForResult(UserIntroduceActivity.class, null, SELECT_IMAGE_REQUEST_statement);
                break;
            case R.id.llPhone:
                goTActivity(PhoneSetActivity.class, null);
                break;
            default:
                break;
        }
    }

    private void getQiNiuToken() {
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.getUploadConfigToken, null, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                QiniuUploadFile qiniuUploadFile = new Gson().fromJson(result, QiniuUploadFile.class);
                upLoadHead(qiniuUploadFile);//这里是七牛的token
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    private void upLoadHead(QiniuUploadFile qiniuUploadFile) {
        qiniuUploadFile.setFile(file);
        qiniuUploadFile.setKey("user-head" + UUID.randomUUID());
        QiniuUploadManager.getInstance(this).upload(qiniuUploadFile, new OnUploadListener() {
            @Override
            public void onStartUpload() {
            }

            @Override
            public void onUploadProgress(String key, double percent) {
            }

            @Override
            public void onUploadFailed(String key, String err) {
                ToastUtil.showShort(getContext(), "封面上传失败,请检查您的网络" + err);
            }

            @Override
            public void onUploadBlockComplete(String key) {
                //上传成功，将信息发送给后端
                updataMess("headUrl", key);

            }

            @Override
            public void onUploadCompleted() {
            }

            @Override
            public void onUploadCancel() {
            }
        });
    }

    /**
     * @param key    1,2,3,4,5,6,7,8,9,10
     * @param values
     */
    private void updataMess(String key, String values) {
        UserHolper.getUserHolper(getContext()).upUser(key, values);
        Map<String,Object>map = new HashMap<>();
        map.put(key,values);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.update, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException { }
            @Override
            public void onFailed(String msg) { }
        });
    }


    private void showSexDialog() {
        BottomDialogUtil2.getInstance(this).show(R.layout.dialog_sex, 0, view -> {
            TextView tvName = view.findViewById(R.id.tv_nan);
            TextView tv_nv = view.findViewById(R.id.tv_nv);
            TextView tvConfirm = view.findViewById(R.id.tvConfirm);
            tvConfirm.setVisibility(View.GONE);
            tvName.setOnClickListener(v -> {
                mBinding.tvSex.setText(tvName.getText().toString());
                updataMess("sex", "男");
                BottomDialogUtil2.getInstance(UserEditMessActivity.this).dess();
            });
            tv_nv.setOnClickListener(v -> {
                mBinding.tvSex.setText(tv_nv.getText().toString());
                updataMess("sex", "女");
                BottomDialogUtil2.getInstance(UserEditMessActivity.this).dess();
            });
        });
    }

    private void showDateDialog() {
        Calendar ca = Calendar.getInstance();
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH);
        int mDay = ca.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    final String data = (month + 1) + "月-" + dayOfMonth + "日 ";
                    updataMess("birthday", data);
                    mBinding.tvDate.setText(data);
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private File file;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_1: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    OpenCameUtil.startPhotoZoom(UserEditMessActivity.this, imageUri, 5, 5);
                }
                break;
            }
            case REQ_2: {
                if (resultCode == RESULT_OK) {
                    OpenCameUtil.startPhotoZoom(UserEditMessActivity.this, data.getData(), 5, 5);
                }
                break;
            }
            case REQ_4: {
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmapCamera = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(OpenCameUtil.imageUris));
                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmapCamera, null, null));
                        mBinding.sdvHead.setImageURI(uri);
                        if (bitmapCamera != null) {
                            try {
                                file = new File(new URI(OpenCameUtil.imageUris.toString()));
                                getQiNiuToken();
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
