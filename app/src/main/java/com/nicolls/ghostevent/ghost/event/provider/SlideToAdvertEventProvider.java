package com.nicolls.ghostevent.ghost.event.provider;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.model.Line;
import com.nicolls.ghostevent.ghost.parse.ViewNode;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SlideToAdvertEventProvider extends EventParamsProvider<Line> {
    private static final String TAG = "SlideToAdvertEventProvider";
    private final IWebTarget target;

    public SlideToAdvertEventProvider(final IWebTarget target) {
        this.target = target;
    }

    @Override
    public Line getParams() {
        if (target == null) {
            return null;
        }
        // get advert nodes
        List<ViewNode> adNodes = new ArrayList<>();
        for (ViewNode node : target.getViewNodes()) {
            if (node.type == ViewNode.Type.ADVERT) {
                adNodes.add(node);
            }
        }
        LogUtil.d(TAG, "advert nodes size:" + adNodes.size());
        // get close advert
        final WebView webView = (WebView) target;
        ViewNode closeNode = null;
        for (ViewNode node : adNodes) {
            if (node.top > 0) {
                closeNode = node;
                break;
            }
        }
        if (closeNode == null && adNodes.size() > 0) {
            closeNode = adNodes.get(adNodes.size() - 1);
        }
        if (closeNode != null) {
            LogUtil.d(TAG, "click advert " + closeNode.toString());
            final int webViewHeight = webView.getHeight();
            final int centerHeight = webViewHeight / 2;
            final int scrollY = webView.getScrollY();
            final int nodeTop = (int) closeNode.top;
            LogUtil.d(TAG, "webViewHeight:" + webViewHeight + " scrollY:" + scrollY);
            int from = scrollY;
            int to = scrollY;
            if (nodeTop < 0) {
                to += nodeTop;
                to -= centerHeight;
            } else if (nodeTop >= 0 && nodeTop <= centerHeight) {
                to -= (centerHeight - nodeTop);
            } else if (nodeTop > centerHeight && nodeTop <= webViewHeight) {
                to += (nodeTop - centerHeight);
            } else {
                to += nodeTop - webViewHeight;
            }
            return new Line(from, to);
        }
        LogUtil.d(TAG,"not find close advert node!");
        return null;
    }

    @Override
    public String getName() {
        return TAG;
    }
}
