package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class WebParseEvent extends BaseEvent {
    private static final String TAG = "WebParseEvent";

    private final Semaphore semaphore = new Semaphore(0, true);
    private IWebTarget target;
    private IWebParser webParser;

    public WebParseEvent(IWebTarget target, IWebParser webParser) {
        super(target);
        this.target = target;
        this.webParser = webParser;
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
                LogUtil.d(TAG, "start to parse!");
                Completable.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        webParser.parse(target, semaphore);
                        LogUtil.d(TAG, "do parse");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "exe parse subscribe ");
                boolean ok = semaphore.tryAcquire(getExecuteTimeOut(), TimeUnit.MILLISECONDS);
                if (!ok) {
                    LogUtil.w(TAG, "parse page time out!");
                    throw new RuntimeException("parse page time out!");
                } else {
                    LogUtil.d(TAG, "parse page completed");
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public long getExecuteTimeOut() {
        return webParser.getParsedDelay() + getExtendsTime();
    }
}
