package com.nicolls.ghostevent.ghost.event.provider;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.parse.ViewNode;

public class ArrowTopClickEventParamsProvider extends EventParamsProvider<TouchPoint> {
    private static final String TAG = "ArrowTopClickEventParamsProvider";
    private final IWebTarget target;

    public ArrowTopClickEventParamsProvider(final IWebTarget target) {
        this.target = target;
    }

    @Override
    public TouchPoint getParams() {
        if (target.getArrowTopNode() != null) {
            ViewNode node = target.getArrowTopNode();
            return TouchPoint.obtainClick(node.centerX, node.centerY);
        }
        return null;
    }

    @Override
    public String getName() {
        return TAG;
    }

}
