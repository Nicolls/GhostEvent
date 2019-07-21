package com.nicolls.ghostevent.ghost.view;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.LogUtil;

public class BaseWebView extends WebView {
    private static final String TAG = "BaseWebView";
    private WebSettings webSettings;
    public BaseWebView(Context context) {
        super(context);
        init();
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT <= 16) {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
        }
        webSettings = this.getSettings();
        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setAllowFileAccess(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
        LogUtil.d(TAG, "initWebView end");
    }

    /**
     * 重写loadUrl,目的是为了判断url的准入
     *
     * @param url 加载的url
     */
    @Override
    public void loadUrl(String url) {
        if(TextUtils.isEmpty(url)){
            LogUtil.e(TAG,"url is null!!");
            return;
        }
        if (verifyUrl(url.trim()) < 0) {
            return;
        }
        super.loadUrl(url);

    }

    /**
     * 判断当前url是否合法，业务方可以根据业务场景，增加url的白名单匹配过程；如禁止webview加载非白名单中的url
     */
    private int verifyUrl(String url) {
        // file协议只允许assert相关目录下的html文件
        if (fileUrlISSafe(url)) {
            enableFileCrossAccess();
        } else {
            disableFileCrossAccess();
        }

        return 1;
    }

    // 关闭了file协议的跨域访问，防止webview加载外部file文件读取应用内的私有文件，造成信息泄露
    private void disableFileCrossAccess() {
        webSettings.setAllowFileAccess(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(false);
            webSettings.setAllowUniversalAccessFromFileURLs(false);
        }
    }

    // 开启file域访问能力,确保加载file协议文件，是在assert目录下，或者信任的私有目录下
    private void enableFileCrossAccess() {
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
    }

    private boolean fileUrlISSafe(String fileUrl) {
        boolean flag = false;
        if (fileUrl.startsWith("file")) {
            // file路径截取
            String urlPath = fileUrl.substring(fileUrl.indexOf("file:") + 7);
            // 可信的file协议的目录文件
            if (!TextUtils.isEmpty(((CharSequence) urlPath)) && !urlPath.contains("..") && !urlPath.contains("\\")
                    && !urlPath.contains("%")) {
                if (urlPath.startsWith("/android_asset") || urlPath.startsWith("/android_res")) {
                    flag = true;
                }
            }
        }

        return flag;
    }
}
