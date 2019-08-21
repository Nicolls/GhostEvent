package com.nicolls.ghostevent.ghost.core;

import android.os.Handler;
import android.view.MotionEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public interface IEventHandler {
    void doEvent(MotionEvent event);
    void quit();
    Handler getEventHandler();
    Handler getMainHandler();
    ExecutorService getEventTaskPool();
}
