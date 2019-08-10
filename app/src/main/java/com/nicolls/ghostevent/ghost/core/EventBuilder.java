package com.nicolls.ghostevent.ghost.core;

import android.content.Context;

import com.nicolls.ghostevent.ghost.event.BackPageEvent;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickArrowTopEvent;
import com.nicolls.ghostevent.ghost.event.ClickCloseAdvertEvent;
import com.nicolls.ghostevent.ghost.event.ClickWebEvent;
import com.nicolls.ghostevent.ghost.event.GroupEvent;
import com.nicolls.ghostevent.ghost.event.HomePageEvent;
import com.nicolls.ghostevent.ghost.event.LoadPageEvent;
import com.nicolls.ghostevent.ghost.event.RedirectClickEvent;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.SlideEvent;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.event.provider.ClickArrowToTopProvider;
import com.nicolls.ghostevent.ghost.event.provider.ClickCloseAdvertProvider;
import com.nicolls.ghostevent.ghost.event.provider.GoBackEventProvider;
import com.nicolls.ghostevent.ghost.event.provider.GoHomeEventProvider;
import com.nicolls.ghostevent.ghost.event.provider.LoadPageEventProvider;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.ViewNode;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertParser;
import com.nicolls.ghostevent.ghost.parse.advert.IAdvertTarget;
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
    private final List<ViewNode> viewNodes = new ArrayList<>();
    private ViewNode arrowTopNode;

    public EventBuilder(Context context,  RedirectHandler redirectHandler,
                        EventExecutor.ExecuteCallBack executeCallBack) {
        this.context = context.getApplicationContext();
        this.redirectHandler = redirectHandler;
        this.executeCallBack = executeCallBack;
    }

    public BaseEvent getSlideDown(IWebTarget webTarget){
        BaseEvent slideDown = new SlideEvent(webTarget, SlideEvent.Direction.DOWN);
        return slideDown;
    }

    public BaseEvent getSlideUp(IWebTarget webTarget){
        BaseEvent slideDown = new SlideEvent(webTarget, SlideEvent.Direction.UP);
        return slideDown;
    }

    public BaseEvent getClickEvent(IWebTarget webTarget){
        Random random=new Random();
        int displayWidth = GhostUtils.displayWidth;
        int borderWidth=displayWidth/4;
        int displayHeight = GhostUtils.displayHeight;
        int borderHeight=displayHeight/8;

        int x=borderWidth+random.nextInt(displayWidth/2);
        int y=borderHeight+random.nextInt(displayHeight/2);
        LogUtil.d(TAG,"getClickEvent x:"+x+" y:"+y);
        TouchPoint clickRandom = TouchPoint.obtainClick(x, y);
        BaseEvent clickRedirect = new RedirectClickEvent(new ClickWebEvent(webTarget, clickRandom), redirectHandler);
        return clickRedirect;
    }

    public BaseEvent getSecondAdvertHeadClickEvent(IWebTarget webTarget){
        Random random=new Random();
        int displayWidth = GhostUtils.displayWidth;
        int borderWidth=displayWidth/4;
        int displayHeight = GhostUtils.displayHeight;

        int x=borderWidth+random.nextInt(displayWidth/2);
        int y=10+random.nextInt(displayHeight/8);
        LogUtil.d(TAG,"getClickEvent x:"+x+" y:"+y);
        TouchPoint clickRandom = TouchPoint.obtainClick(x, y);
        BaseEvent clickRedirect = new RedirectClickEvent(new ClickWebEvent(webTarget, clickRandom), redirectHandler);
        return clickRedirect;
    }

    public List<BaseEvent> buildAutoEvent(IWebTarget target, String url, int size, boolean haveAdvert) {
        List<BaseEvent> list = new ArrayList<>();
        int displayWidth = GhostUtils.displayWidth;
        int displayHeight = GhostUtils.displayHeight;
        TouchPoint clickCenter = TouchPoint.obtainClick(displayWidth / 4, displayHeight - displayHeight / 4);
        BaseEvent clickRedirect = new RedirectClickEvent(new ClickWebEvent(target, clickCenter), redirectHandler);

        BaseEvent loadPageEvent = new LoadPageEvent(target, executeCallBack, new LoadPageEventProvider(target, advertTarget, redirectHandler, url));
        BaseEvent backPageEvent = new BackPageEvent(target, executeCallBack, new GoBackEventProvider(target, advertTarget, redirectHandler));
        BaseEvent homePageEvent = new HomePageEvent(target, executeCallBack, new GoHomeEventProvider(target, advertTarget, redirectHandler));
        BaseEvent clickArrowTopEvent = new ClickArrowTopEvent(target, executeCallBack, new ClickArrowToTopProvider(target, executeCallBack, redirectHandler, advertTarget));


        ScrollVerticalEvent scrollVerticalUpEvent = new ScrollVerticalEvent(target, displayHeight / 3);
        ScrollVerticalEvent scrollVerticalDownEvent = new ScrollVerticalEvent(target, displayHeight / -4);

        BaseEvent slideUp = new SlideEvent(target, SlideEvent.Direction.UP);
        BaseEvent slideDown = new SlideEvent(target, SlideEvent.Direction.DOWN);

        BaseEvent closeAdvertClickEvent = new ClickCloseAdvertEvent(target, executeCallBack, new ClickCloseAdvertProvider(target, executeCallBack, redirectHandler, advertTarget));

        list.add(loadPageEvent);

        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int eventIndex = random.nextInt(12);
            LogUtil.d(TAG, "eventIndex :" + eventIndex);
            switch (eventIndex) {
                case 0:
                case 1:
                    list.add(slideUp);
                    break;
                case 2:
                case 3:
                    list.add(slideDown);
                    break;
                case 4:
                    list.add(scrollVerticalUpEvent);
                    break;
                case 5:
                    list.add(scrollVerticalDownEvent);
                    break;
                case 6:
                    list.add(backPageEvent);
                    break;
                case 7:
                    Random r = new Random();
                    TouchPoint clickP = TouchPoint.obtainClick(displayWidth / 4 + r.nextInt(6) * 100, displayHeight - displayHeight / 8 * r.nextInt(4));
                    BaseEvent cr = new RedirectClickEvent(new ClickWebEvent(target, clickP), redirectHandler);
                    list.add(cr);
                    break;
                case 8:
                    list.add(closeAdvertClickEvent);
                    break;
                case 9:
                    list.add(homePageEvent);
                    break;
                case 10:
                    list.add(clickArrowTopEvent);
                    break;
                case 11:
                    list.add(clickRedirect);
                    break;
                default:
                    list.add(slideUp);
                    break;
            }
        }
        LogUtil.d(TAG, "event size:" + list.size());
