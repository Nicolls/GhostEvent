package com.nicolls.ghostevent.ghost.parse;

import android.graphics.Rect;
import android.webkit.JavascriptInterface;

import com.nicolls.ghostevent.ghost.event.IWebTarget;
import com.nicolls.ghostevent.ghost.event.WebNode;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;

public class JsGhostParser implements JsParser {
    private static final String TAG = "JsGhostParser";
    private final Semaphore semaphore;
    private final IWebTarget target;

    public JsGhostParser(IWebTarget target, Semaphore semaphore) {
        this.semaphore = semaphore;
        this.target = target;
    }

    @Override
    @JavascriptInterface
    public void foundItem(String id, String name, int left, int top, int right, int bottom) {
        WebNode webNode = new WebNode();
        webNode.id = id;
        webNode.position = new Rect(left, top, right, bottom);
        webNode.type = WebNode.Type.AD;
        LogUtil.d(TAG, "foundItem " + webNode.toString());
        semaphore.release();
        target.foundItem(webNode);
    }
}