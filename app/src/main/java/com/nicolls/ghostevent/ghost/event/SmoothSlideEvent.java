package com.nicolls.ghostevent.ghost.event;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SmoothSlideEvent extends SlideEvent {
    private static final String TAG = "SmoothSlideEvent";
    private final ITarget target;
    private final List<TouchPoint> moves = new ArrayList<>();

    public SmoothSlideEvent(ITarget target, Direction direct) {
        super(target, direct);
        this.target = target;
        this.setName(TAG);
    }

    public SmoothSlideEvent(ITarget target, TouchPoint from, TouchPoint to) {
        super(target, from, to);
        this.target = target;
        this.setName(TAG);
    }

    @Override
    protected void calculateMoveEvent(TouchPoint from, TouchPoint to, int moveSize, long moveSpentTime) {
        super.calculateMoveEvent(from, to, 20, 16);
    }
}
