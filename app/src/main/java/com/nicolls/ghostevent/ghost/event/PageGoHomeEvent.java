package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.model.HomePageRedirectListener;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class PageGoHomeEvent extends BaseEvent {
    private static final String TAG = "HomePageEvent";
    private final RedirectHandler handler;
    private WebView webView;
    private ITarget target;
    private final Semaphore semaphore = new Semaphore(0, true);

    private final HomePageRedirectListener listener;

    public PageGoHomeEvent(IWebTarget target, RedirectHandler handler) {
        super(target);
        this.handler = handler;
        this.target = target;
        this.webView = (WebView) target;
        this.listener = new HomePageRedirectListener(target, semaphore);
        this.setName(TAG);
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
                        if (webView.canGoBack()) {
                            webView.goBack();
                            LogUtil.d(TAG, "do first go Home completed");
                        } else {
                            LogUtil.d(TAG, "already in home page ,end!");
                            listener.onSuccess();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "first go home run ,wait web load success!");
                boolean ok = semaphore.tryAcquire(getExecuteTimeOut(), TimeUnit.MILLISECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
                    throw new RuntimeException("go home time out!");
                } else {
                    handler.unRegisterRedirectListener(listener);
                    LogUtil.d(TAG, "web go home completed");
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public long getExecuteTimeOut() {
        return getExtendsTime() + listener.getLoadPageRedirectTimeOut();
    }

}