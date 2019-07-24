package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.JsParser;
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
    private static final long PARSE_WAIT_TIME = 6; // 秒
    private final Semaphore semaphore = new Semaphore(0, true);
    private IWebTarget target;

    public WebParseEvent(IWebTarget target) {
        super(target);
        this.target = target;
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
                        IWebParser parser = new JsParser(target, semaphore);
                        parser.parse();
                        LogUtil.d(TAG, "do parse");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "exe parse subscribe ");
                boolean ok = semaphore.tryAcquire(PARSE_WAIT_TIME, TimeUnit.SECONDS);
                if (!ok) {
                    LogUtil.w(TAG, "parse page time out!");
                } else {
                    LogUtil.d(TAG, "parse page completed");
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
