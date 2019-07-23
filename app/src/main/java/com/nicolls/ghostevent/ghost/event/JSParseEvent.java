package com.nicolls.ghostevent.ghost.event;

import android.graphics.Rect;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.parse.JsGhostParser;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class JSParseEvent extends BaseEvent {
    private static final String TAG = "JSParseEvent";
    private static final long PARSE_WAIT_TIME = 6; // 秒
    private final Semaphore semaphore = new Semaphore(0);
    private IWebTarget target;

    public JSParseEvent(IWebTarget target) {
        super(target);
        this.target = target;
    }

    @Override
    public Completable exe(final AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    return;
                }
                LogUtil.d(TAG, "start to parse!");
                final WebView webView = (WebView) target;
                Completable.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        webView.addJavascriptInterface(new JsGhostParser(target, semaphore), "jsParse");
//                        webView.loadUrl("javascript:alert(window.jsParse)");
                        webView.loadUrl("javascript:alert('ljsfw')");
                        LogUtil.d(TAG, "parse load js code wait to found");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "exe parse subscribe ");
                boolean ok = semaphore.tryAcquire(PARSE_WAIT_TIME, TimeUnit.SECONDS);
                if (!ok) {
                    throw new RuntimeException("parse page time out!");
                } else {
                    LogUtil.d(TAG, "parse page completed");
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
