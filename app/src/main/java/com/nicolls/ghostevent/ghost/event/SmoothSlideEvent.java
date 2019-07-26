package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.ITarget;

import java.util.ArrayList;
import java.util.List;

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
