package com.nicolls.ghostevent.ghost.event;

import android.view.View;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class PageGoBackEvent extends BaseEvent {
    private static final String TAG="PageGoBackEvent";
    private static final long GO_BACK_WAIT_TIME = 6; // 秒
    private final RedirectHandler handler;
    private WebView webView;
    private final Semaphore semaphore = new Semaphore(0);

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

    public PageGoBackEvent(RedirectHandler handler, WebView webView) {
        this.handler = handler;
        this.webView = webView;
    }

    @Override
    public Completable exe(AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                handler.registerRedirectListener(listener);
                Completable.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        webView.goBack();
                        LogUtil.d(TAG,"doGoBack completed");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG,"go back done ,wait web load success!");
                boolean ok = semaphore.tryAcquire(GO_BACK_WAIT_TIME, TimeUnit.SECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
                    throw new RuntimeException("go back time out!");
                } else {
                    LogUtil.d(TAG,"web go back completed");
                    handler.unRegisterRedirectListener(listener);
                }
            }
        }).subscribeOn(Schedulers.io());
    }


}
