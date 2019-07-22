package com.nicolls.ghostevent.ghost.event;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

/**
 * author:mengjiankang
 * date:2018/11/25
 * <p>
 * </p>
 */
public class ClickEvent extends BaseEvent {
    private static final String TAG = "ClickEvent";
    static final int INTERVAL_TIME_CLICK = 100;
    private float x;
    private float y;
    private View view;

    public ClickEvent(View view, float x, float y) {
        this.view = view;
        this.x = x;
        this.y = y;
        this.name = TAG;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public Completable exe(final AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    return;
                }
                doEvent();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    protected void doEvent() {
        long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, getX(), getY(), 0);
        view.dispatchTouchEvent(downEvent);
        sleepTimes(INTERVAL_TIME_CLICK);
        long upTime = SystemClock.uptimeMillis();
        MotionEvent upEvent = MotionEvent.obtain(downTime, upTime, MotionEvent.ACTION_UP, getX(), getY(), 0);
        view.dispatchTouchEvent(upEvent);
    }

    public static class Builder {
        private ClickEvent clickEvent;

        public Builder(View view) {
            clickEvent = new ClickEvent(view, 0, 0);
            clickEvent.x = 0;
            clickEvent.y = 0;

        }

        public static ClickEvent copy(ClickEvent clickEvent) {
            ClickEvent copyEvent = new ClickEvent(clickEvent.view, clickEvent.x, clickEvent.y);
            return copyEvent;
        }

        public Builder setLocation(float x, float y) {
            clickEvent.x = x;
            clickEvent.y = y;
            return this;
        }

        public Builder setLocationWithRatio(float xRatio, float yRatio) {
            xRatio = xRatio < 0 ? 0 : xRatio;
            xRatio = xRatio > 1 ? 1 : xRatio;

            yRatio = yRatio < 0 ? 0 : yRatio;
            yRatio = yRatio > 1 ? 1 : yRatio;

            clickEvent.x = GhostUtils.displayWidth * xRatio;
            clickEvent.y = GhostUtils.displayHeight * yRatio;
            return this;
        }

        public ClickEvent create() {
            return clickEvent;
        }
    }

    @Override
    public String toString() {
        return getName() + "{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }


}
