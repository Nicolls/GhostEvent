package com.nicolls.ghostevent.ghost.core;

import android.content.Context;

import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickWebEvent;
import com.nicolls.ghostevent.ghost.event.GroupEvent;
import com.nicolls.ghostevent.ghost.event.PageGoBackEvent;
import com.nicolls.ghostevent.ghost.event.RedirectClickEvent;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.event.provider.GoBackEventProvider;
import com.nicolls.ghostevent.ghost.event.provider.GoHomeEventProvider;
import com.nicolls.ghostevent.ghost.event.provider.LoadPageEventProvider;
import com.nicolls.ghostevent.ghost.event.provider.SlideToAdvertAndClickProvider;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.ViewNode;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertParser;
import com.nicolls.ghostevent.ghost.parse.advert.IAdvertTarget;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class EventBuilder {
    private static final String TAG = "EventBuilder";
    private final Context context;
    private final RedirectHandler redirectHandler;
    private final EventExecutor.ExecuteCallBack executeCallBack;
    private final List<ViewNode> viewNodes = new ArrayList<>();

    public EventBuilder(Context context, RedirectHandler redirectHandler,
                        EventExecutor.ExecuteCallBack executeCallBack) {
        this.context = context.getApplicationContext();
        this.redirectHandler = redirectHandler;
        this.executeCallBack = executeCallBack;
    }

    public List<BaseEvent> buildAutoEvent(IWebTarget target, String url, int size, boolean haveAdvert) {
        List<BaseEvent> list = new ArrayList<>();
        BaseEvent loadPageEvent = getLoadPageEvent(target, url);
        List<BaseEvent> randomEvents = getRandomEvents(target, size, haveAdvert);
        list.add(loadPageEvent);
        list.addAll(randomEvents);
        return list;
    }

    public GroupEvent getLoadPageEvent(IWebTarget target, String url) {
        return new GroupEvent(target, executeCallBack,
                new LoadPageEventProvider(target, advertTarget, redirectHandler, url).getParams());
    }

    public GroupEvent getGoBackEvent(IWebTarget target) {

        return new GroupEvent(target, executeCallBack, new GoBackEventProvider(target, advertTarget, redirectHandler).getParams());
    }

    public GroupEvent getHomeEvent(IWebTarget target) {
        return new GroupEvent(target, executeCallBack, new GoHomeEventProvider(target, advertTarget, redirectHandler).getParams());
    }


    public WebParseEvent getParseEvent(IWebTarget target) {
        IWebParser parseAdvert = new AdvertParser();
        WebParseEvent parseEvent = new WebParseEvent(target, parseAdvert);
        return parseEvent;
    }

    public GroupEvent getCloseAdvertClickEvent(IWebTarget target) {

        return new GroupEvent(target, executeCallBack, new SlideToAdvertAndClickProvider(target, redirectHandler).getParams());
    }

    public List<BaseEvent> getRandomEvents(IWebTarget target, int size, boolean haveAdvert) {
        List<BaseEvent> list = new ArrayList<>();
        int displayWidth = GhostUtils.displayWidth;
        int displayHeight = GhostUtils.displayHeight;
        TouchPoint clickCenter = TouchPoint.obtainClick(displayWidth / 4, displayHeight - displayHeight / 4);
        ScrollVerticalEvent scrollVerticalUpEvent = new ScrollVerticalEvent(target, displayHeight / 3);
        ScrollVerticalEvent scrollVerticalDownEvent = new ScrollVerticalEvent(target, displayHeight / -4);
        BaseEvent clickRedirect = new RedirectClickEvent(new ClickWebEvent(target, clickCenter), redirectHandler);
        BaseEvent backEvent = new PageGoBackEvent(target, redirectHandler);
        BaseEvent closeAdvertClickEvent = getCloseAdvertClickEvent(target);

        list.add(scrollVerticalUpEvent);
        list.add(scrollVerticalUpEvent);
        list.add(clickRedirect);
        list.add(backEvent);
        list.add(scrollVerticalUpEvent);
        list.add(scrollVerticalUpEvent);
        list.add(scrollVerticalDownEvent);
        if (haveAdvert && closeAdvertClickEvent != null) {
            LogUtil.d(TAG,"add closeAdvertClickEvent");
            list.add(closeAdvertClickEvent);
        }
        list.add(scrollVerticalUpEvent);
        list.add(backEvent);
        list.add(scrollVerticalUpEvent);

//        list.add(scrollVerticalUpEvent);
//        list.add(scrollVerticalUpEvent);
//        list.add(scrollVerticalDownEvent);
//        list.add(clickRedirect);
//        list.add(backEvent);
//        list.add(scrollVerticalUpEvent);
//        list.add(scrollVerticalUpEvent);
//        list.add(clickRedirect);
//        list.add(scrollVerticalUpEvent);
//        list.add(scrollVerticalDownEvent);
//        list.add(backEvent);
//        list.add(scrollVerticalUpEvent);
//        if (haveAdvert && advertEvent != null) {
//            list.add(advertEvent);
//        }
//        list.add(scrollVerticalUpEvent);
//        list.add(backEvent);
//        list.add(scrollVerticalUpEvent);
        return list;
    }

    private final IAdvertTarget advertTarget = new IAdvertTarget() {
        @Override
        public void onParseStart() {
            LogUtil.d(TAG, "onParseWebStart");
            viewNodes.clear();
        }

        @Override
        public void onParseSuccess() {
            LogUtil.d(TAG, "onParseSuccess");
        }

        @Override
        public void onParseFail() {
            LogUtil.d(TAG, "onParseFail");
            viewNodes.clear();
        }

        @Override
        public void onCurrentPageHtml(String result) {
            LogUtil.d(TAG, "onCurrentPageHtml");
//            LogUtil.d(TAG, result);
        }

        @Override
        public void onJsCallBackHandleError() {
            LogUtil.d(TAG, "onJsCallBackHandleError");
        }

        @Override
        public void onFoundItem(ViewNode result) {
//            LogUtil.d(TAG, "foundItem " + result.toString());
            viewNodes.add(result);
        }

        @Override
        public void onFoundItemHtml(String result) {
//            LogUtil.d(TAG, "onFoundItemHtml " + result);
        }

        @Override
        public void onFoundAdvert(ViewNode result) {
//            LogUtil.d(TAG, "onFoundAdvert " + result.toString());
            viewNodes.add(result);
        }
    };

    public List<ViewNode> getViewNodes() {
        return viewNodes;
    }

    public void quit() {
        LogUtil.d(TAG, "quit");
    }

}
