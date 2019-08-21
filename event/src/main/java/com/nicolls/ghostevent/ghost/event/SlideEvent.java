package com.nicolls.ghostevent.ghost.event;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public static final long DEFAULT_INTERVAL_SLIDE_MOVE_TIME_UNIT = 12;

    /**
     * 默认滑动距离
     */
    private static final int DEFAULT_DISTANCE = 1000;

    /**
     * 滑动完成后，Delay一些时长，再结束
     */
    private static final int DELAY_COMPLETED = 1000;

    private int from = 0;
    private int to = 0;
    private int x = 0;
    private List<TouchPoint> moves = new ArrayList<>();
    private ITarget target;
    private long timeOut = 0;

    public enum Direction {
        UP, DOWN
    }

    public SlideEvent(ITarget target, final Direction direct) {
        this.target = target;
        LogUtil.i(TAG, "slide direct:" + direct);
        int yStart = 0;
        int yEnd = 0;
        switch (direct) {
            case UP:
                x = GhostUtils.displayWidth / 2;
                yStart = GhostUtils.displayHeight - GhostUtils.displayHeight / 4;
                yEnd = yStart - DEFAULT_DISTANCE;
                break;
            case DOWN:
                x = GhostUtils.displayWidth / 3;
                yStart = GhostUtils.displayHeight / 4;
                yEnd = yStart + DEFAULT_DISTANCE;
                break;

        }
        this.from = yStart;
        this.to = yEnd;
        calculateMoveEvent();
    }

    public SlideEvent(ITarget target, final int from, final int to) {
        this.target = target;
        Random random = new Random();
        x = GhostUtils.displayWidth / 4 + random.nextInt(GhostUtils.displayWidth / 2);
        LogUtil.i(TAG, "slide from:" + from + " to:" + to + " x:" + x);
        this.from = from;
        this.to = to;
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
            long spentTime = DEFAULT_INTERVAL_SLIDE_MOVE_TIME_UNIT + i;
            TouchPoint move = new TouchPoint(new PointF(x, moveY), spentTime);
            moves.add(move);
            timeOut += spentTime;
        }
        LogUtil.d(TAG, "calculate move size:" + moves.size());
    }

    @Override
    public void exe(final AtomicBoolean cancel, final EventCallBack eventCallBack) {
        ExecutorService executorService=target.getEventTaskPool();
        if(executorService==null||executorService.isShutdown()||executorService.isTerminated()){
            LogUtil.w(TAG,"executorService shutdown ");
            if(eventCallBack!=null){
                cancel.set(true);
                eventCallBack.onFail(null);
            }
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    if (eventCallBack != null) {
                        eventCallBack.onComplete();
                    }
                    return;
                }
                doEvent(cancel);
                sleepTimes(DELAY_COMPLETED);
                if (eventCallBack != null) {
                    eventCallBack.onComplete();
                }
            }
        });
    }

    @Override
    public long getExecuteTimeOut() {
        return timeOut + DELAY_COMPLETED + getExtendsTime();
    }

    protected void doEvent(final AtomicBoolean cancel) {
        LogUtil.d(TAG, "doEvent thread " + Thread.currentThread().getName());
        if (cancel.get()) {
            LogUtil.d(TAG, "event cancel");
            return;
        }
        long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = mockMotionEvent(downTime, downTime, MotionEvent.ACTION_DOWN, x, from);
        LogUtil.d(TAG, "doEvent down:" + x + " -" + from);
        target.doEvent(downEvent);
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
            sleepTimes(move.spentTime);
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

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public String getDescribe() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", from);
            jsonObject.put("to", to);
            jsonObject.put("x", x);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}
