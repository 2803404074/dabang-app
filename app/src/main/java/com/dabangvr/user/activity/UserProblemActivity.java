package com.dabangvr.user.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dabangvr.R;
import com.dbvr.baselibrary.base.ParameterContens;
import com.dbvr.baselibrary.model.QiniuUploadFile;
import com.dbvr.baselibrary.model.Search;
import com.dbvr.baselibrary.utils.OnUploadListener;
import com.dbvr.baselibrary.utils.QiniuUploadManager;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.baselibrary.view.MultiSelectPopupWindows;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.dbvr.imglibrary2.model.Image;
import com.dbvr.imglibrary2.ui.SelectImageActivity;
import com.dbvr.imglibrary2.ui.adapter.SelectedImageAdapter;
import com.dbvr.imglibrary2.utils.TDevice;
import com.dbvr.imglibrary2.widget.recyclerview.SpaceGridItemDecoration;
import com.google.gson.Gson;
import com.hyphenate.util.DensityUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户编辑信息
 */
public class UserProblemActivity extends BaseActivity {

    @BindView(R.id.ivAdd)
    ImageView ivAdd;
    @BindView(R.id.recycle_img)
    RecyclerView recyclerView;
    @BindView(R.id.et_content_fk)
    EditText et_content_fk;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.ll_addView)
    LinearLayout ll_addView;
    @BindView(R.id.ll_select)
    LinearLayout ll_select;
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int PIC_SUM = 4; //选择图片数量
    private static final int SELECT_IMAGE_REQUEST = 0x0011;
    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private SelectedImageAdapter mAdapter;
    private MultiSelectPopupWindows productsMultiSelectPopupWindows;
    private ArrayList<Search> products;
    private List<Search> itemList;
    private ArrayList<String> resultPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_problem;
    }

    @Override
    public void initView() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            setLoaddingView(true);
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);


                mSelectImages.clear();
                mSelectImages.addAll(selectImages);
                if (mSelectImages.size() > 1) {
                    ivAdd.setVisibility(View.VISIBLE);
                    if (mSelectImages.size() == PIC_SUM) {
                        ivAdd.setVisibility(View.GONE);
                    }
                }
                mAdapter = new SelectedImageAdapter(this, mSelectImages, R.layout.selected_image_item);
                recyclerView.setAdapter(mAdapter);
                mItemTouchHelper.attachToRecyclerView(recyclerView);

            }
        }

        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 101 && resultCode == 103) {
            String result = data.getStringExtra("result");
//            tvLocationName.setText(result);
        }
    }

    private ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            // 获取触摸响应的方向   包含两个 1.拖动dragFlags 2.侧滑删除swipeFlags
            // 代表只能是向左侧滑删除，当前可以是这样ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int dragFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        /**
         * 拖动的时候不断的回调方法
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //获取到原来的位置
            int fromPosition = viewHolder.getAdapterPosition();
            //获取到拖到的位置
            int targetPosition = target.getAdapterPosition();
            if (fromPosition < targetPosition) {
                for (int i = fromPosition; i < targetPosition; i++) {
                    Collections.swap(mSelectImages, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > targetPosition; i--) {
                    Collections.swap(mSelectImages, i, i - 1);
                }
            }
            mAdapter.notifyItemMoved(fromPosition, targetPosition);
            return true;
        }

        /**
         * 侧滑删除后会回调的方法
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mSelectImages.remove(position);
            mAdapter.notifyItemRemoved(position);
            ivAdd.setVisibility(View.VISIBLE);
        }
    });


    /**
     * 获取七牛token
     */
    private void getQiniuToken(List<String> img) {
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getUploadConfigToken, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                QiniuUploadFile qiniuUploadFile = new Gson().fromJson(result, QiniuUploadFile.class);
                upLoadImg(qiniuUploadFile, img);//上传图片
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
            }
        });
    }

    /**
     * 上传动态图片
     *
     * @param img
     */
    public void upLoadImg(QiniuUploadFile qiniuUploadFile, List<String> img) {
        resultPath = new ArrayList<>();
        List<QiniuUploadFile> qiniu = new ArrayList<>();
        for (int i = 0; i < img.size(); i++) {
            qiniu.add(new QiniuUploadFile(img.get(i), ParameterContens.problem + UUID.randomUUID(), qiniuUploadFile.getMimeType(), qiniuUploadFile.getUpLoadToken()));
        }
        QiniuUploadManager.getInstance(getContext()).upload(qiniu, new OnUploadListener() {
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
            public void onUploadBlockComplete(String key) {
                //上传成功一个文件
                resultPath.add(DyUrl.QINIUDOMAN + key);
            }

            @Override
            public void onUploadCompleted() {
                subProblem();
            }

            @Override
            public void onUploadCancel() {//取消上传
                setLoaddingView(false);
            }
        });
    }

    @Override
    public void initData() {
        getData();
    }


    private void selectImage() {
        int isPermission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int isPermission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (isPermission1 == PackageManager.PERMISSION_GRANTED && isPermission2 == PackageManager.PERMISSION_GRANTED) {
            startActivity();
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void startActivity() {
        Intent intent = new Intent(this, SelectImageActivity.class);
        intent.putExtra("size", PIC_SUM);
        intent.putParcelableArrayListExtra("selected_images", mSelectImages);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    private void getData() {
        products = new ArrayList<>();
        products.add(new Search("闪退", false, "0"));
        products.add(new Search("卡顿", false, "1"));
        products.add(new Search("黑屏/白屏", false, "2"));
        products.add(new Search("死机", false, "3"));
        products.add(new Search("界面显示异常", false, "4"));
        products.add(new Search("其他", false, "5"));
    }


    @OnClick({R.id.ivBack, R.id.ivAdd, R.id.ll_select, R.id.tvsub})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.ivAdd:
                selectImage();
                break;
            case R.id.ll_select:
                selectProblem();
                break;
            case R.id.tvsub:
                subMess();
                break;
        }
    }

    private void subMess() {

        setLoaddingView(true);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mSelectImages.size(); i++) {
            list.add(mSelectImages.get(i).getPath());
        }
        new Thread(() -> {
            getQiniuToken(list);

        }).start();


    }

    private void subProblem() {

        String content_fk = et_content_fk.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        StringBuilder stringBuilder = new StringBuilder();
        if (itemList == null || itemList.size() == 0) {
            setLoaddingView(false);
            ToastUtil.showShort(this, "请选择您要反馈的问题");
        } else {

            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).isChecked()) {
                    stringBuilder.append(itemList.get(i).getKeyWord() + ",");
                }
            }

        }
        if (TextUtils.isEmpty(content_fk)) {
            setLoaddingView(false);
            ToastUtil.showShort(this, "请详细描述您反馈的问题");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            setLoaddingView(false);
            ToastUtil.showShort(this, "请输入您的联系方式");
            return;
        }

        for (int i = 0; i < resultPath.size(); i++) {
            Log.d("luhuas", "subProblem: " + resultPath.get(i));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("type", stringBuilder.toString().substring(0, stringBuilder.length() - 1));
        map.put("detailed", content_fk);
        map.put("imgs", new Gson().toJson(resultPath));
        map.put("phone", phone);

        OkHttp3Utils.getInstance(getContext()).doPostJson(UserUrl.submitFeedback, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                Log.d("luhuas", "onUi: " + result);
                setLoaddingView(false);
                ToastUtil.showShort(UserProblemActivity.this, "问题已反馈给技术人员");
                AppManager.getAppManager().finishActivity(UserProblemActivity.this);
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
                ToastUtil.showShort(getContext(), msg);
            }
        });
    }

    private void selectProblem() {
        productsMultiSelectPopupWindows = new MultiSelectPopupWindows(getContext(), ll_select, 110, products);
        productsMultiSelectPopupWindows.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                itemList = productsMultiSelectPopupWindows.getItemList();
                ll_addView.removeAllViews();
                if (itemList != null && itemList.size() > 0) {
                    for (int i = 0; i < itemList.size(); i++) {
                        if (itemList.get(i).isChecked()) {
                            TextView textView = new TextView(UserProblemActivity.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            int i1 = DensityUtil.dip2px(UserProblemActivity.this, 10f);
                            layoutParams.setMargins(i1, i1, i1, i1);
                            textView.setLayoutParams(layoutParams);
                            textView.setText(itemList.get(i).getKeyWord());
                            ll_addView.addView(textView);
                        }
                    }
                }
            }
        });
    }
}
