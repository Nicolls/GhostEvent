package com.nicolls.ghostevent.ghost.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickEvent;
import com.nicolls.ghostevent.ghost.event.ClickRedirectEvent;
import com.nicolls.ghostevent.ghost.event.EventExecutor;
import com.nicolls.ghostevent.ghost.event.GroupEvent;
import com.nicolls.ghostevent.ghost.event.HomePageEvent;
import com.nicolls.ghostevent.ghost.event.IEventHandler;
import com.nicolls.ghostevent.ghost.event.IWebTarget;
import com.nicolls.ghostevent.ghost.event.LoadJsEvent;
import com.nicolls.ghostevent.ghost.event.LoadPageEvent;
import com.nicolls.ghostevent.ghost.event.PageGoBackEvent;
import com.nicolls.ghostevent.ghost.event.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.SlideEvent;
import com.nicolls.ghostevent.ghost.event.SmoothSlideEvent;
import com.nicolls.ghostevent.ghost.event.TouchPoint;
import com.nicolls.ghostevent.ghost.event.ViewEventHandler;
import com.nicolls.ghostevent.ghost.event.WebNode;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.parse.SingleParser;
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
    private LoadJsEvent loadJsEvent;
    private SmoothSlideEvent smoothSlide;
    private ScrollVerticalEvent scrollEvent;
    private GroupEvent recordEvent;
    private LoadPageEvent loadPageEvent;
    private WebParseEvent parseEvent;
    private List<WebNode> webNodes = new ArrayList<>();

    private final EventExecutor.ExecuteCallBack executeCallBack = new EventExecutor.ExecuteCallBack() {
        @Override
        public void onSuccess(int eventId) {
            LogUtil.d(TAG,"onSuccess");
        }

        @Override
        public void onFail(int eventId) {
            LogUtil.d(TAG,"onFail");
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
        addJavascriptInterface(SingleParser.singleParser,"jsParser");
        initEvents();
        initWebView();
    }

    private void initEvents() {
        // single event
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
        loadJsEvent = new LoadJsEvent(this);
        parseEvent = new WebParseEvent(this);
        // add ghost event
        ghostEventList.add(scrollEvent);
        ghostEventList.add(scrollEvent);
        ghostEventList.add(click);
    }

    private void initWebView() {
        setWebViewClient(new GhostWebViewClient(redirectHandler));
        setOnTouchListener(new GhostWebViewTouchEvent(this, redirectHandler, recordEvent));
    }

    /**
     * 开始
     *
     * @param url
     */
    public void start(String url) {
        LogUtil.d(TAG,"start");
        isRecord = false;
        loadPageEvent = new LoadPageEvent(this, redirectHandler, url);
        eventExecutor.execute(loadPageEvent);
        eventExecutor.execute(loadJsEvent);
        eventExecutor.execute(click);
    }

    /**
     * 停止
     */
    public void stop() {
        LogUtil.d(TAG,"stop");
        isRecord = false;
        eventExecutor.shutDown();
    }

    /**
     * 重载
     *
     * @param url
     */
    public void reLoad(String url) {
        LogUtil.d(TAG,"reLoad");
        start(url);
    }

    /**
     * 解析
     */
    public void onParse() {
        LogUtil.d(TAG,"onParse");
        webNodes.clear();
        eventExecutor.execute(parseEvent);
    }

    /**
     * 播放解析
     */
    public void onPlayParse() {
        LogUtil.d(TAG, "onPlayParse");
        if (webNodes.size() > 3) {
            WebNode webNode = webNodes.get(3);
            LogUtil.d(TAG, "onPlayParse webNode:" + webNode.toString());
            ClickEvent clickEvent = new ClickEvent(this, TouchPoint.obtainClick(webNode.left, webNode.top));
            eventExecutor.execute(clickEvent);
        }

    }

    /**
     * 录制
     */
    public void record() {
        LogUtil.d(TAG,"record");
        recordEvent.removeAllEvents();
        isRecord = true;
    }

    public boolean isRecord() {
        LogUtil.d(TAG,"isRecord");
        return this.isRecord;
    }

    /**
     * 播放
     */
    public void play() {
        LogUtil.d(TAG,"play");
        isRecord = false;
        eventExecutor.execute(recordEvent);
    }

    /**
     * 回首页
     */
    public void goHome() {
        LogUtil.d(TAG,"goHome");
        recordEvent.addEvent(homePageEvent);
        eventExecutor.execute(homePageEvent);
    }

    /**
     * 重试
     */
    private void retry() {
        LogUtil.d(TAG,"retry");
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

    @Override
    public void onParseWebStart() {
        LogUtil.d(TAG, "onParseWebStart");
        webNodes.clear();
    }

    @Override
    public void foundAdvert(WebNode webNode) {
        LogUtil.d(TAG, "foundAdvert " + webNode.toString());
    }

    @Override
    public void foundItem(WebNode webNode) {
        LogUtil.d(TAG, "foundItem " + webNode.toString());
        webNodes.add(webNode);
    }

    @Override
    public void onParseWebSuccess() {
        LogUtil.d(TAG, "onParseWebSuccess");
    }

    @Override
    public void onParseWebFail() {
        LogUtil.d(TAG, "onParseWebFail");
    }


}
