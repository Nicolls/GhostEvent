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
    private static final long EVENT_EXECUTE_TIME_OUT_EXTEND = 100; // 毫秒
    private final Semaphore semaphore = new Semaphore(0, true);
    private List<BaseEvent> childList = new ArrayList<>();
    private long timeOut = DEFAULT_EVENT_EXECUTE_TIMEOUT;
    private ITarget target;
    private EventExecutor.ExecuteCallBack executeCallBack;

    public GroupEvent(ITarget target, EventExecutor.ExecuteCallBack executeCallBack) {
        this(target, executeCallBack, new BaseEvent[]{});
    }

    public GroupEvent(ITarget target, EventExecutor.ExecuteCallBack executeCallBack, BaseEvent... events) {
        super(target);
        this.target = target;
        this.executeCallBack = executeCallBack;
        this.setName(TAG);
        if (events != null) {
            for (BaseEvent event : events) {
                childList.add(event);
                timeOut += event.getExecuteTimeOut();
                timeOut += EVENT_EXECUTE_TIME_OUT_EXTEND;
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
                        LogUtil.d(TAG, "start child executor have been cancel!");
                        break;
                    }
                    LogUtil.d(TAG, "execute child event");
                    boolean isOk=onChildStart(event);
                    if(!isOk){
                        break;
                    }
                    event.exe(cancel).observeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            LogUtil.d(TAG, "onSubscribe");
                        }

                        @Override
                        public void onComplete() {
                            LogUtil.d(TAG, "child event completed,release semaphore");
                            onChildCompleted(event);
                            semaphore.release();
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.e(TAG, "group event child completed error", e);
                            onChildFail(event);
                            if (event.needRetry()) {
                                LogUtil.d(TAG, "onError, event need retry again");
                                event.exe(cancel);
                                return;
                            }
                            if (executeCallBack != null) {
                                executeCallBack.onFail(event.getId());
                            }
                        }
                    });
                    LogUtil.d(TAG, "child try acquire");
                    boolean ok = semaphore.tryAcquire(event.getExecuteTimeOut()
                            + EVENT_EXECUTE_TIME_OUT_EXTEND, TimeUnit.MILLISECONDS);
                    if (!ok) {
                        LogUtil.d(TAG, "child acquired time out");
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
        return timeOut;
    }

    public boolean onChildStart(BaseEvent event){
        LogUtil.d(TAG,"onChildStart");
        return true;
    }

    public void onChildCompleted(BaseEvent event){
        LogUtil.d(TAG,"onChildCompleted");

    }

    public void onChildFail(BaseEvent event){
        LogUtil.d(TAG,"onChildFail");

    }

    @Override
    public String toString() {
        return "GroupEvent{" +
                "childList=" + Arrays.toString(childList.toArray()) +
                '}';
    }
}
