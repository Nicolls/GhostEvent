package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.behavior.IEventBehavior;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SelectNodeClickEvent extends ClickEvent {
    private static final String TAG = "SelectNodeClickEvent";
    private ViewNode.Type type;
    private IWebTarget target;

    public SelectNodeClickEvent(IWebTarget target, IEventBehavior eventBehavior, ViewNode.Type type) {
        super(target, null, eventBehavior);
        this.type = type;
        this.target = target;
    }

    @Override
    protected void doEvent() {
        List<ViewNode> viewNodes = ParseManager.getInstance().getViewNodes();
        Collections.reverse(viewNodes);
        int viewHeight = GhostUtils.displayHeight;
        final WebView webView = (WebView) target;
        if (webView != null) {
            viewHeight = webView.getHeight();
        }

        ViewNode clickNode = null;
        for (ViewNode node : viewNodes) {
            if (node.type == this.type
                    && node.bottom < (viewHeight - viewHeight / 10)
                    && node.top < viewHeight
                    && node.top > viewHeight / 8) {
                clickNode = node;
                break;
            }
        }

        LogUtil.d(TAG, "select " + (clickNode == null ? "" : clickNode.toString()));

        if (clickNode != null) {
            Random random = new Random();
            int width = (int) (clickNode.right - clickNode.left);
            int height = (int) (clickNode.bottom - clickNode.top);
            int w = random.nextInt(width);
            int h = random.nextInt(height);

            float x = clickNode.left + Math.abs(w);
            if (x >= (clickNode.right - 20)) {
                x = clickNode.right - 20;
            }
            float y = clickNode.top + Math.abs(h);
            if (y >= (clickNode.bottom - 20)) {
                y = clickNode.bottom - 20;
            }
            touchPoint = TouchPoint.obtainClick(x, y);
        }
        super.doEvent();
    }
}
