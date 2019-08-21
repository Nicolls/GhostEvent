package com.nicolls.ghostevent.ghost.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewEventHandler implements IEventHandler {
    private static final String TAG = "ViewEventHandler";
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private HandlerThread eventThread;
    private Handler eventHandler;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private View view;

    public ViewEventHandler(View view) {
        this.view = view;
        eventThread = new HandlerThread("ViewEventThread");
        eventThread.start();
        eventHandler = new Handler(eventThread.getLooper());
    }

    @Override
    public void doEvent(MotionEvent event) {
        LogUtil.d(TAG, "doEvent");
        view.dispatchTouchEvent(event);
    }

    @Override
    public void quit() {
        LogUtil.d(TAG, "quit");
        try {
            eventThread.quit();
            executor.shutdown();
        }catch (Exception e){
            LogUtil.e(TAG,"quit error ",e);
        }
    }

    @Override
    public Handler getEventHandler() {
        return eventHandler;
    }

    @Override
    public Handler getMainHandler() {
        return mainHandler;
    }

    @Override
    public ExecutorService getEventTaskPool() {
        return executor;
    }
}
