package com.nicolls.ghostevent.ghost.event;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.parse.ViewNode;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class AdvertClickEvent extends BaseEvent {
    private static final String TAG = "AdvertClickEvent";
    // 毫秒
    public static final int CLICK_INTERVAL_TIME = 100;
    private static final int CLICK_EXECUTE_TIMEOUT = CLICK_INTERVAL_TIME * 2;
    private TouchPoint touchPoint;
    private IWebTarget target;

    public AdvertClickEvent(IWebTarget target) {
        super(target);
        this.target = target;
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

        // click
        List<ViewNode> adNodes = new ArrayList<>();
        for (ViewNode node : target.getViewNodes()) {
            if (node.type == ViewNode.Type.ADVERT) {
                adNodes.add(node);
            }
        }
        LogUtil.d(TAG, "advert nodes size:" + adNodes.size());
        // update click point

        for (ViewNode node : adNodes) {
            if (node.centerY > 0) {
                touchPoint = TouchPoint.obtainClick(node.centerX, node.centerY);
                break;
            }
        }
        if (touchPoint == null) {
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

    public static class Builder {
        private AdvertClickEvent clickEvent;

        public Builder(IWebTarget target) {
            clickEvent = new AdvertClickEvent(target);
        }

        public static AdvertClickEvent copy(AdvertClickEvent clickEvent) {
            AdvertClickEvent copyEvent = new AdvertClickEvent(clickEvent.target);
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

        public AdvertClickEvent create() {
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

    public long getExecuteTimeOut() {
        return CLICK_EXECUTE_TIMEOUT;
    }

}

