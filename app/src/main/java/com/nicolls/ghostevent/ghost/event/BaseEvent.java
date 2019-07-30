package com.nicolls.ghostevent.ghost.event;

import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.utils.Constants;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;

/**
 * author:mengjiankang
 * date:2018/11/25
 * <p>
 * </p>
 */
public abstract class BaseEvent {
    private static final String TAG = "BaseEvent";
    private ITarget target;

    public BaseEvent(ITarget target) {
        this.target = target;
    }

    public abstract Completable exe(AtomicBoolean cancel);

    public int getId() {
        return hashCode();
    }

    public abstract String getName();

    public abstract String getDetail();

    public boolean needRetry() {
        return false;
    }

    protected void sleepTimes(long times) {
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected MotionEvent mockMotionEvent(long downTime, long eventTime, int action, float x, float y) {
        return MotionEvent.obtain(downTime, eventTime, action, x, y, 0.65f, 0.23f, 0, 1.0f, 1.0f, 4, 0);
    }

    /**
     * 毫秒
     * 每个event允许执行的最大时长，如果超过这个时长还没有执行完，则认为是失败的 ，单位：毫秒
     *
     * @return
     */
    public abstract long getExecuteTimeOut();

    /**
     * 获取延长的时长
     *
     * @return
     */
    protected long getExtendsTime() {
        return Constants.DEFAULT_EVENT_EXECUTE_TIMEOUT_EXTEND;
    }

}
