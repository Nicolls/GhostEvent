package com.nicolls.ghostevent.ghost.real;

import android.os.Handler;
import android.os.Looper;

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

    protected void sleepTimes(long times) {
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
