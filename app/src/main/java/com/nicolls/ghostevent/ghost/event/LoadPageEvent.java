package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class LoadPageEvent extends BaseEvent {
    private static final String TAG="LoadPageEvent";
    private static final long LOAD_PAGE_WAIT_TIME = 6; // 秒
    private final Semaphore semaphore = new Semaphore(0);
    private final RedirectHandler handler;
    private final String url;
    private final IWebTarget target;
    public LoadPageEvent(RedirectHandler handler,IWebTarget target,String url) {
        super(target);
        this.handler = handler;
        this.url=url;
        this.target=target;
        this.setName(TAG);
    }

    private final RedirectHandler.RedirectListener listener = new RedirectHandler.RedirectListener() {
        @Override
        public void onStart() {
            LogUtil.d(TAG, "onStart");
        }

        @Override
        public void onSuccess() {
            LogUtil.d(TAG, "onSuccess");
            semaphore.release();
        }

        @Override
        public void onFail() {
            LogUtil.d(TAG, "onFail");
        }
    };

    @Override
    public Completable exe(final AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    return;
                }
                handler.registerRedirectListener(listener);
                final WebView webView= (WebView) target;
                Completable.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(url);
                        LogUtil.d(TAG,"load url completed");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG,"click done ,wait web load success!");
                boolean ok = semaphore.tryAcquire(LOAD_PAGE_WAIT_TIME, TimeUnit.SECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
                    throw new RuntimeException("load page time out!");
                } else {
                    LogUtil.d(TAG,"web load page completed");
                    handler.unRegisterRedirectListener(listener);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
