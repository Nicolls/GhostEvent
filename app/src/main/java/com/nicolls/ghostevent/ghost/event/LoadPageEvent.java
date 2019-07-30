package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.model.LoadPageRedirectListener;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class LoadPageEvent extends BaseEvent {
    private static final String TAG = "LoadPageEvent";
    private final Semaphore semaphore = new Semaphore(0, true);
    private final RedirectHandler handler;
    private final String url;
    private final IWebTarget target;
    private final LoadPageRedirectListener listener;

    public LoadPageEvent(IWebTarget target, RedirectHandler handler, String url) {
        super(target);
        this.handler = handler;
        this.url = url;
        this.target = target;
        this.listener = new LoadPageRedirectListener(target, semaphore);
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
                handler.registerRedirectListener(listener);
                final WebView webView = (WebView) target;
                Completable.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(url);
                        LogUtil.d(TAG, "load url completed");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "load url run ,wait web load success!");
                boolean ok = semaphore.tryAcquire(getExecuteTimeOut(), TimeUnit.MILLISECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
                    // 加载页面没有成功，则需要停止页面加载
                    LogUtil.w(TAG, "load page time out,stop loading");
                    webView.stopLoading();
                    throw new RuntimeException("load page time out!");
                } else {
                    LogUtil.d(TAG, "web load page completed");
                    handler.unRegisterRedirectListener(listener);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public long getExecuteTimeOut() {
        return getExtendsTime() + listener.getLoadPageRedirectTimeOut();
    }
}
