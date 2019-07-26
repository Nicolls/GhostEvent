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

public class HomePageEvent extends BaseEvent {
    private static final String TAG = "HomePageEvent";
    private static final long GO_BACK_WAIT_TIME = 10; // 秒
    private final RedirectHandler handler;
    private WebView webView;
    private final Semaphore semaphore = new Semaphore(0,true);

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
                semaphore.release();
            }
        }

        @Override
        public void onFail() {
            LogUtil.d(TAG, "redirect load onFail");
        }
    };

    public HomePageEvent(RedirectHandler handler, IWebTarget target) {
        super(target);
        this.handler = handler;
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
                        if(webView.canGoBack()){
                            webView.goBack();
                            LogUtil.d(TAG, "do first go Home completed");
                        }else {
                            LogUtil.d(TAG, "already in home page ,end!");
                            semaphore.release();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "first go home run ,wait web load success!");
                boolean ok = semaphore.tryAcquire(GO_BACK_WAIT_TIME, TimeUnit.SECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
                    throw new RuntimeException("go home time out!");
                } else {
                    LogUtil.d(TAG, "web go home completed");
                    handler.unRegisterRedirectListener(listener);
                }
            }
        }).subscribeOn(Schedulers.io());
    }


}
