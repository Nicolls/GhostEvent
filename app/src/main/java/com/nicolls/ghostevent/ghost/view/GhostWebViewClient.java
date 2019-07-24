package com.nicolls.ghostevent.ghost.view;

import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nicolls.ghostevent.ghost.event.RedirectHandler;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

public class GhostWebViewClient extends WebViewClient {
    private static final String TAG="GhostWebViewClient";
    private boolean isError = false;

    private final RedirectHandler redirectHandler;
    public GhostWebViewClient(RedirectHandler redirectHandler){
        this.redirectHandler=redirectHandler;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        isError = false;
        redirectHandler.notifyStart();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LogUtil.d(TAG, "onPageFinished");
        if (!isError) {
            onSuccess();
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
        isError = true;
        redirectHandler.notifyFail();
    }

    /**
     * 加载成功
     */
    private void onSuccess() {
        LogUtil.d(TAG, "load onSuccess");
        redirectHandler.notifySuccess();
    }

}
