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
            if (node.centerY > 0) {
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
            final int scrollY = webView.getScrollY();
            final int nodeY = (int) closeNode.centerY;
            LogUtil.d(TAG, "webViewHeight:" + webViewHeight + " scrollY:" + scrollY + " nodeY:" + nodeY);
            int from = scrollY;
            int to = 0;
            if (nodeY > 0) {
                if (nodeY > webViewHeight) {
                    int more = nodeY - webViewHeight;
                    to = scrollY + more;
                } else if (nodeY < webViewHeight / 2) {
                    to = scrollY - webViewHeight / 2;
                } else {
                    to = scrollY;
                }
            } else {
                to = scrollY - Math.abs(nodeY) - webViewHeight / 2;
            }
            return new Line(from, to);
        }
        return null;
    }

    @Override
    public String getName() {
        return TAG;
    }
}
