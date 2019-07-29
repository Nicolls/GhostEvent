package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
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
    private static final long GO_BACK_WAIT_TIME = Constants.TIME_NOTIFY_PAGE_LOADED_DELAY * 2; // 毫秒
    private final RedirectHandler handler;
    private IWebTarget target;
    private final Semaphore semaphore = new Semaphore(0, true);

    private final RedirectHandler.RedirectListener listener = new RedirectHandler.RedirectListener() {
        @Override
        public void onStart() {
            LogUtil.d(TAG, "redirect load onStart");
        }

        @Override
        public void onSuccess() {
            LogUtil.d(TAG, "redirect load onSuccess");
            target.getMainHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "semaphore release");
                    semaphore.release();
                }
            }, Constants.TIME_NOTIFY_PAGE_LOADED_DELAY);
        }

        @Override
        public void onFail() {
            LogUtil.d(TAG, "redirect load onFail");
        }
    };

    public PageGoBackEvent(IWebTarget target, RedirectHandler handler) {
        super(target);
        this.handler = handler;
        this.target = target;
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
                        WebView webView = (WebView) target;
                        webView.goBack();
                        LogUtil.d(TAG, "doGoBack completed");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "go back run ,wait web load success!");
                boolean ok = semaphore.tryAcquire(getExecuteTimeOut(), TimeUnit.MILLISECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
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
        return GO_BACK_WAIT_TIME;
    }


}
