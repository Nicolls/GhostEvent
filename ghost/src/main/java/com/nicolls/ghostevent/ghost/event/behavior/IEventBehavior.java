package com.nicolls.ghostevent.ghost.event.behavior;

import com.nicolls.ghostevent.ghost.event.BaseEvent;

public interface IEventBehavior<T> {
    T onStart();

    T onEnd();

    long getTimeOut();
}
