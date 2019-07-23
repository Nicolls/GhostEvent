package com.nicolls.ghostevent.ghost.event;

import android.os.Handler;
import android.view.MotionEvent;

public interface IEventHandler {
    void doEvent(MotionEvent event);
    Handler getEventHandler();
    Handler getMainHandler();
}
