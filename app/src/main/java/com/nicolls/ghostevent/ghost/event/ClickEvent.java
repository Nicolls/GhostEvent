package com.nicolls.ghostevent.ghost.event;

import android.os.SystemClock;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.event.provider.EventParamsProvider;
import com.nicolls.ghostevent.ghost.utils.Constants;
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
    // 毫秒
    public static final int CLICK_INTERVAL_TIME = 100;
    private ITarget target;
    private EventParamsProvider<TouchPoint> provider;

    public ClickEvent(ClickEvent clickEvent) {
        super(clickEvent.target);
        this.target = clickEvent.target;
        this.provider = clickEvent.provider;
        this.setName(TAG);
    }

    public ClickEvent(ITarget target, TouchPoint touchPoint) {
        super(target);
        this.target = target;
        this.provider = new EventParamsProvider<TouchPoint>() {
            @Override
            public TouchPoint getParams() {
                return touchPoint;
            }

            @Override
            public String getName() {
                return "ClickEventParamsProvider";
            }
        };
        this.setName(TAG);
    }

    public ClickEvent(ITarget target, EventParamsProvider<TouchPoint> provider) {
        super(target);
        this.target = target;
        this.provider = provider;
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
        TouchPoint touchPoint = provider.getParams();
        if (touchPoint == null) {
            LogUtil.w(TAG, "touch point null!");
            return;
        }
        // down
        final long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = mockMotionEvent(downTime, downTime, MotionEvent.ACTION_DOWN, touchPoint.point.x, touchPoint.point.y);
        target.doEvent(downEvent);
        // interval
        long clickSpentTime = touchPoint.spentTime;
        if (clickSpentTime <= 0) {
            clickSpentTime = CLICK_INTERVAL_TIME;
        }
        sleepTimes(clickSpentTime);
        // up
        long upTime = SystemClock.uptimeMillis();
        MotionEvent upEvent = mockMotionEvent(downTime, upTime, MotionEvent.ACTION_UP, touchPoint.point.x, touchPoint.point.y);
        target.doEvent(upEvent);
    }

    public long getExecuteTimeOut() {
        return getExtendsTime() + CLICK_INTERVAL_TIME;
    }

    ITarget getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "ClickEvent{" +
                "touchPoint=" + (provider.getParams() == null ? "null" : provider.getParams().toString()) +
                ", name='" + getName() + '\'' +
                '}';
    }

}
