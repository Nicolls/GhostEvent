package com.nicolls.ghostevent.ghost.core;

import android.os.Handler;
import android.view.MotionEvent;

public interface IEventHandler {
    void doEvent(MotionEvent event);
    Handler getEventHandler();
    Handler getMainHandler();
}
