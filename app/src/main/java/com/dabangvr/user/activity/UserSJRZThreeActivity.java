package com.dabangvr.user.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dabangvr.util.OpenCameUtil;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.model.DepVoRz;
import com.dbvr.baselibrary.model.ProvinceMo;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dabangvr.util.OpenCameUtil.REQ_1;
import static com.dabangvr.util.OpenCameUtil.REQ_2;
import static com.dabangvr.util.OpenCameUtil.REQ_4;
import static com.dabangvr.util.OpenCameUtil.imageUri;

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
    @BindView(R.id.tv_address)
    TextView tv_address;

    @BindView(R.id.et_introduce)
    EditText et_introduce;
    @BindView(R.id.ig_id_front)
    ImageView ig_id_front;

    private File file;
    private long lastonclickTime = 0;
    private DepVoRz depVo;

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
        depVo = (DepVoRz) getIntent().getSerializableExtra(ParameterContens.depVo);
        Map<String, Object> map = new HashMap<>();
        map.put("subdistrict", 3);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getAmapDistrict, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                List<ProvinceMo> list = new Gson().fromJson(result, new TypeToken<List<ProvinceMo>>() {
                }.getType());
                if (list != null && list.size() > 0) {
                    mData01 = list;
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
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
                String et_introduceStr = et_introduce.getText().toString().trim();

                if (StringUtils.isMobileNO(etnameStr) || TextUtils.isEmpty(et_addressStr)) {
                    ToastUtil.showShort(this, "请输入商户称");
                    return;
                }
                if (TextUtils.isEmpty(et_projectStr)) {
                    ToastUtil.showShort(this, "主营项目");
                    return;
                }
                if (StringUtils.isEmpty(province) || StringUtils.isEmpty(city) || StringUtils.isEmpty(area)) {
                    ToastUtil.showShort(this, "请选择区域");
                    return;
                }

                if (TextUtils.isEmpty(et_addressStr)) {
                    ToastUtil.showShort(this, "详细地址");
                    return;
                }
                if (file == null) {
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
                BottomDialogUtil2.getInstance(this).showLive(R.layout.dialog_came, view1 -> {
                    view1.findViewById(R.id.takePhoto).setOnClickListener(view2 -> {
                        OpenCameUtil.init("zhiz");
                        OpenCameUtil.openCamera(this);
                        BottomDialogUtil2.getInstance(this).dess();
                    });
                    view1.findViewById(R.id.choosePhoto).setOnClickListener(view2 -> {
                        OpenCameUtil.init("zhiz");
                        OpenCameUtil.openAlbum(this);
                        BottomDialogUtil2.getInstance(this).dess();
                    });
                });
                break;
            case R.id.rl_address: //选择区域
                showLocation();
                break;
            default:
                break;
        }
    }

    private void UserApplyUpload() {
        isLoading(true);
        Map<String, Object> map = new HashMap<>();
        map.put("name", depVo.getName());
        map.put("mainCategory", depVo.getMainCategory());
        map.put("userName", depVo.getUsername());
        map.put("phone", depVo.getPhone());
        map.put("idcard", depVo.getIdcard());
        map.put("productionProvince", province);//省
        map.put("productionCity", city);//市
        map.put("productionCounty", area);//县
        map.put("productionAddress", depVo.getProductionAddress());//详细
        map.put("idcartFacial", depVo.getFileFont());
        map.put("idcartBehind", depVo.getFileBack());
        map.put("threeCertificates", depVo.getThreeCertificates());
        map.put("synopsis", depVo.getSynopsis());
        map.put("agreedAgreement", 1);

        OkHttp3Utils.getInstance(getContext()).upLoadFile(UserUrl.addDept, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                isLoading(false);
                Intent intent = new Intent(UserSJRZThreeActivity.this, UserApplySuccessActivity.class);
                intent.putExtra("name", "商家入驻");
                startActivity(intent);
            }

            @Override
            public void onFailed(String msg) {
                isLoading(false);
                ToastUtil.showShort(getContext(), msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_1: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    OpenCameUtil.startPhotoZoom(UserSJRZThreeActivity.this, imageUri, 7, 5);
                }
                break;
            }
            case REQ_2: {
                if (resultCode == RESULT_OK) {
                    OpenCameUtil.startPhotoZoom(UserSJRZThreeActivity.this, data.getData());
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
                                ig_id_front.setImageBitmap(bitmapCamera);
                                file = new File(new URI(OpenCameUtil.imageUris.toString()));
                                depVo.setThreeCertificates(file);
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


    private RecyclerView recycler_01;
    private RecyclerView recycler_02;
    private RecyclerView recycler_03;
    private RecyclerAdapter adapter01;
    private RecyclerAdapter adapter02;
    private RecyclerAdapter adapter03;
    private TextView tvSelect;

    private List<ProvinceMo> mData01 = new ArrayList<>();
    private List<ProvinceMo.CityMo> mData02 = new ArrayList<>();
    private List<ProvinceMo.XianMo> mData03 = new ArrayList<>();
    private boolean isSelectAll = false;
    private String selectStr = "";

    private String province;
    private String city;
    private String area;
    private String provinceRe;
    private String cityRe;
    private String areaRe;

    private void showLocation() {
        BottomDialogUtil2.getInstance(UserSJRZThreeActivity.this).showLive(R.layout.dialog_address, view1 -> {
            TextView tvSelectName = view1.findViewById(R.id.tvSelectName);
            tvSelect = view1.findViewById(R.id.tvSelect);
            tvSelect.setBackgroundResource(R.color.colorGray7);
            tvSelect.setClickable(false);
            tvSelect.setOnClickListener((view) -> {
                province = provinceRe;
                city = cityRe;
                area = areaRe;
                isSelectAll = true;
                tv_address.setText(selectStr);
                BottomDialogUtil2.getInstance(UserSJRZThreeActivity.this).dess();
            });
            if (!StringUtils.isEmpty(tv_address.getText().toString())) {
                tvSelectName.setText(tv_address.getText().toString());
            }
            recycler_01 = view1.findViewById(R.id.recycler_01);
            recycler_02 = view1.findViewById(R.id.recycler_02);
            recycler_03 = view1.findViewById(R.id.recycler_03);
            recycler_01.setLayoutManager(new LinearLayoutManager(getParent()));
            recycler_02.setLayoutManager(new LinearLayoutManager(getParent()));
            recycler_03.setLayoutManager(new LinearLayoutManager(getParent()));
            adapter01 = new RecyclerAdapter<ProvinceMo>(getContext(), mData01, R.layout.item_txt) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder, ProvinceMo o) {
                    TextView cb_txt = holder.getView(R.id.cb_txt);
                    cb_txt.setText(o.getName());
                    if (o.isCheck()) {
                        cb_txt.setBackgroundResource(R.drawable.shape_db);
                    } else {
                        cb_txt.setBackgroundResource(R.drawable.shape_gray_w);
                    }
                }
            };
            recycler_01.setAdapter(adapter01);

            adapter01.setOnItemClickListener((view2, position) -> {

                for (int i = 0; i < mData01.size(); i++) {
                    if (i == position) {
                        provinceRe = mData01.get(position).getName();
                        mData01.get(i).setCheck(true);
                    } else {
                        mData01.get(i).setCheck(false);
                    }
                }
                adapter01.updateDataa(mData01);

                mData02 = mData01.get(position).getDistricts();
                adapter02.updateDataa(mData02);

                selectStr = mData01.get(position).getName() + "-";
                tvSelectName.setText(selectStr);

            });

            adapter02 = new RecyclerAdapter<ProvinceMo.CityMo>(getContext(), mData02, R.layout.item_txt) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder, ProvinceMo.CityMo o) {
                    TextView cb_txt = holder.getView(R.id.cb_txt);
                    cb_txt.setText(o.getName());
                    if (o.isCheck()) {
                        cb_txt.setBackgroundResource(R.drawable.shape_db);
                    } else {
                        cb_txt.setBackgroundResource(R.drawable.shape_gray_w);
                    }
                }
            };
            recycler_02.setAdapter(adapter02);

            adapter02.setOnItemClickListener((view2, position) -> {

                for (int i = 0; i < mData02.size(); i++) {
                    if (i == position) {
                        cityRe = mData02.get(position).getName();
                        mData02.get(i).setCheck(true);
                    } else {
                        mData02.get(i).setCheck(false);
                    }
                }
                adapter02.updateDataa(mData02);

                mData03 = mData02.get(position).getDistricts();
                adapter03.updateDataa(mData03);


                selectStr = selectStr + mData02.get(position).getName() + "-";
                tvSelectName.setText(selectStr);
            });

            adapter03 = new RecyclerAdapter<ProvinceMo.XianMo>(getContext(), mData03, R.layout.item_txt) {
                @Override
                public void convert(Context mContext, BaseRecyclerHolder holder, ProvinceMo.XianMo o) {
                    TextView cb_txt = holder.getView(R.id.cb_txt);
                    cb_txt.setText(o.getName());
                    if (o.isCheck()) {
                        cb_txt.setBackgroundResource(R.drawable.shape_db);
                    } else {
                        cb_txt.setBackgroundResource(R.drawable.shape_gray_w);
                    }
                }
            };
            recycler_03.setAdapter(adapter03);

            adapter03.setOnItemClickListener((view2, position) -> {

                for (int i = 0; i < mData03.size(); i++) {
                    if (i == position) {
                        tvSelect.setBackgroundResource(R.color.colorDb5);
                        tvSelect.setClickable(true);
                        areaRe = mData03.get(position).getName();
                        mData03.get(i).setCheck(true);
                    } else {
                        mData03.get(i).setCheck(false);
                    }
                }
                adapter03.updateDataa(mData03);

                selectStr = selectStr + mData03.get(position).getName();
                tvSelectName.setText(selectStr);


            });
        });
    }
}
