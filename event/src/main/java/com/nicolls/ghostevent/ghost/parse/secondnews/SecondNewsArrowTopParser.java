package com.nicolls.ghostevent.ghost.parse.secondnews;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.parse.WebBaseParser;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;

public class SecondNewsArrowTopParser extends WebBaseParser {
    private static final String TAG = "SecondNewsArrowTopParser";

    public void foundItemByClass(IWebTarget target, final Semaphore semaphore) {
        LogUtil.d(TAG, "execute foundItemByClass ");
        final WebView webView = (WebView) target;
        LogUtil.d(TAG, "view width-height:" + webView.getWidth() + "-" + webView.getHeight());
        String findItemByClassName = String.format(Constants.JS_FUNCTION_FIND_ARROW_TOP_BY_CLASS_NAME,
                webView.getWidth(), webView.getHeight());
        LogUtil.d(TAG, "findItemByClassName " + findItemByClassName);
        target.executeJs(findItemByClassName);
        target.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "semaphore release");
                semaphore.release();
            }
        }, getParsedDelay());
    }

    @Override
    public void parse(IWebTarget target, Semaphore semaphore) {
        LogUtil.d(TAG, "start parse");
        foundItemByClass(target, semaphore);
        LogUtil.d(TAG, "end parse");
    }
}