//
//        list.add(slideUp);
//        list.add(slideUp);
//        list.add(slideDown);
//        list.add(scrollVerticalUpEvent);
//        list.add(slideDown);
//        list.add(scrollVerticalUpEvent);
//        list.add(clickRedirect);
//        list.add(backPageEvent);
//        list.add(scrollVerticalUpEvent);
//        list.add(scrollVerticalUpEvent);
//        list.add(scrollVerticalDownEvent);
//        if (haveAdvert && closeAdvertClickEvent != null) {
//            LogUtil.d(TAG, "add closeAdvertClickEvent");
//            list.add(closeAdvertClickEvent);
//        }
//        list.add(scrollVerticalUpEvent);
//        list.add(backPageEvent);
//        list.add(scrollVerticalUpEvent);
        Random rAdvert=new Random();
        int index=rAdvert.nextInt(size-1);
        list.add(index,closeAdvertClickEvent);
        return list;
    }

    public BaseEvent getLoadPageEvent(IWebTarget target,String url) {
        BaseEvent loadPageEvent = new LoadPageEvent(target, executeCallBack, new LoadPageEventProvider(target, advertTarget, redirectHandler, url));
        return loadPageEvent;
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

    public BaseEvent getClickArrowTopNodeEvent(IWebTarget target) {
        BaseEvent baseEvent = new ClickArrowTopEvent(target, executeCallBack, new ClickArrowToTopProvider(target, executeCallBack, redirectHandler, advertTarget));
        return baseEvent;
    }

    public GroupEvent getCloseAdvertClickEvent(IWebTarget target) {

        return new GroupEvent(target, executeCallBack, new ClickCloseAdvertProvider(target, executeCallBack, redirectHandler, advertTarget).getParams());
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
        public void onMessage(String message) {
            LogUtil.d(TAG, "onMessage " + message);
        }

        @Override
        public void onPrintContext(String context) {
            LogUtil.d(TAG, "printContext " + context);
        }

        @Override
        public void onFoundIdItem(ViewNode result) {
            LogUtil.d(TAG, "onFoundIdItem " + result.toString());

        }

        @Override
        public void onFoundClassItem(ViewNode result) {
            LogUtil.d(TAG, "onFoundClassItem " + result.toString());
            if (result.type == ViewNode.Type.ARROW_TOP) {
                arrowTopNode = result;
            }
        }

    };

    public List<ViewNode> getViewNodes() {
        return viewNodes;
    }

    public ViewNode getArrowTopNode() {
        return arrowTopNode;
    }

    public void quit() {
        LogUtil.d(TAG, "quit");
    }

}
