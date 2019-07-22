package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;

/**
 * author:mengjiankang
 * date:2018/11/25
 * <p>
 * </p>
 */
public abstract class BaseEvent {
    private static final String TAG = "BaseEvent";
    protected String name;

    public abstract Completable exe(AtomicBoolean cancel);

    public int getId() {
        return hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean needRetry() {
        return false;
    }

    protected void sleepTimes(long times) {
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
