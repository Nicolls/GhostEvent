package com.nicolls.ghostevent.ghost.real;


import android.os.HandlerThread;

import com.nicolls.ghostevent.ghost.LogUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class EventExecutor {
    private static final String TAG = "EventExecutor";
    private static final int EVENT_INTERVAL_TIME=500;
    private BlockingQueue<BaseEvent> eventBlockingQueue = new LinkedBlockingQueue<>();
    private HandlerThread delayThread;
    private Thread executeThread;
    private boolean isRunExecutor = true;
    public EventExecutor() {
        this.delayThread = new HandlerThread("EventExecutor background thread");
        this.delayThread.start();
        this.executeThread = new Thread(executeCmdTask);
        this.executeThread.start();
    }

    public synchronized void enQueue(BaseEvent event) {
        LogUtil.d(TAG, "enQueue event: " + event.getId());
        try {
            eventBlockingQueue.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Runnable executeCmdTask=new Runnable() {
        @Override
        public void run() {
            try {
                // 一直等待直到有新的命令
                BaseEvent event;
                while (isRunExecutor && (event = eventBlockingQueue.take()) != null) {
                    CountDownLatch countDownLatch=new CountDownLatch(1);
                    LogUtil.d(TAG,"exe event");
                    executeEvent(event,countDownLatch);
                    LogUtil.d(TAG,"await event");
                    countDownLatch.await();
                    LogUtil.d(TAG,"event completed");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    private void executeEvent(final BaseEvent event,final CountDownLatch countDownLatch) {
        event.exe().observeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                LogUtil.d(TAG,"event call back");
                Thread.sleep(EVENT_INTERVAL_TIME);
                LogUtil.d(TAG,"count down");
                countDownLatch.countDown();
            }
        });
    }

    public void  shutDown(){
        isRunExecutor=false;
    }

}
