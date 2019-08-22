package com.nicolls.ghostevent.ghost.event.behavior;

import java.util.concurrent.atomic.AtomicBoolean;

public interface IEventBehavior<T> {
    T onStart(AtomicBoolean cancel);

    T onEnd(AtomicBoolean cancel);

    long getTimeOut();
}
