package com.nicolls.ghostevent.ghost.event.enclosure;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.model.LoadPageRedirectListener;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class PageGoBackEvent extends BaseEvent {
    private static final String TAG = "PageGoBackEvent";
    private final RedirectHandler handler;
    private IWebTarget target;
    private final Semaphore semaphore = new Semaphore(0, true);

    private final LoadPageRedirectListener listener;

    public PageGoBackEvent(IWebTarget target, RedirectHandler handler) {
        super(target);
        this.handler = handler;
        this.target = target;
        this.listener = new LoadPageRedirectListener(target, semaphore);
        this.setName(TAG);
    }

    @Override
    public Completable exe(AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                handler.registerRedirectListener(listener);
                final WebView webView = (WebView) target;
                Completable.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if(webView.canGoBack()){
                            webView.goBack();
                            LogUtil.d(TAG, "doGoBack completed");
                        }else {
                            LogUtil.d(TAG, "can not go back!");
                            semaphore.release();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "go back run ,wait web load success!");
                boolean ok = semaphore.tryAcquire(getExecuteTimeOut(), TimeUnit.MILLISECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
                    // 加载页面没有成功，则需要停止页面加载
                    LogUtil.w(TAG, "go back time out,stop loading");
                    webView.stopLoading();
                    throw new RuntimeException("go back time out!");
                } else {
                    LogUtil.d(TAG, "web go back completed");
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
