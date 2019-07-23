package com.nicolls.ghostevent.ghost.event;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

public class ViewEventHandler implements IEventHandler {

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
        view.dispatchTouchEvent(event);
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
