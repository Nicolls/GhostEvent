package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * author:mengjiankang
 * date:2018/11/25
 * <p>
 * </p>
 */
public class ClickRedirectEvent extends ClickEvent {
    private static final String TAG = "ClickRedirectEvent";
    private static final long REDIRECT_WAIT_TIME = 6; // 秒
    private final Semaphore semaphore = new Semaphore(0);
    private final RedirectHandler handler;

    public ClickRedirectEvent(RedirectHandler handler, ITarget target, TouchPoint click) {
        super(target, click);
        this.handler = handler;
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
                Completable.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        doEvent();
                        LogUtil.d(TAG,"doEvent completed");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG,"click done ,wait web load success!");
                boolean ok = semaphore.tryAcquire(REDIRECT_WAIT_TIME, TimeUnit.SECONDS);
                if (!ok) {
                    handler.unRegisterRedirectListener(listener);
                    throw new RuntimeException("redirect time out!");
                } else {
                    LogUtil.d(TAG,"web load completed");
                    handler.unRegisterRedirectListener(listener);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

}
