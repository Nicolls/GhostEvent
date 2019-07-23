package com.nicolls.ghostevent.ghost.event;

import android.view.MotionEvent;

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
    private String name;
    private ITarget target;

    public BaseEvent(ITarget target) {
        this.target = target;
    }

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

    protected MotionEvent mockMotionEvent(long downTime, long eventTime, int action, float x, float y) {
        return MotionEvent.obtain(downTime, eventTime, action, x, y, 0.7f, 0.8f, 0, 1.0f, 1.0f, 4, 0);
    }

}
