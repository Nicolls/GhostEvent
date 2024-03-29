package com.nicolls.ghostevent.ghost.parse.home;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.parse.WebBaseParser;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;

public class HomeParser extends WebBaseParser {
    private static final String TAG = "HomeParser";

    public void foundItem(IWebTarget target, final Semaphore semaphore) {
        ParseManager.getInstance().clearViewNodes();
        LogUtil.d(TAG, "execute foundItem ");
        final WebView webView= (WebView) target;
        LogUtil.d(TAG,"view width-height:"+webView.getWidth()+"-"+webView.getHeight());
        String findItem=String.format(Constants.JS_FUNCTION_FIND_ITEM,webView.getWidth(),webView.getHeight());
        LogUtil.d(TAG,"findItem "+findItem);
        String printContext=String.format(Constants.JS_FUNCTION_PRINT_CONTEXT,webView.getWidth(),webView.getHeight());
        target.executeJs(printContext);
        target.executeJs(findItem);

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
        foundItem(target, semaphore);
        LogUtil.d(TAG, "end parse");
    }
}