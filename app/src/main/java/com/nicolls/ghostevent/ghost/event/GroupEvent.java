package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class GroupEvent extends BaseEvent {
    private static final String TAG = "GroupEvent";
    private final Semaphore semaphore = new Semaphore(1, true);
    private List<BaseEvent> childList = new ArrayList<>();

    public GroupEvent() {
        this.name = TAG;
    }

    public GroupEvent(BaseEvent ... events) {
        this.name = TAG;
        for (BaseEvent event:events){
            childList.add(event);
        }
    }

    public void addEvent(BaseEvent event) {
        childList.add(event);
    }

    public void removeEvent(BaseEvent event) {
        childList.remove(event);
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
                for (BaseEvent event : childList) {
                    LogUtil.d(TAG, "start child event:" + event.getName());
                    if (cancel.get()) {
                        LogUtil.d(TAG, "start child cancel!");
                        break;
                    }
                    LogUtil.d(TAG, "child acquire");
                    semaphore.acquire();
                    LogUtil.d(TAG, "execute child event");
                    Disposable disposable = event.exe(cancel).observeOn(Schedulers.io()).subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            LogUtil.d(TAG, "child event call back ");
                            if (cancel.get()) {
                                LogUtil.d(TAG, "child event call back cancel return!");
                                semaphore.release();
                                return;
                            }
                            Thread.sleep(Constant.EVENT_INTERVAL_TIME);
                            LogUtil.d(TAG, "child event completed");
                            semaphore.release();
                        }
                    });
                }
            }
        }).subscribeOn(Schedulers.io());
    }

}
