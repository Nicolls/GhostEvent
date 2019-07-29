package com.nicolls.ghostevent.ghost.core;


import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.CancelEvent;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EventExecutor {
    private static final String TAG = "EventExecutor";
    private BlockingQueue<BaseEvent> eventBlockingQueue = new LinkedBlockingQueue<>();
    private Thread executeThread;
    private volatile AtomicBoolean cancelAtom = new AtomicBoolean(false);
    private Disposable lastDisposable;
    // 信号量，事件需要按顺序一个接一个的执行
    private final Semaphore semaphore = new Semaphore(0, true);

    public interface ExecuteCallBack {
        void onSuccess(int eventId);

        void onFail(int eventId);

        void onTimeOut(int eventId);
    }

    private ExecuteCallBack executeCallBack;

    public EventExecutor() {
        init();
    }

    private void init() {
        this.executeThread = new Thread(executeEventTask);
        this.executeThread.start();
    }

    public void setExecuteCallBack(ExecuteCallBack callBack) {
        this.executeCallBack = callBack;
    }

    public synchronized void execute(List<BaseEvent> eventList) {
        for (BaseEvent event : eventList) {
            execute(event);
        }
    }

    public synchronized void execute(BaseEvent event) {
        if (cancelAtom.get()) {
            LogUtil.d(TAG, "execute cancel,have been shutdown!");
            return;
        }
        LogUtil.d(TAG, "execute event: " + event.getName());
        try {
            eventBlockingQueue.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Runnable executeEventTask = new Runnable() {
        @Override
        public void run() {
            try {
                // 一直等待直到有新的命令
                LogUtil.d(TAG, "executeEventTask start run");
                BaseEvent event;
                while (!cancelAtom.get() && (event = eventBlockingQueue.take()) != null) {

                    LogUtil.d(TAG, "execute event " + event.getName());
                    executeEvent(event);
                    long timeOut = event.getExecuteTimeOut();
                    LogUtil.d(TAG, event.getName() + " wait timeOut:" + timeOut);
                    boolean ok = semaphore.tryAcquire(timeOut, TimeUnit.MILLISECONDS);
                    if (!ok) {
                        LogUtil.d(TAG, "event " + event.getName() + " time out !");
                        executeCallBack.onTimeOut(event.getId());
                    }else {
                        LogUtil.d(TAG, "semaphore acquired");
                    }
                }
                LogUtil.d(TAG, "executeEventTask over");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    private void executeEvent(final BaseEvent event) {
        event.exe(cancelAtom).observeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.d(TAG, "onSubscribe");
                lastDisposable = d;
            }

            @Override
            public void onComplete() {
                if (cancelAtom.get()) {
                    LogUtil.d(TAG, "event " + event.getName()
                            + " completed ,but executor have been cancel!");
                    semaphore.release();
                    return;
                }
                LogUtil.d(TAG, "execute event " + event.getName() + " completed");
                executeCallBack.onSuccess(event.getId());
                LogUtil.d(TAG, "semaphore release");
                semaphore.release();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "execute event " + event.getName() + " fail!" + e);
                if (event.needRetry()) {
                    LogUtil.d(TAG, event.getName() + "need retry!");
                    executeEvent(event);
                    return;
                }
                // 只要出现事件错误，则停止
                if (lastDisposable != null) {
                    lastDisposable.dispose();
                }
                executeCallBack.onFail(event.getId());
                LogUtil.d(TAG, "semaphore release");
                semaphore.release();
            }
        });
    }

    public void shutDown() {
        LogUtil.d(TAG, "shutDown");
        cancelAtom.set(true);
        try {
            eventBlockingQueue.put(CancelEvent.instance);
        } catch (InterruptedException e) {
            LogUtil.e(TAG, "invoke shutdown exe cancel event error ", e);
            e.printStackTrace();
        }
    }

    public void retry() {
        shutDown();
        cancelAtom.set(false);
        executeThread = null;
        init();
    }

}
