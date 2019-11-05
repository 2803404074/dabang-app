package com.dabangvr.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dabangvr.R;
import com.dbvr.baselibrary.utils.HtmlStyleUtil;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.baselibrary.view.BaseHtmlActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class HtmlActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.bar)
    ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_html;
    }

    @Override
    public void initView() {
        webData();
        webViewSettingsInit();
    }

    private void webData() {
        // setWebViewClient 此方法的作用是,当在webView进点击时,不跳转到游览器的设置(也就是不打开新的Activity),而是在本app里进行操作
        webView.setWebViewClient(new WebViewClient());
        // requestFocus 触摸焦点起作用（如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件
        webView.requestFocus();

        // setWebChromeClient 该监听事件是指UI(界面)发送改变时进行各监听.  onProgressChanged
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //首先通过代码让pRogressBar显示出来
                webView.setVisibility(View.VISIBLE);
                //其次对progressBar设置加载进度的参数
                bar.setProgress(newProgress);
                if (newProgress == 100) {
                    //加载完毕让进度条消失
                    bar.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }
    private void webViewSettingsInit(){
        //      得到一个webView的设置对象,WebSettings.
        WebSettings settings = webView.getSettings();
        // setJavaScriptEnabled  使webView可以支持Javascript:
        settings.setJavaScriptEnabled(true);
        // setSupportZoom 使webView允许网页缩放,记住用这方法前,要有让webView支持Javascript的设定.否则会不起作用
        settings.setSupportZoom(true);
    }


    @OnClick(R.id.ivBack)
    public void onclick(View view) {
        if (view.getId() == R.id.ivBack) {
            finish();
        }
    }

    @Override
    public void initData() {
        webView.loadUrl(getIntent().getStringExtra("url"));
    }
}
