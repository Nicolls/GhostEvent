package com.nicolls.ghostevent.ghost.core;


import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.CancelEvent;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventExecutor {
    private static final String TAG = "EventExecutor";
    private BlockingQueue<BaseEvent> eventBlockingQueue = new LinkedBlockingQueue<>();
    private Thread executeThread;
    private IEventHandler eventHandler;
    private volatile AtomicBoolean cancelAtom = new AtomicBoolean(false);
    // 事件信号量，事件需要按顺序一个接一个的执行
    private final Semaphore semaphore = new Semaphore(0, true);

    // reset信号量，task运行完成后，才算reset完成
    private Semaphore resetSemaphore = new Semaphore(0, true);

    public interface ExecuteCallBack {
        void onSuccess(BaseEvent event);

        void onFail(BaseEvent event);

        void onTimeOut(BaseEvent event);
    }

    private ExecuteCallBack executeCallBack;

    public EventExecutor(final IEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        init();
    }

    private void init() {
        LogUtil.d(TAG, "init EventExecutor");
        try {
            eventBlockingQueue.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, "clear eventBlockingQueue error ", e);
        }
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
            LogUtil.d(TAG, "put event to queue cancel,executor have been shutdown!");
            return;
        }
        LogUtil.d(TAG, "enqueue event  " + event.getName());
        try {
            eventBlockingQueue.put(event);
            LogUtil.d(TAG, "enqueue done");
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
                        executeCallBack.onTimeOut(event);
                    } else {
                        LogUtil.d(TAG, "semaphore acquired");
                    }
                }
                eventBlockingQueue.clear();
                LogUtil.d(TAG, "executeEventTask over");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (resetSemaphore != null) {
                LogUtil.d(TAG, "release resetSemaphore");
                resetSemaphore.release();
            }
        }
    };


    private void executeEvent(final BaseEvent event) {
        event.exe(cancelAtom, new BaseEvent.EventCallBack() {
            @Override
            public void onComplete() {
                if (cancelAtom.get()) {
                    LogUtil.d(TAG, "event " + event.getName()
                            + " completed ,but executor have been cancel!");
                    semaphore.release();
                    return;
                }
                LogUtil.d(TAG, "execute event " + event.getName() + " completed");
                executeCallBack.onSuccess(event);
                LogUtil.d(TAG, "semaphore release");
                semaphore.release();
            }

            @Override
            public void onFail(Exception e) {
                LogUtil.e(TAG, "execute event " + event.getName() + " fail!", e);
                // 只要出现事件错误，则停止
                executeCallBack.onFail(event);
                LogUtil.d(TAG, "semaphore release");
                semaphore.release();
            }
        });
    }

    public void shutDown(final BaseEvent.EventCallBack eventCallBack) {
        LogUtil.d(TAG, "shutDown");
        ExecutorService executorService=eventHandler.getEventTaskPool();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "do shutDown set cancelAtom true and put CancelEvent");
                if (!cancelAtom.get()) {
                    LogUtil.d(TAG, "start shutDown");
                    cancelAtom.set(true);
                    try {
                        boolean ok = eventBlockingQueue.offer(CancelEvent.instance, CancelEvent.instance.getExecuteTimeOut(), TimeUnit.MILLISECONDS);
                        LogUtil.d(TAG, "offer cancel event ok:" + ok);
                        if (!ok) {
                            resetSemaphore.release();
                        }
                    } catch (InterruptedException e) {
                        LogUtil.e(TAG, "invoke shutdown exe cancel event error ", e);
                        e.printStackTrace();
                    }
                }
                if (eventCallBack != null) {
                    eventCallBack.onComplete();
                }
            }
        });
    }

    public void reset(final BaseEvent.EventCallBack eventCallBack) {
        eventHandler.getEventTaskPool().execute(new Runnable() {
            @Override
            public void run() {
                resetSemaphore = new Semaphore(0, true);
                shutDown(BaseEvent.EventCallBack.defaultCallBack);

                LogUtil.d(TAG, "reset tryAcquire resetSemaphore");
                try {
                    boolean isOK = resetSemaphore.tryAcquire(CancelEvent.instance.getExecuteTimeOut()
                            + Constants.DEFAULT_EVENT_EXECUTE_TIMEOUT_EXTEND, TimeUnit.MILLISECONDS);
                    LogUtil.d(TAG, "resetSemaphore Acquired " + isOK + ", reset completed ");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                eventBlockingQueue.clear();
                cancelAtom.set(false);
                init();
                if (eventCallBack != null) {
                    eventCallBack.onComplete();
                }
            }
        });
    }

}
