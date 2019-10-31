package com.dabangvr.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dabangvr.fragment.SameCityFragment;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.imglibrary2.model.Image;
import com.dbvr.imglibrary2.ui.PreviewImageActivity;
import com.dbvr.imglibrary2.ui.SelectImageActivity;
import com.dbvr.imglibrary2.ui.adapter.SelectedImageAdapter;
import com.dbvr.imglibrary2.utils.TDevice;
import com.dbvr.imglibrary2.widget.recyclerview.SpaceGridItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 发表动态
 */
public class CreateDynamicActivity extends BaseActivity {

    @BindView(R.id.ivAdd)
    ImageView ivAdd;
    @BindView(R.id.recycle_img)
    RecyclerView recyclerView;
    @BindView(R.id.tvLocationName)
    TextView tvLocationName;
    @BindView(R.id.etInput)
    EditText etInput;
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int SELECT_IMAGE_REQUEST = 0x0011;
    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private List<String> mPath = new ArrayList<>();
    private SelectedImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_create_dynamic;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initView() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));


    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivAdd,R.id.ivClose,R.id.llLocation,R.id.tvSend})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivAdd:
                selectImage();
                break;
            case R.id.ivClose:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.llLocation:
                goTActivityForResult(LocationActivity.class,null,101);//101请求码，用于返回设置定位值
                break;
            case R.id.tvSend:
                if (StringUtils.isEmpty(etInput.getText().toString())){

                }else {
                    send();
                }
                break;
                default:break;
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity();
            } else {
                //申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                Toast.makeText(this, "需要您的存储权限!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double mJd;
    private double mWd;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                ArrayList<Image> selectImages = data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                mSelectImages.clear();
                mSelectImages.addAll(selectImages);
                if (mSelectImages.size() > 1) {
                    ivAdd.setVisibility(View.VISIBLE);
                    if (mSelectImages.size() == 9){
                        ivAdd.setVisibility(View.GONE);
                    }
                }
                mAdapter = new SelectedImageAdapter(this, mSelectImages, R.layout.selected_image_item);
                recyclerView.setAdapter(mAdapter);
                mItemTouchHelper.attachToRecyclerView(recyclerView);

                mAdapter.setItemClickListener((position)->{
                    ArrayList<Image> list = new ArrayList<>();
                    list.add(mSelectImages.get(position));
                    for (int i = 0; i < mSelectImages.size(); i++) {
                        if (position!=i){
                            list.add(mSelectImages.get(i));
                        }
                    }
                    Intent previewIntent = new Intent(this, PreviewImageActivity.class);
                    previewIntent.putParcelableArrayListExtra("preview_images", list);
                    startActivity(previewIntent);
                });
            }
        }

        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 101 && resultCode == 103) {
            mJd = data.getDoubleExtra("mJd", 0);
            mWd = data.getDoubleExtra("mWd", 0);
            String mCity = data.getStringExtra("mCity");
            String mProvince = data.getStringExtra("mProvince");
            tvLocationName.setText(StringUtils.isEmpty(mCity) ? mProvince : mCity);
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

    private void startActivity() {
        Intent intent = new Intent(this, SelectImageActivity.class);
        intent.putParcelableArrayListExtra("selected_images", mSelectImages);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }


    private void send(){
        Intent i = new Intent();
        i.putParcelableArrayListExtra("img", mSelectImages);
        i.putExtra("content", etInput.getText().toString());
        i.putExtra("mJd", mJd);
        i.putExtra("mWd", mWd);
//        i.putExtra("mJd", mJd);
//        i.putExtra("mWd", mWd);
        setResult(100, i);
        finish();
    }

}
