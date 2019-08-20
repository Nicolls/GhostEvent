package com.nicolls.ghostevent.ghost.event;

import android.os.SystemClock;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.event.behavior.IEventBehavior;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * author:mengjiankang
 * date:2018/11/25
 * <p>
 * </p>
 */
public class ClickEvent extends BaseEvent {
    private static final String TAG = "ClickEvent";
    // 毫秒
    public static final int CLICK_INTERVAL_TIME = 160;
    private ITarget target;
    private IEventBehavior eventBehavior;
    protected TouchPoint touchPoint;

    public ClickEvent(ClickEvent clickEvent) {
        this.target = clickEvent.target;
        this.eventBehavior = clickEvent.eventBehavior;
        this.touchPoint = clickEvent.touchPoint;
    }

    public ClickEvent(ITarget target, TouchPoint touchPoint) {
        this(target, touchPoint, null);
    }

    public ClickEvent(ITarget target, TouchPoint touchPoint, IEventBehavior eventBehavior) {
        this.target = target;
        this.touchPoint = touchPoint;
        this.eventBehavior = eventBehavior;
    }

    @Override
    public Completable exe(final AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (eventBehavior != null) {
                    eventBehavior.onStart(cancel);
                }
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    return;
                }
                target.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        doEvent();
                    }
                });
                if (eventBehavior != null) {
                    eventBehavior.onEnd(cancel);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    protected void doEvent() {
        if (touchPoint == null) {
            LogUtil.w(TAG, "touch point null!");
            return;
        }
        // down
        final long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = mockMotionEvent(downTime, downTime, MotionEvent.ACTION_DOWN, touchPoint.point.x, touchPoint.point.y);
        target.doEvent(downEvent);
        // interval
        long clickSpentTime = CLICK_INTERVAL_TIME;
        sleepTimes(clickSpentTime / 2);
        // move
        long moveTime = SystemClock.uptimeMillis();
        MotionEvent moveEvent = mockMotionEvent(downTime, moveTime, MotionEvent.ACTION_MOVE, touchPoint.point.x, touchPoint.point.y);
        target.doEvent(moveEvent);
        sleepTimes(clickSpentTime / 2);
        // up
        long upTime = SystemClock.uptimeMillis();
        MotionEvent upEvent = mockMotionEvent(downTime, upTime, MotionEvent.ACTION_UP, touchPoint.point.x, touchPoint.point.y);
        target.doEvent(upEvent);
    }

    public long getExecuteTimeOut() {
        return getExtendsTime() + CLICK_INTERVAL_TIME + (eventBehavior == null ? 0 : eventBehavior.getTimeOut());
    }

    ITarget getTarget() {
        return target;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public String getDescribe() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("touchPoint", (touchPoint == null ? "null" : touchPoint.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
