package com.dabangvr.home.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.dabangvr.R;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.live.activity.GeGoActivity;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tencent.liteav.demo.my.activity.ShortVideoActivity;
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity;
import com.tencent.liteav.demo.videorecord.utils.TCConstants;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.ugc.TXRecordCommon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragmentHome extends BaseFragment {
    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.tvVideo)
    TextView tvVideo;
    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter contentAdapter;

    @Override
    public int layoutId() {
        return R.layout.app_bar_main;
    }

    @Override
    public void initView() {
        tvVideo.setOnClickListener(view -> {
            goTActivity(ShortVideoActivity.class,null);
        });
        getType();
    }

    @Override
    public void initData() {

    }

    private void getType() {
        //获取标签
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getLiveCategoryList, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                List<TagMo> list = new Gson().fromJson(result, new TypeToken<List<TagMo>>() {
                }.getType());
                list.add(0, new TagMo("1", "关注"));
                List<String> mTitles = new ArrayList<>();
                if (list != null && list.size() > 0) {
                    mFragments = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        mFragments.add(new HomeFragment(Integer.parseInt(list.get(i).getId())));
                        mTitles.add(list.get(i).getName());
                    }
                    contentAdapter = new ContentPagerAdapter(getChildFragmentManager(), mTitles, mFragments);
                    viewPager.setAdapter(contentAdapter);
                    tabLayout.setViewPager(viewPager);
                    viewPager.setOffscreenPageLimit(1);
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    @OnClick({R.id.ivSearch,R.id.ivFunction})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivSearch:
                goTActivity(SearchActivity.class,null);
                break;
            case R.id.ivFunction:
                showFunction();
                break;
                default:break;
        }
    }

    private void showFunction() {
        BottomDialogUtil2.getInstance(getActivity()).show(R.layout.dialog_main_function, 0, view -> {
            view.findViewById(R.id.tvOpenLive).setOnClickListener(view13 -> {
                //goTActivity(CreateLiveActivity.class,null);
                checkPermission();
                BottomDialogUtil2.getInstance(getActivity()).dess();
            });
            view.findViewById(R.id.tvOpenVideo).setOnClickListener(view12 -> {
                startVideoRecordActivity();
                BottomDialogUtil2.getInstance(getActivity()).dess();
            });
            view.findViewById(R.id.ivClose).setOnClickListener(view1 -> BottomDialogUtil2.getInstance(getActivity()).dess());
        });
    }

    private void startVideoRecordActivity() {
        MyApplication.getInstance().initShortVideo();
        Intent intent = new Intent(getContext(), TCVideoRecordActivity.class);
        intent.putExtra(TCConstants.RECORD_CONFIG_MIN_DURATION, 5 * 1000);
        intent.putExtra(TCConstants.RECORD_CONFIG_MAX_DURATION, 60 * 1000);
        intent.putExtra(TCConstants.RECORD_CONFIG_ASPECT_RATIO, TXRecordCommon.VIDEO_ASPECT_RATIO_9_16);//视频比例
        intent.putExtra(TCConstants.RECORD_CONFIG_RECOMMEND_QUALITY, TXRecordCommon.VIDEO_QUALITY_HIGH);//超清
        intent.putExtra(TCConstants.RECORD_CONFIG_HOME_ORIENTATION, TXLiveConstants.VIDEO_ANGLE_HOME_DOWN); // 竖屏录制
        intent.putExtra(TCConstants.RECORD_CONFIG_TOUCH_FOCUS, true);//手动对焦
        intent.putExtra(TCConstants.RECORD_CONFIG_NEED_EDITER, true);//录制完去编辑
        startActivity(intent);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 222);
                return;
            } else {
                goTActivity(GeGoActivity.class, null);
            }
        } else {
            goTActivity(GeGoActivity.class, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 222){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goTActivity(GeGoActivity.class, null);
            } else {
                ToastUtil.showShort(getContext(),"您已禁用了相机权限，将无法使用开播功能");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
