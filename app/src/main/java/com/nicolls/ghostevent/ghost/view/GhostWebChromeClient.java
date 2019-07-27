package com.nicolls.ghostevent.ghost.view;

import android.os.Message;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

public class GhostWebChromeClient extends WebChromeClient {
    private static final String TAG = "GhostWebChromeClient";

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        LogUtil.d(TAG, "onCreateWindow");
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onCloseWindow(WebView window) {
        LogUtil.d(TAG, "onCloseWindow");
        super.onCloseWindow(window);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        LogUtil.d(TAG, "onJsAlert");
        if (result != null) {
            result.cancel();
            return true;
        } else {
            return onJsAlert(view, url, message, result);
        }
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        LogUtil.d(TAG, "onJsConfirm");
        if (result != null) {
            result.cancel();
            return true;
        } else {
            return onJsConfirm(view, url, message, result);
        }
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        LogUtil.d(TAG, "onJsPrompt");
        if (result != null) {
            result.cancel();
            return true;
        } else {
            return onJsPrompt(view, url, message, defaultValue, result);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        LogUtil.d(TAG, "onConsoleMessage:" + consoleMessage);
        return super.onConsoleMessage(consoleMessage);
    }
}
