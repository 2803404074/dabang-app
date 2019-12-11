package com.dabangvr.comment.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.app.hubert.library.Controller;
import com.app.hubert.library.NewbieGuide;
import com.app.hubert.library.OnGuideChangedListener;
import com.dabangvr.R;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.home.activity.SearchActivity;
import com.dabangvr.home.fragment.HomeFragment;
import com.dabangvr.live.activity.GeGoActivity;
import com.dabangvr.mall.activity.CartActivity;
import com.dabangvr.mall.activity.OrderActivity;
import com.dabangvr.user.activity.UserAboutActivity;
import com.dabangvr.user.activity.UserDropActivity;
import com.dabangvr.user.activity.UserIntroduceActivity;
import com.dabangvr.user.activity.UserMessActivity;
import com.dabangvr.user.activity.UserSettingActivity;
import com.dbvr.baselibrary.adapter.ContentPagerAdapter;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity;
import com.tencent.liteav.demo.videorecord.utils.TCConstants;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.ugc.TXRecordCommon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity instance;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.ivFunction)
    ImageView ivFunction;

    private SimpleDraweeView sdvHead;

    private TextView tvNickName;

    private TextView tvIntroduce;

    @BindView(R.id.tvMain)
    TextView tvMain;

    @BindView(R.id.tab_layout)
    SmartTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private ArrayList<Fragment> mFragments;
    private ContentPagerAdapter contentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }


    protected UserMess userMess;
    @SuppressLint("WrongConstant")
    @Override
    public void initView() {
        userMess = SPUtils.instance(this).getUser();
        if (userMess!=null){
            if (userMess.getIsAnchor()!=1){
                ivFunction.setVisibility(View.GONE);
                navigationView.getMenu().add(11,11,11,"商户入住").setIcon(R.mipmap.wifi);
            }else {
                ivFunction.setVisibility(View.VISIBLE);
                navigationView.getMenu().add(22,22,22,"我的店铺").setIcon(R.mipmap.wifi);
            }
        }
        navigationView.setNavigationItemSelectedListener(this);
        sdvHead = navigationView.getHeaderView(0).findViewById(R.id.sdvHead_main);
        tvNickName = navigationView.getHeaderView(0).findViewById(R.id.tvNickName);
        tvIntroduce = navigationView.getHeaderView(0).findViewById(R.id.tvIntroduce);
        tvIntroduce.setOnClickListener((view) -> {
            goTActivityForResult(UserIntroduceActivity.class, null, 100);
        });
        AppManager.getAppManager().finishActivity(LoginActivity.class);

        getType();
    }

    private void getType() {
        //获取标签
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.getLiveCategoryList,null,new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result){
                List<TagMo> list = new Gson().fromJson(result, new TypeToken<List<TagMo>>() {}.getType());
                list.add(0,new TagMo("0","关注"));
                list.add(1,new TagMo("1","附近"));
                List<String> mTitles = new ArrayList<>();
                if (list != null && list.size() > 0) {
                    mFragments = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        mFragments.add(new HomeFragment(Integer.parseInt(list.get(i).getId())));
                        mTitles.add(list.get(i).getName());
                    }
                    contentAdapter = new ContentPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
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

    @OnClick({R.id.tvMain, R.id.ivUser, R.id.ivFunction, R.id.ivSearch})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.tvMain:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ivUser:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ivFunction:
                showFunction();
                break;
            case R.id.ivSearch:
                goTActivity(SearchActivity.class, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void initData() {

        if (null == userMess) {
            goTActivity(LoginActivity.class, null);
            AppManager.getAppManager().finishActivity(MainActivity.class);
            return;
        }
        sdvHead.setImageURI(userMess.getHeadUrl());
        tvNickName.setText(userMess.getNickName());
        tvIntroduce.setText(StringUtils.isEmpty(userMess.getAutograph()) ? "未设置个性签名.." : userMess.getAutograph());

        initController();
    }

    private void initController() {
        Controller controller = NewbieGuide.with(this)
                .setOnGuideChangedListener(new OnGuideChangedListener() {//设置监听
                    @Override
                    public void onShowed(Controller controller) {
                        //引导层显示
                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        //引导层消失
                    }
                })
                .setBackgroundColor(Color.BLACK)//设置引导层背景色，建议有透明度，默认背景色为：0xb2000000
                .setEveryWhereCancelable(true)//设置点击任何区域消失，默认为true
                .setLayoutRes(R.layout.dialog_func_tips, R.id.tvQuit, R.id.ivLine, R.id.tvTip)//自定义的提示layout,第二个可变参数为点击隐藏引导层view的id
                .alwaysShow(true)//是否每次都显示引导层，默认false
                .setLabel("guide1")
                .build();//构建引导层的控制器
        controller.resetLabel("guide1");
        controller.remove();//移除引导层
        controller.show();//显示引导层
    }

    private void startVideoRecordActivity() {
        MyApplication.getInstance().initShortVideo();
        Intent intent = new Intent(this, TCVideoRecordActivity.class);
        intent.putExtra(TCConstants.RECORD_CONFIG_MIN_DURATION, 5 * 1000);
        intent.putExtra(TCConstants.RECORD_CONFIG_MAX_DURATION, 60 * 1000);
        intent.putExtra(TCConstants.RECORD_CONFIG_ASPECT_RATIO, TXRecordCommon.VIDEO_ASPECT_RATIO_9_16);//视频比例
        intent.putExtra(TCConstants.RECORD_CONFIG_RECOMMEND_QUALITY, TXRecordCommon.VIDEO_QUALITY_HIGH);//超清
        intent.putExtra(TCConstants.RECORD_CONFIG_HOME_ORIENTATION, TXLiveConstants.VIDEO_ANGLE_HOME_DOWN); // 竖屏录制
        intent.putExtra(TCConstants.RECORD_CONFIG_TOUCH_FOCUS, true);//手动对焦
        intent.putExtra(TCConstants.RECORD_CONFIG_NEED_EDITER, true);//录制完去编辑
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_qb) {
            goTActivity(UserDropActivity.class, null);
        } else if (id == R.id.nav_order) {
            goTActivity(OrderActivity.class, null);
        } else if (id == R.id.nav_cart) {
            goTActivity(CartActivity.class, null);
        } else if (id == R.id.nav_mess) {
            goTActivity(UserMessActivity.class, null);
        }else if (id == R.id.nav_about) {
            goTActivity(UserAboutActivity.class, null);
        } else if (id == R.id.nav_set) {
            goTActivity(UserSettingActivity.class, null);
        }else if (id==11){
            ToastUtil.showShort(getContext(),"入驻");
        }else if (id == 22){
            ToastUtil.showShort(getContext(),"我的商户");
        }
        return true;
    }

    private void showFunction() {
        BottomDialogUtil2.getInstance(this).show(R.layout.dialog_main_function, 0, view -> {
            view.findViewById(R.id.tvOpenLive).setOnClickListener(view13 -> {
                //goTActivity(CreateLiveActivity.class,null);
                checkPermission();
                BottomDialogUtil2.getInstance(MainActivity.this).dess();
            });
            view.findViewById(R.id.tvOpenVideo).setOnClickListener(view12 -> {
                startVideoRecordActivity();
                BottomDialogUtil2.getInstance(MainActivity.this).dess();
            });
            view.findViewById(R.id.ivClose).setOnClickListener(view1 -> BottomDialogUtil2.getInstance(MainActivity.this).dess());
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 222);
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

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    AppManager.getAppManager().AppExit(this);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BottomDialogUtil2.getInstance(MainActivity.this).dess();
    }


}
