package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.event.behavior.IEventBehavior;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;

import java.util.Random;

public class ClickNodeEvent extends ClickEvent {

    public ClickNodeEvent(ITarget target, IEventBehavior eventBehavior) {
        super(target, null, eventBehavior);
    }

    @Override
    protected void doEvent() {
        ViewNode viewNode = ParseManager.getInstance().getCurrentParseNode();
        if (viewNode != null) {
            Random random = new Random();
            int width = (int) (viewNode.right - viewNode.left);
            int height = (int) (viewNode.bottom - viewNode.top);
            int w = random.nextInt(width);
            int h = random.nextInt(height);

            float x = viewNode.left + Math.abs(w);
            if (x >= (viewNode.right - 20)) {
                x = viewNode.right - 20;
            }
            float y = viewNode.top + Math.abs(h);
            if (y >= (viewNode.bottom - 20)) {
                y = viewNode.bottom - 20;
            }
            touchPoint = TouchPoint.obtainClick(x, y);
        }
        super.doEvent();
    }
}
