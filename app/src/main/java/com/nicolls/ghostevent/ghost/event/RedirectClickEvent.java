package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.model.LoadPageRedirectListener;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * author:mengjiankang
 * date:2018/11/25
 * <p>
 * </p>
 */
public class RedirectClickEvent extends ClickEvent {
    private static final String TAG = "RedirectClickEvent";
    private final Semaphore semaphore = new Semaphore(0, true);
    private final RedirectHandler handler;
    private final IWebTarget target;
    private final LoadPageRedirectListener listener;

    public RedirectClickEvent(ClickWebEvent clickEvent, RedirectHandler handler) {
        super(clickEvent);
        this.handler = handler;
        this.target = clickEvent.getWebTarget();
        this.listener = new LoadPageRedirectListener(target, semaphore);
        this.setName(TAG);
    }

    @Override
    public Completable exe(final AtomicBoolean cancel) {
        final Completable clickCompletable = super.exe(cancel);
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    return;
                }
                final WebView webView= (WebView) target;
                handler.registerRedirectListener(listener);
                clickCompletable.subscribe();
                LogUtil.d(TAG, "click run ,wait web load success!");
                boolean ok = semaphore.tryAcquire(getExecuteTimeOut(), TimeUnit.MILLISECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
                    LogUtil.d(TAG,"redirect time out! but ignore this exception! go on");
                    // 加载页面没有成功，则需要停止页面加载
                    webView.stopLoading();
                } else {
                    LogUtil.d(TAG, "web load completed");
                    handler.unRegisterRedirectListener(listener);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public long getExecuteTimeOut() {
        return listener.getLoadPageRedirectTimeOut() + super.getExecuteTimeOut();
    }

}
