package com.dbvr.baselibrary.view;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

import com.dbvr.baselibrary.ui.LoadingUtils;
import com.dbvr.baselibrary.utils.HtmlStyleUtil;
import com.dbvr.baselibrary.utils.MyWebViewClient;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import butterknife.ButterKnife;
import okhttp3.Call;

public abstract class BaseHtmlActivity extends AppCompatActivity {

    private Context mContext;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        ButterKnife.bind(this);
        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }

        mContext = this;
        initView();
        initData();
    }

    private LoadingUtils mLoaddingUtils;

    public void setLoaddingView(boolean isLoadding){
        if(mLoaddingUtils==null){
            mLoaddingUtils=new LoadingUtils(mContext);
        }
        if(isLoadding){
            mLoaddingUtils.show();
        }else{
            mLoaddingUtils.dismiss();
        }
    }



    public abstract int setLayout();

    public abstract void initView();

    public abstract void initData();

    public Context getContext() {
        return this.mContext;
    }

    public void setWebView(WebView webViewx, String html) {
        this.webView = webViewx;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 有网络时采用缓存
        webView.getSettings().setUserAgentString(System.getProperty("http.agent"));
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        MyWebViewClient webViewClient = new MyWebViewClient(webView);
        webViewClient.setWebCallBack(error -> {
            setLoaddingView(false);
            ToastUtil.showShort(mContext,"网络异常");
        });
        webView.setWebViewClient(webViewClient);
        webView.loadDataWithBaseURL(null, null, "text/html","utf-8", null);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    //加载完成
                    setLoaddingView(false);
                }
            }
        });
    }

    /**
     * 修改状态栏颜色
     * @param color
     */
    public void setStatusColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    /**
     *  设置状态栏字体颜色
     *  谷歌特定修改
     *  需要在跟布局加上 android:fitsSystemWindows="true" 这句话
     * @param dark   ture 黑色，false 白色，或者 根据flag去切换状态的颜色，具体的规则还不知道
     */
    public void setAndroidNativeLightStatusBar(boolean dark) {
        View decor = getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setLoaddingView(false);
        if (null != webView) {
            webView.destroy();
        }
        if (mContext != null) {
            mContext = null;
        }
    }
}
