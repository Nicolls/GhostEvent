package com.nicolls.ghostevent.ghost.event.behavior;

import java.util.concurrent.atomic.AtomicBoolean;

public interface IEventBehavior<T> {
    T onStart(AtomicBoolean cancel);

    void onEnd(AtomicBoolean cancel);

    long getTimeOut();
}
