package com.nicolls.ghostevent.ghost.event;

import android.annotation.SuppressLint;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.parse.JsBaseInterface;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class LoadJsInfEvent extends BaseEvent {
    private static final String TAG = "LoadJsInfEvent";
    private static final long PARSE_WAIT_JS_INIT_TIME = 2; // 秒
    private static final long PARSE_WAIT_TIME = PARSE_WAIT_JS_INIT_TIME + 2; // 秒
    private final Semaphore semaphore = new Semaphore(0, true);
    private IWebTarget target;
    private JsBaseInterface jsInterface;

    @SuppressLint("JavascriptInterface")
    public LoadJsInfEvent(IWebTarget target, JsBaseInterface jsInterface) {
        super(target);
        this.target = target;
        this.jsInterface = jsInterface;
        final WebView webView = (WebView) target;
        this.setName(TAG);
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
                LogUtil.d(TAG, "start to load js!");
                Completable.fromRunnable(new Runnable() {
                    @SuppressLint("JavascriptInterface")
                    @Override
                    public void run() {
                        final WebView webView = (WebView) target;
                        webView.addJavascriptInterface(jsInterface, jsInterface.getName());
                        webView.loadUrl(jsInterface.getJsText());
                        LogUtil.d(TAG, "do load js");
                        target.getEventHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d(TAG, "semaphore release");
                                semaphore.release();
                            }
                        }, PARSE_WAIT_JS_INIT_TIME * 1000);
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "exe load js subscribe ");
                boolean ok = semaphore.tryAcquire(PARSE_WAIT_TIME, TimeUnit.SECONDS);
                if (!ok) {
                    LogUtil.w(TAG, "load js time out!");
                } else {
                    LogUtil.d(TAG, "load js completed");
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public boolean needRetry() {
        return true;
    }
}
