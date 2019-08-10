package com.nicolls.ghostevent.ghost.event.model;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;

public class HomePageRedirectListener extends LoadPageRedirectListener {
    private static final String TAG = "HomePageRedirectListener";
    private static final int MAX_RECEIVE_SUCCESS_TIMES = 6;
    private final WebView webView;

    public HomePageRedirectListener(IWebTarget target, Semaphore semaphore) {
        super(target, semaphore);
        this.webView = (WebView) target;
        setMaxReceiveSuccessTimes(MAX_RECEIVE_SUCCESS_TIMES);
    }

    @Override
    public void onStart(String url) {
        LogUtil.d(TAG, "redirect load onStart");
    }

    @Override
    public void onSuccess(String url) {
        LogUtil.d(TAG, "redirect load onSuccess");
        check(url);
    }

    @Override
    public void onFail() {
        LogUtil.d(TAG, "redirect load onFail");
        check(null);
    }

    private void check(String url) {
        if (webView.canGoBack()) {
            LogUtil.d(TAG, "go back continue ");
            webView.goBack();
        } else {
            LogUtil.d(TAG, "can not go back ,to Home");
            super.onSuccess(url);
        }
    }
}
