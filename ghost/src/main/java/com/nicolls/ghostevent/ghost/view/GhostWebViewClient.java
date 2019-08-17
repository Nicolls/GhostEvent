package com.nicolls.ghostevent.ghost.view;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.request.EventReporter;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

public class GhostWebViewClient extends WebViewClient {
    private static final String TAG = "GhostWebViewClient";
    private boolean isError = false;
    private boolean isHaveNotifySuccess = false;
    private final RedirectHandler redirectHandler;
    private IWebTarget target;
    private String url;

    public GhostWebViewClient(IWebTarget target,RedirectHandler redirectHandler) {
        this.redirectHandler = redirectHandler;
        this.target=target;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String scheme = request.getUrl().getScheme();
        // 拦截非http / https类型请求
        if (!isHttpUrl(scheme)) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        // 拦截非http / https类型请求
        if (!isHttpUrl(scheme)) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    private boolean isHttpUrl(String scheme) {
        return scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https");
    }

    public String getCurrentUrl() {
        return url;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        LogUtil.d(TAG, "onPageStarted " + url);
        this.url = url;
        isError = false;
        isHaveNotifySuccess = false;
        redirectHandler.notifyStart(url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LogUtil.d(TAG, "onPageFinished " + url);
        this.url = url;
        if (!isError && !isHaveNotifySuccess) {
            isHaveNotifySuccess = true;
            onSuccess(view,url);
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        LogUtil.d(TAG, "onReceivedError");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LogUtil.d(TAG, "onReceivedError errorCode:" + error.getErrorCode()
                    + " description:" + error.getDescription());
        } else {
            LogUtil.d(TAG, "onReceivedError");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LogUtil.d(TAG, "new api");
            if (request.isForMainFrame()) {
                isError = true;
                redirectHandler.notifyFail();
            }
        } else {
            isError = true;
            redirectHandler.notifyFail();
        }

    }

    /**
     * 加载成功
     */
    private void onSuccess(WebView view,final String url) {
        LogUtil.d(TAG, "load onSuccess");
        ParseManager.getInstance().loadJsInterface(view, url);
        target.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectHandler.notifySuccess(url);
            }
        },1000);
        GhostUtils.Page page=GhostUtils.currentPage(url);
        switch (page){
            case HOME:
                EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_SHOW_HOME_PAGE,Constants.EVENT_TARGET_WEBVIEW,"");
            case OTHER:
                EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_CLICK_ADVERT,Constants.EVENT_TARGET_WEBVIEW,"");
            case SECOND_ADVERT:
                EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_SHOW_SECOND_ADVERT_PAGE,Constants.EVENT_TARGET_WEBVIEW,"");

            case SECOND_NEWS:
                EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_SHOW_SECOND_NEWS_PAGE,Constants.EVENT_TARGET_WEBVIEW,"");

                default:
                    break;
        }
    }

}
