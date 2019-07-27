package com.nicolls.ghostevent.ghost.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.IEventHandler;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.core.ViewEventHandler;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickEvent;
import com.nicolls.ghostevent.ghost.event.ClickRedirectEvent;
import com.nicolls.ghostevent.ghost.event.GroupEvent;
import com.nicolls.ghostevent.ghost.event.HomePageEvent;
import com.nicolls.ghostevent.ghost.event.LoadJsInterfaceEvent;
import com.nicolls.ghostevent.ghost.event.LoadJsScriptInfEvent;
import com.nicolls.ghostevent.ghost.event.LoadPageEvent;
import com.nicolls.ghostevent.ghost.event.PageGoBackEvent;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.SlideEvent;
import com.nicolls.ghostevent.ghost.event.SmoothSlideEvent;
import com.nicolls.ghostevent.ghost.event.TouchPoint;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.JsBaseInterface;
import com.nicolls.ghostevent.ghost.parse.ViewNode;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertJsInterface;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertParser;
import com.nicolls.ghostevent.ghost.parse.advert.IAdvertTarget;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author:mengjiankang
 * date:2018/11/21
 * <p>
 * </p>
 */
public class GhostWebView extends BaseWebView implements IWebTarget {
    private static final String TAG = "GhostWebView";

    private static final int MAX_TRY_TIMES = 5;
    private EventExecutor eventExecutor = new EventExecutor();
    private final RedirectHandler redirectHandler = new RedirectHandler();
    private final List<BaseEvent> ghostEventList = new ArrayList<>();
    private IEventHandler eventHandler;
    private int tryTimes = 0;
    private boolean isRecord = false;
    /**
     * event
     */
    private SlideEvent slideTop;
    private SlideEvent slideBottom;
    private ClickEvent click;
    private ClickRedirectEvent redirectClick;
    private PageGoBackEvent goBackEvent;
    private HomePageEvent homePageEvent;
    private LoadJsScriptInfEvent loadJsInfEvent;
    private SmoothSlideEvent smoothSlide;
    private ScrollVerticalEvent scrollEvent;
    private GroupEvent recordEvent;
    private LoadPageEvent loadPageEvent;
    private LoadJsInterfaceEvent loadJsInterfaceEvent;
    private List<ViewNode> viewNodes = new ArrayList<>();

    /**
     * js interface
     */
    private JsBaseInterface advertInterface;

    private final EventExecutor.ExecuteCallBack executeCallBack = new EventExecutor.ExecuteCallBack() {
        @Override
        public void onSuccess(int eventId) {
            LogUtil.d(TAG, "onSuccess");
        }

        @Override
        public void onFail(int eventId) {
            LogUtil.d(TAG, "onFail");
            tryTimes++;
//            if (tryTimes <= MAX_TRY_TIMES) {
//                retry();
//            }
        }
    };

    public GhostWebView(Context context) {
        super(context);
        init();
    }

