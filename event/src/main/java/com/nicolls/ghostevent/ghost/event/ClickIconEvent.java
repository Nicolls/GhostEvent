package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.event.behavior.IEventBehavior;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;

public class ClickIconEvent extends ClickEvent {

    public ClickIconEvent(ITarget target, IEventBehavior eventBehavior) {
        super(target, null, eventBehavior);
    }

    @Override
    protected void doEvent() {
        ViewNode viewNode = ParseManager.getInstance().getCurrentParseNode();
        if (viewNode != null) {
            float x = viewNode.centerX;
            float y = viewNode.centerY;
            touchPoint=TouchPoint.obtainClick(x,y);
        }
        super.doEvent();
    }
}
