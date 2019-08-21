package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.event.behavior.IEventBehavior;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;

import java.util.concurrent.Semaphore;

public class ClickIconEvent extends ClickEvent {
    private static final String TAG="ClickIconEvent";
    public ClickIconEvent(ITarget target, IEventBehavior eventBehavior) {
        super(target, null, eventBehavior);
    }

    @Override
    protected void doEvent(final Semaphore semaphore) {
        ViewNode viewNode = ParseManager.getInstance().getCurrentParseNode();
        if (viewNode != null) {
            float x = viewNode.centerX;
            float y = viewNode.centerY;
            touchPoint=TouchPoint.obtainClick(x,y);
        }
        super.doEvent(semaphore);
    }

    @Override
    public String getName() {
        return TAG;
    }
}