    public GhostWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GhostWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        eventHandler = new ViewEventHandler(this);
        eventExecutor.setExecuteCallBack(executeCallBack);
        initEvents();
        initWebView();
    }

    private void initEvents() {
        // single event
        advertInterface = new AdvertJsInterface(getContext(), advertTarget);
        loadJsInterfaceEvent = new LoadJsInterfaceEvent(this, advertInterface);

        slideTop = new SlideEvent(this, SlideEvent.Direction.TOP);
        slideBottom = new SlideEvent(this, SlideEvent.Direction.BOTTOM);

        redirectClick = new ClickRedirectEvent(redirectHandler, GhostWebView.this, TouchPoint.obtainClick(GhostUtils.displayWidth / 2, GhostUtils.displayHeight / 2));

        click = new ClickEvent(this, TouchPoint.obtainClick(GhostUtils.displayWidth / 2, GhostUtils.displayHeight / 2));

        goBackEvent = new PageGoBackEvent(redirectHandler, GhostWebView.this);

        homePageEvent = new HomePageEvent(redirectHandler, this);

        smoothSlide = new SmoothSlideEvent(this,
                TouchPoint.obtainDown(GhostUtils.displayWidth / 2, GhostUtils.displayHeight - GhostUtils.displayHeight / 4),
                TouchPoint.obtainUp(GhostUtils.displayWidth / 2, GhostUtils.displayHeight / 3));
        recordEvent = new GroupEvent(this);
        scrollEvent = new ScrollVerticalEvent(this, GhostUtils.displayHeight);
        loadPageEvent = new LoadPageEvent(this, redirectHandler, getUrl());
        loadJsInfEvent = new LoadJsScriptInfEvent(this, advertInterface);
        // add ghost event
        ghostEventList.add(scrollEvent);
        ghostEventList.add(scrollEvent);
        ghostEventList.add(click);
    }

    private void initWebView() {
        setWebChromeClient(new GhostWebChromeClient());
        setWebViewClient(new GhostWebViewClient(redirectHandler));
        setOnTouchListener(new GhostWebViewTouchEvent(this, redirectHandler, recordEvent));
    }

    /**
     * 开始
     *
     * @param url
     */
    public void start(String url) {
        LogUtil.d(TAG, "start");
        isRecord = false;
        eventExecutor.execute(loadJsInterfaceEvent);
        loadPageEvent = new LoadPageEvent(this, redirectHandler, url);
        eventExecutor.execute(loadPageEvent);
        eventExecutor.execute(loadJsInfEvent);

        // 解析
        IWebParser parseAdvert = new AdvertParser();
        WebParseEvent parseEvent = new WebParseEvent(this, parseAdvert);
        eventExecutor.execute(parseEvent);
    }

    /**
     * 停止
     */
    public void stop() {
        LogUtil.d(TAG, "stop");
        isRecord = false;
        eventExecutor.shutDown();
    }

    /**
     * 重载
     *
     * @param url
     */
    public void reLoad(String url) {
        LogUtil.d(TAG, "reLoad");
        start(url);
    }

    /**
     * 解析
     */
    public void onParse() {
        LogUtil.d(TAG, "onParse");
        viewNodes.clear();
        IWebParser parseAdvert = new AdvertParser();
        WebParseEvent parseEvent = new WebParseEvent(this, parseAdvert);
        eventExecutor.execute(parseEvent);
    }

    /**
     * 播放解析
     */
    public void onPlayParse() {
        LogUtil.d(TAG, "onPlayParse");
        if (viewNodes.size() > 3) {
            ViewNode domNode = viewNodes.get(3);
            LogUtil.d(TAG, "onPlayParse webNode:" + domNode.toString());
            ClickEvent clickEvent = new ClickEvent(this, TouchPoint.obtainClick(domNode.left, domNode.top));
            eventExecutor.execute(clickEvent);
        }

    }

    /**
     * 录制
     */
    public void record() {
        LogUtil.d(TAG, "record");
        recordEvent.removeAllEvents();
        isRecord = true;
    }

    public boolean isRecord() {
        LogUtil.d(TAG, "isRecord");
        return this.isRecord;
    }

    /**
     * 播放
     */
    public void play() {
        LogUtil.d(TAG, "play");
        isRecord = false;
        eventExecutor.execute(recordEvent);
    }

    /**
     * 回首页
     */
    public void goHome() {
        LogUtil.d(TAG, "goHome");
        recordEvent.addEvent(homePageEvent);
        eventExecutor.execute(homePageEvent);
    }

    /**
     * 重试
     */
    private void retry() {
        LogUtil.d(TAG, "retry");
        isRecord = false;
        eventExecutor.retry();
    }

    @Override
    public Handler getMainHandler() {
        return eventHandler.getMainHandler();
    }

    @Override
    public Handler getEventHandler() {
        return eventHandler.getEventHandler();
    }

    @Override
    public void doEvent(MotionEvent event) {
        eventHandler.doEvent(event);
    }

    private final IAdvertTarget advertTarget = new IAdvertTarget() {
        @Override
        public void onParseStart() {
            LogUtil.d(TAG, "onParseWebStart");
            LogUtil.d(TAG, "webview width-height:" + getWidth() + "-" + getHeight());
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
            LogUtil.d(TAG, result);
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
            LogUtil.d(TAG, "onFoundItemHtml " + result);
        }

        @Override
        public void onFoundAdvert(ViewNode result) {
            LogUtil.d(TAG, "onFoundAdvert " + result.toString());
            viewNodes.add(result);
        }
    };

    @Override
    public void executeJs(String js) {
        LogUtil.d(TAG, "executeJs " + js);
        loadUrl(js);
    }
}
