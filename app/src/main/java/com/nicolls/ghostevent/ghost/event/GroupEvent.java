package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class GroupEvent extends BaseEvent {
    private static final String TAG = "GroupEvent";
    private final Semaphore semaphore = new Semaphore(1, true);
    private List<BaseEvent> childList = new ArrayList<>();
    private long eventIntervalTime = Constants.EVENT_INTERVAL_TIME;
    private ITarget target;

    public GroupEvent(ITarget target) {
        this(target, new BaseEvent[]{});
    }

    public GroupEvent(ITarget target, BaseEvent... events) {
        this(target, Constants.EVENT_INTERVAL_TIME, events);
    }

    public GroupEvent(ITarget target, long eventIntervalTime, BaseEvent... events) {
        super(target);
        this.target = target;
        this.setName(TAG);
        this.eventIntervalTime = eventIntervalTime;
        if (events != null) {
            for (BaseEvent event : events) {
                childList.add(event);
            }
        }
    }

    public void addEvent(BaseEvent event) {
        childList.add(event);
    }

    public void addEvent(List<BaseEvent> events) {
        childList.addAll(events);
    }

    public void removeEvent(BaseEvent event) {
        childList.remove(event);
    }

    public void removeAllEvents() {
        childList.clear();
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
                for (final BaseEvent event : childList) {
                    LogUtil.d(TAG, "start child event:" + event.getName());
                    if (cancel.get()) {
                        LogUtil.d(TAG, "start child cancel!");
                        break;
                    }
                    LogUtil.d(TAG, "child acquire");
                    semaphore.acquire();
                    LogUtil.d(TAG, "execute child event");
                    event.exe(cancel).observeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            LogUtil.d(TAG, "onSubscribe");
                        }

                        @Override
                        public void onComplete() {
                            LogUtil.d(TAG, "child event call back ");
                            if (cancel.get()) {
                                LogUtil.d(TAG, "child event call back cancel return!");
                                semaphore.release();
                                return;
                            }
                            try {
                                Thread.sleep(eventIntervalTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            LogUtil.d(TAG, "child event completed");
                            semaphore.release();
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (event.needRetry()) {
                                event.exe(cancel);
                            }
                            LogUtil.e(TAG, "group event child completed error", e);
                        }
                    });
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public String toString() {
        return "GroupEvent{" +
                "childList=" + Arrays.toString(childList.toArray()) +
                ", eventIntervalTime=" + eventIntervalTime +
                '}';
    }
}
