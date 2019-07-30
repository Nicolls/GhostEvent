package com.nicolls.ghostevent.ghost.event;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * author:mengjiankang
 * date:2018/11/25
 * <p>
 * </p>
 */
public class SlideEvent extends BaseEvent {
    private static final String TAG = "SlideEvent";
    /**
     * 每一个滑动事件需要触发的move数
     */
    private static final int DEFAULT_SLIDE_MOVE_SIZE = 8;
    /**
     * 每一个滑动事件触发的move所占用的时间
     */
    public static final long DEFAULT_INTERVAL_SLIDE_MOVE_TIME_UNIT = 16;

    public static final long WAIT_TIME_OUT_MOVE_UNIT = 10;

    public static final long WAIT_TIME_OUT_SLIDE = 300;


    private int from = 0;
    private int to = 0;
    private int x = 0;
    private List<TouchPoint> moves = new ArrayList<>();
    private ITarget target;

    public enum Direction {
        UP, DOWN
    }

    public SlideEvent(ITarget target, final Direction direct) {
        super(target);
        this.target = target;
        this.setName(TAG);
        LogUtil.i(TAG, "slide direct:" + direct);
        int distance = GhostUtils.displayHeight / 3;
        int yStart = 0;
        int yEnd = 0;
        switch (direct) {
            case UP:
                x = GhostUtils.displayWidth / 2;
                yStart = GhostUtils.displayHeight - GhostUtils.displayHeight / 4;
                yEnd = yStart - distance;
                break;
            case DOWN:
                x = GhostUtils.displayWidth / 3;
                yStart = GhostUtils.displayHeight / 4;
                yEnd = yStart + distance;
                break;

        }
        this.from = yStart;
        this.to = yEnd;
        calculateMoveEvent();
    }

    private void calculateMoveEvent() {
        moves.clear();
        float vertical = to - from;
        int flag = vertical > 0 ? 1 : -1;
        float verticalDistance = Math.abs(vertical);
        float distanceUnit = verticalDistance / DEFAULT_SLIDE_MOVE_SIZE;
        for (int i = 0; i < DEFAULT_SLIDE_MOVE_SIZE; i++) {
            float moveY = from + distanceUnit * i * flag;
            TouchPoint move = new TouchPoint(new PointF(x, moveY), DEFAULT_INTERVAL_SLIDE_MOVE_TIME_UNIT);
            moves.add(move);
        }
        LogUtil.d(TAG, "calculate move size:" + moves.size());
    }

    @Override
    public Completable exe(final AtomicBoolean cancel) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    return;
                }
                doEvent(cancel);
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public long getExecuteTimeOut() {
        return moves.size() * DEFAULT_INTERVAL_SLIDE_MOVE_TIME_UNIT + getExtendsTime();
    }

    protected void doEvent(final AtomicBoolean cancel) {
        if (cancel.get()) {
            LogUtil.d(TAG, "event cancel");
            return;
        }
        long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = mockMotionEvent(downTime, downTime, MotionEvent.ACTION_DOWN, x, from);
        LogUtil.d(TAG, "doEvent down:" + x + " -" + from);
        target.doEvent(downEvent);
        final Semaphore semaphore = new Semaphore(0, true);
        for (final TouchPoint move : moves) {
            if (cancel.get()) {
                LogUtil.d(TAG, "event cancel in move ,send up event and cancel!");
                long upTime = SystemClock.uptimeMillis();
                MotionEvent upEvent = mockMotionEvent(downTime, upTime, MotionEvent.ACTION_UP, x, to);
                target.doEvent(upEvent);
                return;
            }
            LogUtil.d(TAG, "start move event");
            long moveEventTime = SystemClock.uptimeMillis();
            MotionEvent moveEvent = mockMotionEvent(downTime, moveEventTime, MotionEvent.ACTION_MOVE, move.point.x, move.point.y);
            target.doEvent(moveEvent);
            LogUtil.d(TAG, "start post delay:" + move.spentTime);

            target.getEventHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "semaphore release ");
                    semaphore.release();
                }
            }, move.spentTime);
            LogUtil.d(TAG, "start acquire semaphore");
            try {
                boolean ok = semaphore.tryAcquire(move.spentTime + WAIT_TIME_OUT_MOVE_UNIT, TimeUnit.MILLISECONDS);
                if (!ok) {
                    LogUtil.d(TAG, "semaphore acquire time out");
                } else {
                    LogUtil.d(TAG, "semaphore acquired");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long moveEventTime = SystemClock.uptimeMillis();
        MotionEvent moveEvent = mockMotionEvent(downTime, moveEventTime, MotionEvent.ACTION_MOVE, x, to);
        target.doEvent(moveEvent);
        LogUtil.d(TAG, "up event");
        long upTime = SystemClock.uptimeMillis();
        LogUtil.d(TAG, "doEvent up:" + x + " -" + to);
        MotionEvent upEvent = mockMotionEvent(downTime, upTime, MotionEvent.ACTION_UP, x, to);
        target.doEvent(upEvent);
    }

}
