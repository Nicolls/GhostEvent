package com.nicolls.ghostevent.ghost.event;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import com.nicolls.ghostevent.ghost.utils.LogUtil;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;

import java.util.ArrayList;
import java.util.List;

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
    static final int INTERVAL_TIME_CLICK = 100;
    /**
     * 每一个滑动事件需要触发的move数
     */
    private static final int SLIDE_MOVE_TIMES = 8;
    /**
     * 每一个滑动事件触发的move所占用的时间
     */
    private static final long INTERVAL_TIME_SLIDE_MOVE_UNIT = 16;
    private PointF from=new PointF();
    private PointF to=new PointF();
    private List<PointF> moves=new ArrayList<>();
    private View view;

    public enum Direction{
        LEFT,RIGHT,TOP,BOTTOM
    }

    public SlideEvent(View view, final Direction direct) {
        this.view=view;
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
        int moveUnit = distance / SLIDE_MOVE_TIMES;
        int xStart = 0;
        int xEnd = 0;
        int yStart = 0;
        int yEnd = 0;
        int moveUnitX = 0;
        int moveUnitY = 0;
        switch (direct) {
            case TOP:
                xStart = xEnd = centerX;
                yStart = GhostUtils.displayHeight - GhostUtils.displayHeight / 4;
                yEnd = yStart - distance;
                moveUnitX = 0;
                moveUnitY = -moveUnit;
                break;
            case BOTTOM:
                xStart = xEnd = centerX;
                yStart = GhostUtils.displayHeight / 4;
                yEnd = yStart + distance;
                moveUnitX = 0;
                moveUnitY = moveUnit;
                break;
            case LEFT:
                yStart = yEnd = centerY;
                xStart = GhostUtils.displayWidth - GhostUtils.displayWidth / 4;
                xEnd = xStart - distance;
                moveUnitX = -moveUnit;
                moveUnitY = 0;
                break;
            case RIGHT:
                yStart = yEnd = centerY;
                xStart = GhostUtils.displayWidth / 4;
                xEnd = xStart + distance;
                moveUnitX = moveUnit;
                moveUnitY = 0;
                break;

        }
        this.from.x = xStart;
        this.from.y = yStart;
        // 移动的点，包括up跟down
        for (int i = 0; i < SLIDE_MOVE_TIMES; i++) {
            int moveX = xStart + moveUnitX * i;
            int moveY = yStart + moveUnitY * i;
            PointF move = new PointF(moveX, moveY);
            this.moves.add(move);
        }
        this.to.x = xEnd;
        this.to.y = yEnd;
    }

    public SlideEvent(View view, final PointF from, final PointF to) {
        this.view=view;
        this.from.x=from.x;
        this.from.y=from.y;
        this.to.x = to.x;
        this.to.y= to.y;
        if (from.x < 0 || from.y < 0 || to.x < 0 || to.y < 0) {
            LogUtil.e(TAG, "invalid params slide from - to");
            return;
        }
        float horizontal = to.x - from.x;
        float vertical = to.y - from.y;
        float horizontalDistance = Math.abs(horizontal);
        float verticalDistance = Math.abs(vertical);
        float yUnit = 0;
        float xUnit = 0;
        float ratio = 0;
        if (vertical != 0) {
            ratio = Math.abs(horizontal / vertical);
        }

        if (ratio <= 1&&vertical!=0) { // 说明是一个偏y轴的滑动，则以y为distance
            final float distanceUnit = Math.abs(vertical) / SLIDE_MOVE_TIMES;
            yUnit = distanceUnit;
            if (horizontalDistance > 5) { // 如果x轴只动了5，默认是一个直y轴的滑动
                xUnit = Math.abs(ratio) * distanceUnit;
            }
        } else {
            final float distanceUnit = Math.abs(horizontal) / SLIDE_MOVE_TIMES;
            xUnit = distanceUnit;
            if (verticalDistance > 5) { // 如果y轴只动了5，默认是一个直x轴的滑动
                yUnit = Math.abs(ratio) * distanceUnit;
            }
        }

        for (int i = 0, j = 0; i < horizontalDistance || j < verticalDistance; i += xUnit, j += yUnit) {
            float moveX = horizontal > 0 ? (from.x + i) : (from.x - i);
            float moveY = vertical > 0 ? (from.y + j) : (from.y - j);
            PointF move = new PointF(moveX, moveY);
            moves.add(move);
        }
    }


    @Override
    public Completable exe() {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                long downTime = SystemClock.uptimeMillis();
                MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, from.x, from.y, 0);
                view.dispatchTouchEvent(downEvent);

                for (PointF move : moves) {
                    long moveTime = SystemClock.uptimeMillis();
                    MotionEvent moveEvent = MotionEvent.obtain(downTime, moveTime, MotionEvent.ACTION_MOVE, move.x, move.y, 0);
                    view.dispatchTouchEvent(moveEvent);
                    sleepTimes(INTERVAL_TIME_SLIDE_MOVE_UNIT);
                }

                long upTime = SystemClock.uptimeMillis();
                MotionEvent upEvent = MotionEvent.obtain(downTime, upTime, MotionEvent.ACTION_UP, to.x, to.y, 0);
                view.dispatchTouchEvent(upEvent);
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public String getName() {
        return "SlideEvent";
    }

}
