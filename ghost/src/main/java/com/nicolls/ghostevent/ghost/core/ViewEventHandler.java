package com.nicolls.ghostevent.ghost.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

public class ViewEventHandler implements IEventHandler {
    private static final String TAG="ViewEventHandler";
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private HandlerThread eventThread;
    private Handler eventHandler;

    private View view;

    public ViewEventHandler(View view) {
        this.view = view;
        eventThread = new HandlerThread("Event Spent Time Wait Thread");
        eventThread.start();
        eventHandler = new Handler(eventThread.getLooper());
    }

    @Override
    public void doEvent(MotionEvent event) {
        LogUtil.d(TAG,"doEvent");
        view.dispatchTouchEvent(event);
    }

    @Override
    public void quit() {
        LogUtil.d(TAG,"quit");
        eventThread.quit();
    }

    @Override
    public Handler getEventHandler() {
        return eventHandler;
    }

    @Override
    public Handler getMainHandler() {
        return mainHandler;
    }
}
