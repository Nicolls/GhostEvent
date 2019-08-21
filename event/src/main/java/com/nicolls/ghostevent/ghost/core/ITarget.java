package com.nicolls.ghostevent.ghost.core;

import android.os.Handler;
import android.view.MotionEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public interface ITarget {
    Handler getMainHandler();
    Handler getEventHandler();
    ExecutorService getEventTaskPool();
    void doEvent(MotionEvent event);
}
