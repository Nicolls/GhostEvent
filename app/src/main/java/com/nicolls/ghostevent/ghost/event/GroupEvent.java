package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class GroupEvent extends BaseEvent {
    private static final String TAG = "GroupEvent";
    // 使用这个延长来确保，执行child event时的超时判断一定大于，child event本身的超时判断
    private final Semaphore semaphore = new Semaphore(0, true);
    private List<BaseEvent> childList = new ArrayList<>();
    private long timeOut = 0;
    private EventExecutor.ExecuteCallBack executeCallBack;

    public GroupEvent(ITarget target, EventExecutor.ExecuteCallBack executeCallBack) {
        this(target, executeCallBack, new BaseEvent[]{});
    }

    public GroupEvent(ITarget target, EventExecutor.ExecuteCallBack executeCallBack, BaseEvent... events) {
        this(target, executeCallBack, Arrays.asList(events));
    }

    public GroupEvent(ITarget target, EventExecutor.ExecuteCallBack executeCallBack, List<BaseEvent> list) {
        super(target);
        this.executeCallBack = executeCallBack;
        this.setName(TAG);
        if (list != null) {
            for (BaseEvent event : list) {
                childList.add(event);
                timeOut += event.getExecuteTimeOut();
            }
        }
        initTimeOut();
    }

    public void addEvent(BaseEvent event) {
        childList.add(event);
        initTimeOut();
    }

    public void addEvent(List<BaseEvent> events) {
        childList.addAll(events);
        initTimeOut();
    }

    private void initTimeOut() {
        timeOut = 0;
        if (childList != null) {
            for (BaseEvent event : childList) {
                timeOut += event.getExecuteTimeOut();
            }
        }
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
                    LogUtil.d(TAG, "execute child event:" + event.getName());
                    if (cancel.get()) {
                        LogUtil.d(TAG, "execute child executor have been cancel!");
                        break;
                    }
                    boolean isOk = onChildStart(event);
                    if (!isOk) {
                        break;
                    }
                    event.exe(cancel).observeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            LogUtil.d(TAG, "onSubscribe " + event.getName());
                        }

                        @Override
                        public void onComplete() {
                            LogUtil.d(TAG, "child event completed " + event.getName() + ",release semaphore");
                            onChildCompleted(event);
                            semaphore.release();
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.e(TAG, "group execute child event error " + event.getName(), e);
                            onChildFail(event);
                            if (event.needRetry()) {
                                LogUtil.d(TAG, "onError, event " + event.getName() + " need retry again");
                                event.exe(cancel);
                                return;
                            }
                            if (executeCallBack != null) {
                                executeCallBack.onFail(event.getId());
                            }
                            LogUtil.d(TAG, "semaphore release");
                            semaphore.release();
                        }
                    });
                    long timeOut = event.getExecuteTimeOut();
                    LogUtil.d(TAG, "next child try acquire wait current child. timeOut:" + timeOut);
                    boolean ok = semaphore.tryAcquire(timeOut, TimeUnit.MILLISECONDS);
                    if (!ok) {
                        LogUtil.d(TAG, "child acquired time out!");
                        executeCallBack.onTimeOut(event.getId());
                    } else if (executeCallBack != null) {
                        LogUtil.d(TAG, "child acquired semaphore");
                    }
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public long getExecuteTimeOut() {
        return timeOut + getExtendsTime();
    }

    public boolean onChildStart(BaseEvent event) {
        LogUtil.d(TAG, "onChildStart");
        return true;
    }

    public void onChildCompleted(BaseEvent event) {
        LogUtil.d(TAG, "onChildCompleted");

    }

    public void onChildFail(BaseEvent event) {
        LogUtil.d(TAG, "onChildFail");

    }

    @Override
    public String toString() {
        return "GroupEvent{" +
                "childList=" + Arrays.toString(childList.toArray()) +
                '}';
    }
}
