package com.nicolls.ghostevent.ghost.core;

import android.content.Context;

import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickEvent;
import com.nicolls.ghostevent.ghost.event.ClickIconEvent;
import com.nicolls.ghostevent.ghost.event.GoBackEvent;
import com.nicolls.ghostevent.ghost.event.LoadPageEvent;
import com.nicolls.ghostevent.ghost.event.SlideEvent;
import com.nicolls.ghostevent.ghost.event.behavior.ClickIconEventBehavior;
import com.nicolls.ghostevent.ghost.event.behavior.LoadWebEventBehavior;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.parse.home.HomeArrowTopParser;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.Random;

public class EventBuilder {
    private static final String TAG = "EventBuilder";
    private final Context context;
    private final RedirectHandler redirectHandler;
    private final EventExecutor.ExecuteCallBack executeCallBack;

    public EventBuilder(Context context, RedirectHandler redirectHandler,
                        EventExecutor.ExecuteCallBack executeCallBack) {
        this.context = context.getApplicationContext();
        this.redirectHandler = redirectHandler;
        this.executeCallBack = executeCallBack;
    }

    public BaseEvent getSlideDown(IWebTarget webTarget) {
        BaseEvent slideDown = new SlideEvent(webTarget, SlideEvent.Direction.DOWN);
        return slideDown;
    }

    public BaseEvent getSlideUp(IWebTarget webTarget) {
        BaseEvent slideDown = new SlideEvent(webTarget, SlideEvent.Direction.UP);
        return slideDown;
    }

    public BaseEvent getClickEvent(IWebTarget webTarget) {
        Random random = new Random();
        int displayWidth = GhostUtils.displayWidth;
        int borderWidth = displayWidth / 4;
        int displayHeight = GhostUtils.displayHeight;
        int borderHeight = displayHeight / 8;

        int x = borderWidth + random.nextInt(displayWidth / 2);
        int y = borderHeight + random.nextInt(displayHeight / 2);
        LogUtil.d(TAG, "getClickEvent x:" + x + " y:" + y);
        TouchPoint clickRandom = TouchPoint.obtainClick(x, y);
        LoadWebEventBehavior behavior = new LoadWebEventBehavior(webTarget, redirectHandler);
        BaseEvent clickRedirect = new ClickEvent(webTarget, clickRandom, behavior);
        return clickRedirect;
    }

    public BaseEvent getSecondAdvertHeadClickEvent(IWebTarget webTarget) {
        Random random = new Random();
        int displayWidth = GhostUtils.displayWidth;
        int borderWidth = displayWidth / 4;
        int displayHeight = GhostUtils.displayHeight;

        int x = borderWidth + random.nextInt(displayWidth / 2);
        int y = 10 + random.nextInt(displayHeight / 8);
        LogUtil.d(TAG, "getClickEvent x:" + x + " y:" + y);
        TouchPoint clickRandom = TouchPoint.obtainClick(x, y);
        LoadWebEventBehavior behavior = new LoadWebEventBehavior(webTarget, redirectHandler);
        BaseEvent clickRedirect = new ClickEvent(webTarget, clickRandom, behavior);
        return clickRedirect;
    }

    public BaseEvent getLoadPageEvent(IWebTarget target, String url) {
        LoadWebEventBehavior behavior = new LoadWebEventBehavior(target, redirectHandler);
        BaseEvent loadPageEvent = new LoadPageEvent(target, url, behavior);
        return loadPageEvent;
    }

    public BaseEvent getGoBackEvent(IWebTarget target) {
        LoadWebEventBehavior behavior = new LoadWebEventBehavior(target, redirectHandler);
        BaseEvent gobackEvent = new GoBackEvent(target, behavior);
        return gobackEvent;
    }

    public BaseEvent getClickArrowTopNodeEvent(IWebTarget target) {
        HomeArrowTopParser topParser = new HomeArrowTopParser();
        ClickIconEventBehavior behavior = new ClickIconEventBehavior(target, redirectHandler, topParser);
        ClickIconEvent clickIconEvent = new ClickIconEvent(target, behavior);
        return clickIconEvent;
    }

    public void quit() {
        LogUtil.d(TAG, "quit");
    }

}
