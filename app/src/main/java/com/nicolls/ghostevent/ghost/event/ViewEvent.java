package com.nicolls.ghostevent.ghost.event;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.nicolls.ghostevent.ghost.DisplayUtils;
import com.nicolls.ghostevent.ghost.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ViewEvent implements IEvent {

    private static final String TAG = "ViewEvent";
    /**
     * 触发点击的时间间隔
     */
    private static final long INTERVAL_TIME_CLICK = 100;
    /**
     * 触发长点击的时间间隔
     */
    private static final long INTERVAL_TIME_LONG_CLICK = 1000;
    /**
     * 每一个滑动事件需要触发的move数
     */
    private static final int SLIDE_MOVE_TIMES = 8;
    /**
     * 每一个滑动事件触发的move所占用的时间
     */
    private static final long INTERVAL_TIME_SLIDE_MOVE_UNIT = 16;
    private static int sDisplayWidth;
    private static int sDisplayHeight;

    private final View view;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ViewEvent(@NonNull final View view) {
        this.view = view;
        Point display = DisplayUtils.getDisplaySize(view.getContext());
        sDisplayWidth = display.x;
        sDisplayHeight = display.y;
    }

    @Override
    public void clickRatio(float ratioX, float ratioY) {
        LogUtil.i(TAG, "clickRatio ratioX:" + ratioX + " ratioY:" + ratioY);
        ratioX = ratioX < 0 ? 0 : ratioX;
        ratioX = ratioX > 1 ? 1 : ratioX;

        ratioY = ratioY < 0 ? 0 : ratioY;
        ratioY = ratioY > 1 ? 1 : ratioY;

        float x = sDisplayWidth * ratioX;
        float y = sDisplayHeight * ratioY;
        click(x, y);
    }

    @Override
    public void click(final float x, final float y) {
        LogUtil.i(TAG, "click x:" + x + " y:" + y);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long downTime = SystemClock.uptimeMillis();
                MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
                view.dispatchTouchEvent(downEvent);
                sleepTimes(INTERVAL_TIME_CLICK);
                long upTime = SystemClock.uptimeMillis();
                MotionEvent upEvent = MotionEvent.obtain(downTime, upTime, MotionEvent.ACTION_UP, x, y, 0);
                view.dispatchTouchEvent(upEvent);
            }
        });

    }

    @Override
    public void longClick(final float x, final float y) {
        LogUtil.i(TAG, "longClick x:" + x + " y:" + y);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long downTime = SystemClock.uptimeMillis();
                MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
                view.dispatchTouchEvent(downEvent);
                sleepTimes(INTERVAL_TIME_LONG_CLICK);
                long upTime = SystemClock.uptimeMillis();
                MotionEvent upEvent = MotionEvent.obtain(downTime, upTime, MotionEvent.ACTION_UP, x, y, 0);
                view.dispatchTouchEvent(upEvent);
            }
        });


    }

    @Override
    public void slide(final Direction direct) {
        LogUtil.i(TAG, "slide direct:" + direct);
        int distance = 0;
        switch (direct) {
            case TOP:
            case BOTTOM:
                distance = sDisplayHeight / 3;
                break;
            case LEFT:
            case RIGHT:
                distance = sDisplayWidth / 3;
                break;
        }
        slide(direct, distance);
    }

    private void slide(final Direction direct, final int distance) {

        int centerX = sDisplayWidth / 2;
        int centerY = sDisplayHeight / 2;
        int moveUnit = distance / SLIDE_MOVE_TIMES;
        Point down = new Point();
        Point up = new Point();
        List<Point> moves = new ArrayList<>();
        int xStart = 0;
        int xEnd = 0;
        int yStart = 0;
        int yEnd = 0;
        int moveUnitX = 0;
        int moveUnitY = 0;
        switch (direct) {
            case TOP:
                xStart = xEnd = centerX;
                yStart = sDisplayHeight - sDisplayHeight / 4;
                yEnd = yStart - distance;
                moveUnitX = 0;
                moveUnitY = -moveUnit;
                break;
            case BOTTOM:
                xStart = xEnd = centerX;
                yStart = sDisplayHeight / 4;
                yEnd = yStart + distance;
                moveUnitX = 0;
                moveUnitY = moveUnit;
                break;
            case LEFT:
                yStart = yEnd = centerY;
                xStart = sDisplayWidth - sDisplayWidth / 4;
                xEnd = xStart - distance;
                moveUnitX = -moveUnit;
                moveUnitY = 0;
                break;
            case RIGHT:
                yStart = yEnd = centerY;
                xStart = sDisplayWidth / 4;
                xEnd = xStart + distance;
                moveUnitX = moveUnit;
                moveUnitY = 0;
                break;

        }
        down.x = xStart;
        down.y = yStart;
        for (int i = 0; i < SLIDE_MOVE_TIMES; i++) {
            int moveX = xStart + moveUnitX * i;
            int moveY = yStart + moveUnitY * i;
            Point move = new Point(moveX, moveY);
            moves.add(move);
        }
        up.x = xEnd;
        up.y = yEnd;
        slide(down, moves, up);

    }

    private void slide(final Point down, final List<Point> moves, final Point up) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long downTime = SystemClock.uptimeMillis();
                MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, down.x, down.y, 0);
                view.dispatchTouchEvent(downEvent);

                for (Point move : moves) {
                    long moveTime = SystemClock.uptimeMillis();
                    MotionEvent moveEvent = MotionEvent.obtain(downTime, moveTime, MotionEvent.ACTION_MOVE, move.x, move.y, 0);
                    view.dispatchTouchEvent(moveEvent);
                    sleepTimes(INTERVAL_TIME_SLIDE_MOVE_UNIT);
                }

                long upTime = SystemClock.uptimeMillis();
                MotionEvent upEvent = MotionEvent.obtain(downTime, upTime, MotionEvent.ACTION_UP, up.x, up.y, 0);
                view.dispatchTouchEvent(upEvent);
            }
        });
    }

    @Override
    public void slide(final PointF from, final PointF to) {
        LogUtil.i(TAG, "slide from:" + from.toString() + " to:" + to.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    private void sleepTimes(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
