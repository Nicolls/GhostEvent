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
            LogUtil.d(TAG, "use clickNode");
            Random random = new Random();
            if (clickNode.right > GhostUtils.displayWidth) {
                clickNode.right = GhostUtils.displayWidth - 100;
                clickNode.left = GhostUtils.displayWidth / 8;
            }
            int width = (int) (clickNode.right - clickNode.left);
            int height = (int) (clickNode.bottom - clickNode.top);
            int w = random.nextInt(width);
            int h = random.nextInt(height / 2);

            float x = clickNode.left + Math.abs(w);
            if (x >= (clickNode.right - 20)) {
                x = clickNode.right - 20;
            }
            float y = clickNode.top + 50 + Math.abs(h);
            if (y >= (clickNode.bottom - 20)) {
                y = clickNode.bottom - 20;
            }
            touchPoint = TouchPoint.obtainClick(x, y);
        } else {
            Random random = new Random();
            if (random.nextInt(20) >= 17) {
                LogUtil.d(TAG, "clickNode null random click");
                int displayWidth = GhostUtils.displayWidth;
                int borderWidth = displayWidth / 4;
                int displayHeight = GhostUtils.displayHeight;
                int borderHeight = displayHeight / 8;

                int x = borderWidth + random.nextInt(displayWidth / 2);
                int y = borderHeight + random.nextInt(displayHeight / 2);
                LogUtil.d(TAG, "getClickEvent x:" + x + " y:" + y);
                touchPoint = TouchPoint.obtainClick(x, y);
            }
        }
        super.doEvent();
    }
}
