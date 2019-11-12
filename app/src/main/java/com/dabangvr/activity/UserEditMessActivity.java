package com.dabangvr.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.fragment.SameCityFragment;
import com.dabangvr.fragment.other.Order.UserSJRZTwoActivity;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.eventBus.ReadEvent;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.Conver;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户编辑信息
 */
public class UserEditMessActivity extends BaseActivity {

    private static final int SELECT_IMAGE_REQUEST_statement = 1000;
    private static final int SELECT_IMAGE_REQUEST_address = 1100;
    private static final int SELECT_IMAGE_REQUEST_Phone = 1200;
    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;
    @BindView(R.id.tvNickName)
    EditText tvNickName;
    @BindView(R.id.tvUserId)
    TextView tvId;
    @BindView(R.id.tvSex)
    TextView tvSex;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvIntroduce)
    TextView tvIntroduce;
    @BindView(R.id.tvPhone)
    TextView tvPhone;

    private UserMess userMess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        EventBus.getDefault().register(this); //第1步: 注册
        return R.layout.activity_user_mess_modify;
    }

    @Override
    public void initView() {
        userMess = SPUtils.instance(this).getUser();
        sdvHead.setImageURI(userMess.getHeadUrl());
        tvNickName.setText(userMess.getNickName());
        tvDate.setText("----");

        tvId.setText(String.valueOf(userMess.getId()));

        //性别
        if (StringUtils.isEmpty(userMess.getSex())) {
            tvSex.setHint("未设置性别");
        } else {
            tvSex.setText(userMess.getSex());
        }

        //常住地
        if (StringUtils.isEmpty(userMess.getPermanentResidence())) {
            tvLocation.setHint("设置你的常驻地址，有利于吸引周边人气哦~");
        } else {
            tvLocation.setText(userMess.getPermanentResidence());
        }

        //手机号
        if (StringUtils.isEmpty(userMess.getMobile())) {
            tvPhone.setHint("未绑定手机号");
        } else {
            tvPhone.setText(StringUtils.hidTel(userMess.getMobile()));
        }

        //个人说明
        tvIntroduce.setHint(StringUtils.isEmpty(userMess.getAutograph()) ? "添加个人说明，如你的座右铭等" : userMess.getAutograph());
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivBack, R.id.llHead, R.id.llNick, R.id.llSex, R.id.llDate, R.id.llLocation, R.id.llIntroduce, R.id.llPhone, R.id.tv_sub})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.llHead:
                break;
            case R.id.llNick:
                break;
            case R.id.llSex://性别
                showSexDialog();
                break;
            case R.id.llDate://日期选择
                showDateDialog();
                break;
            case R.id.llLocation:
                goTActivityForResult(LocationActivity.class, null, SELECT_IMAGE_REQUEST_address);
                break;
            case R.id.llIntroduce:
                goTActivityForResult(IntroduceActivity.class, null, SELECT_IMAGE_REQUEST_statement);
                break;
            case R.id.llPhone:

                if (TextUtils.isEmpty(userMess.getMobile())) {

                }
                Intent intent = new Intent(this, PhoneSetActivity.class);
                intent.putExtra("mobile", userMess.getMobile());
                startActivity(intent);
                break;
            case R.id.tv_sub: //提交修改资料
                String nickName = tvNickName.getText().toString().trim();
                String userId = tvId.getText().toString().trim();
                String sex = tvSex.getText().toString().trim();
                String date = tvDate.getText().toString().trim();
                String location = tvLocation.getText().toString().trim();
                String introduce = tvIntroduce.getText().toString().trim();
                String phone = tvPhone.getText().toString().trim();

                Map<String, Object> map = new HashMap<>();
                map.put("nickName", nickName);
                map.put("permanentResidence", location);
                map.put("sex", sex);
                map.put("anchorId", userId);
                map.put("autograph", introduce);
                map.put("mobile", phone);
                map.put("id", userMess.getId());
                //修改用户信息
                userMess.setNickName(nickName);
                userMess.setPermanentResidence(location);
                userMess.setSex(sex);
                userMess.setAutograph(introduce);
                userMess.setMobile(phone);
                setLoaddingView(true);
                for (String key : map.keySet()) {
                    Log.d("luhuas", "onclick: key=" + key + "==value=" + map.get(key));
                }
                OkHttp3Utils.getInstance(this).doPostJson(UserUrl.updateUser, map, new ObjectCallback<String>(this) {
                    @Override
                    public void onUi(String result) {
                        ToastUtil.showShort(getContext(), result);
                        setLoaddingView(false);
                        SPUtils.instance(getContext()).putUser(userMess); //提交用户信息
                    }

                    @Override
                    public void onFailed(String msg) {
                        ToastUtil.showShort(getContext(), msg);
                        setLoaddingView(false);

                    }
                });
                break;
        }
    }

    private void showSexDialog() {
        BottomDialogUtil2.getInstance(this).show(R.layout.dialog_sex, 0, new Conver() {
            @Override
            public void setView(View view) {
                TextView tvName = view.findViewById(R.id.tv_nan);
                TextView tv_nv = view.findViewById(R.id.tv_nv);
                TextView tvConfirm = view.findViewById(R.id.tvConfirm);
                tvConfirm.setVisibility(View.GONE);
                tvName.setOnClickListener(v -> {
                    tvSex.setText(tvName.getText().toString());
                    BottomDialogUtil2.getInstance(UserEditMessActivity.this).dess();
                });
                tv_nv.setOnClickListener(v -> {
                    tvSex.setText(tv_nv.getText().toString());
                    BottomDialogUtil2.getInstance(UserEditMessActivity.this).dess();
                });
            }
        });
    }

    private void showDateDialog() {
        Calendar ca = Calendar.getInstance();
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH);
        int mDay = ca.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final String data = (month + 1) + "月-" + dayOfMonth + "日 ";
                        ToastUtil.showShort(getContext(), data);
                    }
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST_statement && data != null) {
                String statement = data.getStringExtra(ParameterContens.statement);
                tvIntroduce.setText(statement);
            }
//            if (requestCode == SELECT_IMAGE_REQUEST_Phone && data != null) {
//                String mobile = data.getStringExtra("mobile");
//                tvPhone.setText(StringUtils.hidTel(mobile));
//            }
        } else if (resultCode == SameCityFragment.cityFragmentCode) {
            if (requestCode == SELECT_IMAGE_REQUEST_address && data != null) {
                String provinceName = data.getStringExtra("mProvince");
                String cityName = data.getStringExtra("cityName");
                tvLocation.setText(provinceName + "-" + cityName == null ? "" : cityName);
            }
        }
    }

    //EventBus主线程接收消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(ReadEvent event) {
        String state = event.getState();
        //如果多个消息，可在实体类中添加type区分消息
        switch (event.getType()) {
            case 1111:
                String info = event.getInfo();
                tvPhone.setText(info);
                break;


        }
    }
}
