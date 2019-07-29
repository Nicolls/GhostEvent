package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.event.provider.EventParamsProvider;

public class ClickWebEvent extends ClickEvent {
    public ClickWebEvent(ClickWebEvent clickEvent) {
        super(clickEvent);
    }

    public ClickWebEvent(IWebTarget target, TouchPoint touchPoint) {
        super(target, touchPoint);
    }

    public ClickWebEvent(IWebTarget target, EventParamsProvider<TouchPoint> provider) {
        super(target, provider);
    }

    IWebTarget getWebTarget() {
        return (IWebTarget) getTarget();
    }
}
