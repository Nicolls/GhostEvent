package com.nicolls.ghostevent.ghost.core;

import android.os.Handler;
import android.view.MotionEvent;

public interface ITarget {
    Handler getMainHandler();
    Handler getEventHandler();
    void doEvent(MotionEvent event);
}
