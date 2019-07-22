package com.nicolls.ghostevent.ghost.event;

import io.reactivex.Completable;

/**
 * author:mengjiankang
 * date:2018/11/25
 * <p>
 * </p>
 */
public abstract class BaseEvent {

    public abstract Completable exe();

    public int getId() {
        return hashCode();
    }

    public abstract String getName();

    protected void sleepTimes(long times) {
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
