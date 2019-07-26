package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
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
    private static final long LOAD_PAGE_WAIT_TIME = 15; // 秒
    private final Semaphore semaphore = new Semaphore(0,true);
    private final RedirectHandler handler;
    private final String url;
    private final IWebTarget target;
    public LoadPageEvent(IWebTarget target,RedirectHandler handler,String url) {
        super(target);
        this.handler = handler;
        this.url=url;
        this.target=target;
        this.setName(TAG);
    }

    private final RedirectHandler.RedirectListener listener = new RedirectHandler.RedirectListener() {
        @Override
        public void onStart() {
            LogUtil.d(TAG, "redirect load onStart");
        }

        @Override
        public void onSuccess() {
            LogUtil.d(TAG, "redirect load onSuccess");
            semaphore.release();
        }

        @Override
        public void onFail() {
            LogUtil.d(TAG, "redirect load onFail");
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
                LogUtil.d(TAG,"load url run ,wait web load success!");
                boolean ok = semaphore.tryAcquire(LOAD_PAGE_WAIT_TIME, TimeUnit.SECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
                    // loadpage 事件如果都没有成功，则需要停止所有的
                    cancel.set(true);
                    throw new RuntimeException("load page time out!");
                } else {
                    LogUtil.d(TAG,"web load page completed");
                    handler.unRegisterRedirectListener(listener);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
