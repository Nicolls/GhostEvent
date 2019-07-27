package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.ITarget;
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

public class HomePageEvent extends BaseEvent {
    private static final String TAG = "HomePageEvent";
    private static final long GO_BACK_WAIT_TIME = 10 * 1000; // 毫秒
    private static final long WAIT_PAGE_LOADED_TIME = 3 * 1000; // 毫秒
    private final RedirectHandler handler;
    private WebView webView;
    private ITarget target;
    private final Semaphore semaphore = new Semaphore(0, true);

    private final RedirectHandler.RedirectListener listener = new RedirectHandler.RedirectListener() {
        @Override
        public void onStart() {
            LogUtil.d(TAG, "redirect load onStart");
        }

        @Override
        public void onSuccess() {
            LogUtil.d(TAG, "redirect load onSuccess");
            if (webView.canGoBack()) {
                LogUtil.d(TAG, "go back continue ");
                webView.goBack();
            } else {
                LogUtil.d(TAG, "can not go back ,to Home");
                target.getMainHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d(TAG, "semaphore release");
                        semaphore.release();
                    }
                }, WAIT_PAGE_LOADED_TIME);
            }
        }

        @Override
        public void onFail() {
            LogUtil.d(TAG, "redirect load onFail");
        }
    };

    public HomePageEvent(IWebTarget target, RedirectHandler handler) {
        super(target);
        this.handler = handler;
        this.target = target;
        this.webView = (WebView) target;
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
                            semaphore.release();
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
        return GO_BACK_WAIT_TIME;
    }

}
