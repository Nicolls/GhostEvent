package com.nicolls.ghostevent.ghost.event;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
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
    public static final int CLICK_INTERVAL_TIME = 100;
    private TouchPoint touchPoint;
    private ITarget target;

    public ClickEvent(ITarget target) {
        super(target);
        this.target = target;
    }

    public ClickEvent(ITarget target, TouchPoint touchPoint) {
        super(target);
        this.target = target;
        if (touchPoint == null) {
            touchPoint = new TouchPoint(new PointF(0, 0), CLICK_INTERVAL_TIME);
        }
        this.touchPoint = touchPoint;
        this.setName(TAG);
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
        final long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = mockMotionEvent(downTime, downTime, MotionEvent.ACTION_DOWN, touchPoint.point.x, touchPoint.point.y);
        target.doEvent(downEvent);
        long clickSpentTime = touchPoint.spentTime;
        if (clickSpentTime <= 0) {
            clickSpentTime = CLICK_INTERVAL_TIME;
        }
        target.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                long upTime = SystemClock.uptimeMillis();
                MotionEvent upEvent = mockMotionEvent(downTime, upTime, MotionEvent.ACTION_UP, touchPoint.point.x, touchPoint.point.y);
                target.doEvent(upEvent);
            }
        }, clickSpentTime);
    }

    public static class Builder {
        private ClickEvent clickEvent;

        public Builder(ITarget target) {
            clickEvent = new ClickEvent(target, new TouchPoint(new PointF(0, 0), CLICK_INTERVAL_TIME));
        }

        public static ClickEvent copy(ClickEvent clickEvent) {
            ClickEvent copyEvent = new ClickEvent(clickEvent.target, clickEvent.touchPoint);
            return copyEvent;
        }

        public Builder setLocation(float x, float y) {
            clickEvent.touchPoint.point.x = x;
            clickEvent.touchPoint.point.y = x;
            return this;
        }

        public Builder setLocationWithRatio(float xRatio, float yRatio) {
            xRatio = xRatio < 0 ? 0 : xRatio;
            xRatio = xRatio > 1 ? 1 : xRatio;

            yRatio = yRatio < 0 ? 0 : yRatio;
            yRatio = yRatio > 1 ? 1 : yRatio;

            clickEvent.touchPoint = new TouchPoint(new PointF(GhostUtils.displayWidth * xRatio,
                    GhostUtils.displayHeight * yRatio), CLICK_INTERVAL_TIME);
            return this;
        }

        public ClickEvent create() {
            return clickEvent;
        }
    }

    @Override
    public String toString() {
        return "ClickEvent{" +
                "touchPoint=" + touchPoint.toString() +
                ", name='" + getName() + '\'' +
                '}';
    }
}
