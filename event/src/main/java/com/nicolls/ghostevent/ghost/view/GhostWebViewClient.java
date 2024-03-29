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
    private boolean isLoadStartDone = false;

    public GhostWebViewClient(IWebTarget target, RedirectHandler redirectHandler) {
        this.redirectHandler = redirectHandler;
        this.target = target;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String scheme = "http";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            scheme = request.getUrl().getScheme();
        }
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

    public void initLoadStartDone() {
        LogUtil.d(TAG,"initLoadStartDone");
        isLoadStartDone = false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        LogUtil.d(TAG, "onPageStarted " + url);
        isLoadStartDone = true;
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
            onSuccess(view, url);
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
                onError(view);
            }
        } else {
            onError(view);
        }

    }

    private void onError(WebView view) {
        if (!isLoadStartDone) {
            LogUtil.d(TAG, "onError have not start");
            return;
        }
        isLoadStartDone = false;
        isError = true;
        LogUtil.d(TAG, "onError");
        ParseManager.getInstance().loadJsInterface(view, url);
        target.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectHandler.notifyFail();
                isLoadStartDone = false;
            }
        }, 1000);

    }

    /**
     * 加载成功
     */
    private void onSuccess(WebView view, final String url) {
        LogUtil.d(TAG, "load onSuccess start done:" + isLoadStartDone);
        if (!isLoadStartDone) {
            LogUtil.d(TAG, "onSuccess have not start");
            return;
        }
        ParseManager.getInstance().loadJsInterface(view, url);
        target.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectHandler.notifySuccess(url);
            }
        }, 1000);
        isLoadStartDone = false;
        GhostUtils.Page page = GhostUtils.currentPage(url);
        switch (page) {
            case HOME:
                EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_SHOW_HOME_PAGE);
            case OTHER:
                EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_CLICK_ADVERT);
            case SECOND_ADVERT:
                EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_SHOW_SECOND_ADVERT_PAGE);

            case SECOND_NEWS:
                EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_SHOW_SECOND_NEWS_PAGE);

            default:
                break;
        }
    }

}
