package com.nicolls.ghostevent.ghost.event.provider;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.ClickEvent;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.event.provider.EventParamsProvider;
import com.nicolls.ghostevent.ghost.parse.ViewNode;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class AdvertClickEventParamsProvider extends EventParamsProvider<TouchPoint> {
    private static final String TAG = "AdvertClickEventParamsProvider";
    private final IWebTarget target;

    public AdvertClickEventParamsProvider(final IWebTarget target) {
        this.target = target;
    }

    @Override
    public TouchPoint getParams() {
        List<ViewNode> adNodes = new ArrayList<>();
        for (ViewNode node : target.getViewNodes()) {
            if (node.type == ViewNode.Type.ADVERT) {
                adNodes.add(node);
            }
        }
        LogUtil.d(TAG, "advert nodes size:" + adNodes.size());
        for (ViewNode node : adNodes) {
            if (node.top > 0) {
                return TouchPoint.obtainClick(node.centerX, node.centerY);
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return TAG;
    }

}
