package com.dabangvr.fragment.other.Order;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dabangvr.R;
import com.dbvr.baselibrary.model.Update;
import com.dbvr.baselibrary.update.InstallUtils;
import com.dbvr.baselibrary.update.utils.PermissionUtils;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.utils.VersionUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.UserUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于我们
 */
public class UserAboutActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.tv_version)
    TextView tv_version;

    @BindView(R.id.tv_version_t)
    TextView tv_version_t;

    private InstallUtils.DownloadCallBack downloadCallBack;
    private String apkDownloadPath;
    private TextView tv_massage;
    private String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_about;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void initView() {
        versionName = VersionUtil.getAppVersionName(this);
        tv_version.setText("V" + versionName);
        tv_version_t.setText("V" + versionName);
        initCallBack();
    }

    @Override
    public void initData() {

        tv_content.setText("fe fef efe fe fef ef ef efe fe wf wefewofwepkfwef wef fewofwef ");
    }

    @OnClick({R.id.ivBack, R.id.ll_version_update})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);

                break;
            case R.id.ll_version_update:
                checkVersion(1);
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置监听,防止其他页面设置回调后当前页面回调失效
        if (InstallUtils.isDownloading()) {
            InstallUtils.setDownloadCallBack(downloadCallBack);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initCallBack() {
        downloadCallBack = new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                tv_massage.setText("0%");
            }

            @Override
            public void onComplete(String path) {
                apkDownloadPath = path;
                tv_massage.setText("100%");

                //先判断有没有安装权限
                InstallUtils.checkInstallPermission(UserAboutActivity.this, new InstallUtils.InstallPermissionCallBack() {
                    @Override
                    public void onGranted() {
                        //去安装APK
                        installApk(apkDownloadPath);
                    }

                    @Override
                    public void onDenied() {
                        //弹出弹框提醒用户
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setTitle("温馨提示")
                                .setMessage("必须授权才能安装APK，请设置允许安装")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //打开设置页面
                                        InstallUtils.openInstallPermissionSetting(UserAboutActivity.this, new InstallUtils.InstallPermissionCallBack() {
                                            @Override
                                            public void onGranted() {
                                                //去安装APK
                                                installApk(apkDownloadPath);
                                            }

                                            @Override
                                            public void onDenied() {
                                                //还是不允许咋搞？
                                                ToastUtil.showShort(getContext(), "不允许安装咋搞？强制更新就退出应用程序吧！");
                                            }
                                        });
                                    }
                                })
                                .create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onLoading(long total, long current) {
                //内部做了处理，onLoading 进度转回progress必须是+1，防止频率过快
                int progress = (int) (current * 100 / total);
                tv_massage.setText(progress + "%");
            }

            @Override
            public void onFail(Exception e) {
                ToastUtil.showShort(getContext(), "下载失败");
            }

            @Override
            public void cancle() {

            }
        };
    }

    private void installApk(String path) {
        InstallUtils.installAPK(this, path, new InstallUtils.InstallCallBack() {
            @Override
            public void onSuccess() {
                //onSuccess：表示系统的安装界面被打开
                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                Toast.makeText(getContext(), "正在安装程序", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(Exception e) {
                ToastUtil.showShort(getContext(), "安装失败");
            }
        });
    }

    private void checkVersion(int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        OkHttp3Utils.getInstance(getContext()).doPostJson(UserUrl.updateApp, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                Log.d("luhuas", "onUi: " + result);
                try {
                    Update update = new Gson().fromJson(result, Update.class);
                    if (update != null) {
                        String versionCode = update.getVersionCode();
                        if (!TextUtils.isEmpty(versionCode)) {
                            if (!TextUtils.equals(versionCode, versionName)) {
                                DialogUtil.getInstance(getContext()).show(R.layout.dialog_tip, holder -> {
                                    TextView tvTitle = holder.findViewById(R.id.tv_title);
                                    tv_massage = holder.findViewById(R.id.tv_massage);
                                    String title = "提示！！";
                                    tv_massage.setVisibility(View.VISIBLE);
                                    tv_massage.setText("发现新版本，是否安装?");
                                    tvTitle.setText(title);
                                    holder.findViewById(R.id.tvCancel).setOnClickListener(view1 -> DialogUtil.getInstance(getContext()).des());
                                    holder.findViewById(R.id.tvConfirm).setOnClickListener(v -> download(update.getUrl()));
                                });
                            } else {
                                ToastUtil.showShort(getContext(), "当前是最新版本");
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    setLoaddingView(false);
                }

            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
                ToastUtil.showShort(getContext(), msg);
            }
        });
    }

    private void download(String path) {
        if (!PermissionUtils.isGrantSDCardReadPermission(this)) {
            PermissionUtils.requestSDCardReadPermission(this, 100);
        } else {
            InstallUtils.with(this)
                    //必须-下载地址
                    .setApkUrl(path)
                    //非必须-下载保存的文件的完整路径+name.apk
//                    .setApkPath(Constants.APK_SAVE_PATH)
                    //非必须-下载回调
                    .setCallBack(downloadCallBack)
                    //开始下载
                    .startDownload();
        }

    }


    ///调用系统的安装方法
    private void installAPK(File savedFile) {
        //调用系统的安装方法
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(getContext(), "fileprovider", savedFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(savedFile);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        this.startActivity(intent);
        this.finish();
    }

}
