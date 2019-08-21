package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.event.behavior.IEventBehavior;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class ClickNodeEvent extends ClickEvent {
    private static final String TAG="ClickIconEvent";

    public ClickNodeEvent(ITarget target, IEventBehavior eventBehavior) {
        super(target, null, eventBehavior);
    }

    @Override
    protected void doEvent(final Semaphore semaphore) {
        ViewNode viewNode = ParseManager.getInstance().getCurrentParseNode();
        if (viewNode != null) {
            Random random = new Random();

            if (viewNode.right > GhostUtils.displayWidth) {
                viewNode.right = GhostUtils.displayWidth - 100;
                viewNode.left = GhostUtils.displayWidth / 8;
            }

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
        super.doEvent(semaphore);
    }

    @Override
    public String getName() {
        return TAG;
    }
}
