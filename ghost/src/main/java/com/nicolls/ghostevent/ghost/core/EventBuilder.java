package com.nicolls.ghostevent.ghost.core;

import android.content.Context;

import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickEvent;
import com.nicolls.ghostevent.ghost.event.ClickIconEvent;
import com.nicolls.ghostevent.ghost.event.ClickNodeEvent;
import com.nicolls.ghostevent.ghost.event.GoBackEvent;
import com.nicolls.ghostevent.ghost.event.GoHomeEvent;
import com.nicolls.ghostevent.ghost.event.GroupEvent;
import com.nicolls.ghostevent.ghost.event.LoadPageEvent;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.SlideEvent;
import com.nicolls.ghostevent.ghost.event.behavior.ClickIconEventBehavior;
import com.nicolls.ghostevent.ghost.event.behavior.LoadWebEventBehavior;
import com.nicolls.ghostevent.ghost.event.behavior.ScrollReadMoreEventBehavior;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.parse.home.HomeArrowTopParser;
import com.nicolls.ghostevent.ghost.parse.secondnews.SecondNewsArrowTopParser;
import com.nicolls.ghostevent.ghost.parse.secondnews.SecondNewsMainIconParser;
import com.nicolls.ghostevent.ghost.parse.secondnews.SecondNewsReadMoreParser;
import com.nicolls.ghostevent.ghost.parse.secondnews.SecondNewsTopAdvertParser;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
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
        BaseEvent slideEvent = new SlideEvent(webTarget, SlideEvent.Direction.DOWN);

        Random random = new Random();
        if (random.nextInt(10) > 5) {
            int from = GhostUtils.displayHeight / 2 + random.nextInt(GhostUtils.displayHeight / 4);
            int to = from + 200 + random.nextInt(GhostUtils.displayHeight / 4);
            BaseEvent slideRandom = new SlideEvent(webTarget, from, to);
            return slideRandom;
        }
        return slideEvent;
    }

    public BaseEvent getSlideUp(IWebTarget webTarget) {
        BaseEvent slideEvent = new SlideEvent(webTarget, SlideEvent.Direction.UP);
        Random random = new Random();
        if (random.nextInt(10) > 4) {
            int from = GhostUtils.displayHeight - random.nextInt(GhostUtils.displayHeight / 4) - 200;
            int to = from - 500 - random.nextInt(400);
            BaseEvent slideRandom = new SlideEvent(webTarget, from, to);
            return slideRandom;
        }
        return slideEvent;
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

    public BaseEvent getSecondNewsAdvertHeadClickEvent(IWebTarget webTarget) {
        SecondNewsTopAdvertParser topParser = new SecondNewsTopAdvertParser();
        ClickIconEventBehavior behavior = new ClickIconEventBehavior(webTarget, redirectHandler, topParser);
        ClickNodeEvent clickIconEvent = new ClickNodeEvent(webTarget, behavior);
        return clickIconEvent;
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

    public BaseEvent getGoHomeEvent(IWebTarget target) {
        LoadWebEventBehavior behavior = new LoadWebEventBehavior(target, redirectHandler);
        BaseEvent event = new GoHomeEvent(target, behavior);
        return event;
    }

    public BaseEvent getHomeClickArrowTopNodeEvent(IWebTarget target) {
        HomeArrowTopParser topParser = new HomeArrowTopParser();
        ClickIconEventBehavior behavior = new ClickIconEventBehavior(target, redirectHandler, topParser);
        ClickIconEvent clickIconEvent = new ClickIconEvent(target, behavior);
        return clickIconEvent;
    }

    public BaseEvent getSecondNewsClickArrowTopNodeEvent(IWebTarget target) {
        SecondNewsArrowTopParser topParser = new SecondNewsArrowTopParser();
        ClickIconEventBehavior behavior = new ClickIconEventBehavior(target, redirectHandler, topParser);
        ClickIconEvent clickIconEvent = new ClickIconEvent(target, behavior);
        return clickIconEvent;
    }

    public BaseEvent getSecondNewsClickMainIconNodeEvent(IWebTarget target) {
        SecondNewsMainIconParser topParser = new SecondNewsMainIconParser();
        ClickIconEventBehavior behavior = new ClickIconEventBehavior(target, redirectHandler, topParser);
        ClickIconEvent clickIconEvent = new ClickIconEvent(target, behavior);
        return clickIconEvent;
    }

    public BaseEvent getScrollToReadMoreNodeEvent(IWebTarget target) {
        SecondNewsReadMoreParser topParser = new SecondNewsReadMoreParser();
        ScrollReadMoreEventBehavior behavior = new ScrollReadMoreEventBehavior(target, topParser);
        ScrollVerticalEvent scrollVerticalEvent = new ScrollVerticalEvent(target, behavior);
        return scrollVerticalEvent;
    }

    public BaseEvent getSecondNewsScrollAndClickReadMoreNodeEvent(IWebTarget target) {

        List<BaseEvent> childEvents = new ArrayList<>();
        // scroll
        BaseEvent scrollEvent = getScrollToReadMoreNodeEvent(target);
        childEvents.add(scrollEvent);
        // click
        SecondNewsReadMoreParser topParser = new SecondNewsReadMoreParser();
        ClickIconEventBehavior behavior = new ClickIconEventBehavior(target, redirectHandler, topParser);
        ClickIconEvent clickIconEvent = new ClickIconEvent(target, behavior);
        childEvents.add(clickIconEvent);
        Random random = new Random();
        int slideSize = random.nextInt(3) + 1;
        for (int i = 0; i < slideSize; i++) {
            BaseEvent slideEvent = getSlideUp(target);
            childEvents.add(slideEvent);
        }
        GroupEvent groupEvent = new GroupEvent(target, executeCallBack, childEvents);

        return groupEvent;
    }

    public void quit() {
        LogUtil.d(TAG, "quit");
    }

}
