package com.nicolls.ghostevent.ghost.event;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.ITarget;
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

    /**
     * 每一个滑动最小的延迟时间
     */
    public static final long MIN_INTERVAL_SLIDE_MOVE_TIME_UNIT = 6;

    /**
     * 每一个滑动最大的延迟时间
     */
    public static final long MAX_INTERVAL_SLIDE_MOVE_TIME_UNIT = 30;

    /**
     * 10 毫秒
     */
    private static final long TIME_MOVE_SPACE_MILLISECONDS = 20;

    private TouchPoint from = new TouchPoint(new PointF(0, 0), TouchPoint.INTERVAL_DOWN_UP_TIME);
    private TouchPoint to = new TouchPoint(new PointF(0, 0), TouchPoint.INTERVAL_DOWN_UP_TIME);
    private List<TouchPoint> moves = new ArrayList<>();
    private ITarget target;
    private long timeOut = 0;

    public enum Direction {
        LEFT, RIGHT, TOP, BOTTOM
    }

    public SlideEvent(ITarget target, final Direction direct) {
        super(target);
        this.target = target;
        this.setName(TAG);
        LogUtil.i(TAG, "slide direct:" + direct);
        int distance = 0;
        switch (direct) {
            case TOP:
            case BOTTOM:
                distance = GhostUtils.displayHeight / 3;
                break;
            case LEFT:
            case RIGHT:
                distance = GhostUtils.displayHeight / 3;
                break;
        }
        int centerX = GhostUtils.displayWidth / 2;
        int centerY = GhostUtils.displayHeight / 2;
        int xStart = 0;
        int xEnd = 0;
        int yStart = 0;
        int yEnd = 0;
        switch (direct) {
            case TOP:
                xStart = xEnd = centerX;
                yStart = GhostUtils.displayHeight - GhostUtils.displayHeight / 4;
                yEnd = yStart - distance;
                break;
            case BOTTOM:
                xStart = xEnd = centerX;
                yStart = GhostUtils.displayHeight / 4;
                yEnd = yStart + distance;
                break;
            case LEFT:
                yStart = yEnd = centerY;
                xStart = GhostUtils.displayWidth - GhostUtils.displayWidth / 4;
                xEnd = xStart - distance;
                break;
            case RIGHT:
                yStart = yEnd = centerY;
                xStart = GhostUtils.displayWidth / 4;
                xEnd = xStart + distance;
                break;

        }
        this.from.point.x = xStart;
        this.from.point.y = yStart;
        this.to.point.x = xEnd;
        this.to.point.y = yEnd;
        calculateMoveEvent(this.from, this.to, DEFAULT_SLIDE_MOVE_SIZE, DEFAULT_INTERVAL_SLIDE_MOVE_TIME_UNIT);
    }

    public SlideEvent(ITarget target, final TouchPoint from, final TouchPoint to) {
        this(target, from, to, null);
    }

    public SlideEvent(ITarget target, final TouchPoint from, final TouchPoint to, final List<TouchPoint> moveList) {
        super(target);
        this.setName(TAG);
        this.target = target;
        this.from = from;
        this.to = to;
        if (from.point.x < 0 || from.point.y < 0 || to.point.x < 0 || to.point.y < 0) {
            LogUtil.e(TAG, "invalid params slide from - to");
            return;
        }
        moves.clear();
        if (moveList != null) {
            moves.addAll(moveList);
            return;
        }
        calculateMoveEvent(this.from, this.to, DEFAULT_SLIDE_MOVE_SIZE, DEFAULT_INTERVAL_SLIDE_MOVE_TIME_UNIT);
    }

    protected void calculateMoveEvent(TouchPoint from, TouchPoint to, int moveSize, long moveSpentTime) {
        moves.clear();
        timeOut = 0;
        PointF toPoint = to.point;
        PointF fromPoint = from.point;
        float horizontal = toPoint.x - fromPoint.x;
        float vertical = toPoint.y - fromPoint.y;
        float horizontalDistance = Math.abs(horizontal);
        float verticalDistance = Math.abs(vertical);
        float xUnit = 0;
        float yUnit = 0;
        float ratio = 0;
        if (vertical != 0) {
            ratio = horizontalDistance / verticalDistance;
        }

        if (ratio <= 1 && vertical != 0) { // 说明是一个偏y轴的滑动，则以y为distance
            final float distanceUnit = verticalDistance / moveSize;
            yUnit = distanceUnit;
            if (horizontalDistance > 5) { // 如果x轴只动了5，默认是一个直y轴的滑动
                xUnit = ratio * distanceUnit;
            }
        } else {
            final float distanceUnit = horizontalDistance / moveSize;
            xUnit = distanceUnit;
            if (verticalDistance > 5) { // 如果y轴只动了5，默认是一个直x轴的滑动
                yUnit = ratio * distanceUnit;
            }
        }
        timeOut = 0;
        for (int i = 0, count = 0; count < moveSize; i++, count++) {
            float moveX = horizontal > 0 ? (fromPoint.x + i * xUnit) : (fromPoint.x - i * xUnit);
            float moveY = vertical > 0 ? (fromPoint.y + i * yUnit) : (fromPoint.y - i * yUnit);
            TouchPoint move = new TouchPoint(new PointF(moveX, moveY), moveSpentTime);

            long spentTime = moveSpentTime;
            if (spentTime < MIN_INTERVAL_SLIDE_MOVE_TIME_UNIT) {
                spentTime = MIN_INTERVAL_SLIDE_MOVE_TIME_UNIT;
            } else if (spentTime > MAX_INTERVAL_SLIDE_MOVE_TIME_UNIT) {
                spentTime = MAX_INTERVAL_SLIDE_MOVE_TIME_UNIT;
            }
            move.spentTime = spentTime;
            timeOut += spentTime;
            moves.add(move);
        }
        LogUtil.d(TAG, "calculate move size:" + moves.size());
    }

    public void setMoveSpentTime(long time) {
        for (TouchPoint move : moves) {
            move.spentTime = time;
        }
    }

    public void setMoveSize(int size) {
        calculateMoveEvent(this.from, this.to, size, DEFAULT_INTERVAL_SLIDE_MOVE_TIME_UNIT);
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
                doEvent(cancel, from, to);
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public long getExecuteTimeOut() {
        return timeOut + moves.size() * TIME_MOVE_SPACE_MILLISECONDS;
    }

    protected void doEvent(final AtomicBoolean cancel, TouchPoint from, TouchPoint to) {
        if (cancel.get()) {
            LogUtil.d(TAG, "event cancel");
            return;
        }
        long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = mockMotionEvent(downTime, downTime, MotionEvent.ACTION_DOWN, from.point.x, from.point.y);
        LogUtil.d(TAG, "doEvent down:" + from.toString() + " up:" + to.toString());
        LogUtil.d(TAG, "down event");
        target.doEvent(downEvent);
        final Semaphore semaphore = new Semaphore(0, true);
        for (final TouchPoint move : moves) {
            if (cancel.get()) {
                LogUtil.d(TAG, "event cancel in move ,send up event and cancel!");
                long upTime = SystemClock.uptimeMillis();
                MotionEvent upEvent = mockMotionEvent(downTime, upTime, MotionEvent.ACTION_UP, to.point.x, to.point.y);
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
                boolean ok = semaphore.tryAcquire(move.spentTime + TIME_MOVE_SPACE_MILLISECONDS, TimeUnit.MILLISECONDS);
                if (!ok) {
                    LogUtil.d(TAG, "semaphore acquire time out");
                } else {
                    LogUtil.d(TAG, "semaphore acquired");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LogUtil.d(TAG, "up event");
        long upTime = SystemClock.uptimeMillis();
        MotionEvent upEvent = mockMotionEvent(downTime, upTime, MotionEvent.ACTION_UP, to.point.x, to.point.y);
        target.doEvent(upEvent);
    }

    @Override
    public String toString() {
        return "SlideEvent{" +
                "from=" + from.toString() +
                ", to=" + to.toString() +
                '}';
    }
}
