package com.nicolls.ghostevent.ghost.event;


import android.os.HandlerThread;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class EventExecutor {
    private static final String TAG = "EventExecutor";
    private BlockingQueue<BaseEvent> eventBlockingQueue = new LinkedBlockingQueue<>();
    private HandlerThread delayThread;
    private Thread executeThread;
    private final AtomicBoolean cancelAtom = new AtomicBoolean(false);
    // 信号量，事件需要按顺序一个接一个的执行
    private final Semaphore semaphore = new Semaphore(1, true);

    public EventExecutor() {
        this.delayThread = new HandlerThread("EventExecutor background thread");
        this.delayThread.start();
        this.executeThread = new Thread(executeCmdTask);
        this.executeThread.start();
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

    private Runnable executeCmdTask = new Runnable() {
        @Override
        public void run() {
            try {
                // 一直等待直到有新的命令
                BaseEvent event;
                while (!cancelAtom.get() && (event = eventBlockingQueue.take()) != null) {
                    LogUtil.d(TAG, "semaphore acquire waiting!");
                    semaphore.acquire();
                    LogUtil.d(TAG, "exe event " + event.getName());
                    if (!cancelAtom.get()) {
                        executeEvent(event);
                    } else {
                        LogUtil.d(TAG, "exe fail because of shutdown!");
                        semaphore.release();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    private void executeEvent(final BaseEvent event) {
        Disposable disposable = event.exe(cancelAtom).observeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                if (cancelAtom.get()) {
                    LogUtil.d(TAG, "event call back ,executor have been shutdown!");
                    semaphore.release();
                    return;
                }
                LogUtil.d(TAG, "event " + event.getName() + " execute completed");
                Thread.sleep(Constant.EVENT_INTERVAL_TIME);
                LogUtil.d(TAG, "semaphore release");
                semaphore.release();
            }
        });
    }

    public void shutDown() {
        LogUtil.d(TAG, "shutDown");
        cancelAtom.set(true);
        eventBlockingQueue.clear();
    }

}
