package com.nicolls.ghostevent.ghost.event;

import android.graphics.PointF;

public class TouchPoint {
    /**
     * down事件和up事件花费的时长
     */
    public static final long INTERVAL_DOWN_UP_TIME = 0;

    /**
     * move事件花费的时长
     */
    public static final long INTERVAL_MOVE_TIME = 16;

    public PointF point;
    public long spentTime;

    public TouchPoint(PointF point, long spentTime) {
        if (point == null) {
            point = new PointF(0, 0);
        }
        this.point = point;
        this.spentTime = spentTime;
    }

    public TouchPoint(TouchPoint touchPoint) {
        this.point = touchPoint.point;
        this.spentTime = touchPoint.spentTime;
    }

    public static TouchPoint obtain() {
        return new TouchPoint(null, 0);
    }

    public static TouchPoint obtain(TouchPoint touchPoint) {
        return new TouchPoint(touchPoint);
    }

    public static TouchPoint obtainMove(float x, float y) {
        return new TouchPoint(new PointF(x, y), INTERVAL_MOVE_TIME);
    }

    public static TouchPoint obtainDown(float x, float y) {
        return new TouchPoint(new PointF(x, y), INTERVAL_DOWN_UP_TIME);
    }

    public static TouchPoint obtainUp(float x, float y) {
        return new TouchPoint(new PointF(x, y), INTERVAL_DOWN_UP_TIME);
    }

    public static TouchPoint obtainClick(float x, float y) {
        return new TouchPoint(new PointF(x, y), ClickEvent.CLICK_INTERVAL_TIME);
    }

    @Override
    public String toString() {
        return "TouchPoint{" +
                "point=" + point.toString() +
                ", spentTime=" + spentTime +
                '}';
    }
}
