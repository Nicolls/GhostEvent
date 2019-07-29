package com.nicolls.ghostevent.ghost.event.provider;

public abstract class EventParamsProvider<T> {
    public abstract T getParams();
    public abstract String getName();
}
