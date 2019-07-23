package com.nicolls.ghostevent.ghost.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickEvent;
import com.nicolls.ghostevent.ghost.event.ClickRedirectEvent;
import com.nicolls.ghostevent.ghost.event.EventExecutor;
import com.nicolls.ghostevent.ghost.event.GroupEvent;
import com.nicolls.ghostevent.ghost.event.IEventHandler;
import com.nicolls.ghostevent.ghost.event.IWebTarget;
import com.nicolls.ghostevent.ghost.event.PageGoBackEvent;
import com.nicolls.ghostevent.ghost.event.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.SlideEvent;
import com.nicolls.ghostevent.ghost.event.SmoothSlideEvent;
import com.nicolls.ghostevent.ghost.event.TouchPoint;
import com.nicolls.ghostevent.ghost.event.ViewEventHandler;
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
    private int displayWidth;
    private int displayHeight;
    private EventExecutor eventExecutor = new EventExecutor();
    private final RedirectHandler redirectHandler = new RedirectHandler();
    private final List<BaseEvent> ghostEventList = new ArrayList<>();
    private IEventHandler eventHandler;
    private int tryTimes = 0;

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
        CookieManager.getInstance().removeSessionCookie();
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setWebChromeClient(new GhostWebChromeClient());
        setWebViewClient(new GhostWebViewClient());
        clearCache(true);
        clearHistory();
        displayWidth = GhostUtils.displayWidth;
        displayHeight = GhostUtils.displayHeight;
        eventHandler = new ViewEventHandler(this);
        eventExecutor.setExecuteCallBack(executeCallBack);
        initEvents();
    }

    private void initEvents() {
        final BaseEvent slideTop = new SlideEvent(this, SlideEvent.Direction.TOP);
        final BaseEvent slideBottom = new SlideEvent(this, SlideEvent.Direction.BOTTOM);

        final BaseEvent redirectClick = new ClickRedirectEvent(redirectHandler, GhostWebView.this, TouchPoint.obtainClick(displayWidth / 2, displayHeight / 2));

        final BaseEvent click = new ClickEvent(this, TouchPoint.obtainClick(displayWidth / 2, displayHeight / 2));

        final GroupEvent groupEvent = new GroupEvent(this, slideTop, slideBottom, slideTop, slideBottom);

        final BaseEvent goBackEvent = new PageGoBackEvent(redirectHandler, GhostWebView.this);

        final BaseEvent smoothSlide = new SmoothSlideEvent(this,
                TouchPoint.obtainDown(displayWidth / 2, displayHeight - displayHeight / 4),
                TouchPoint.obtainUp(displayWidth / 2, displayHeight / 3));

        final BaseEvent scrollEvent = new ScrollVerticalEvent(this, displayHeight);
        ghostEventList.add(scrollEvent);
        ghostEventList.add(scrollEvent);
        ghostEventList.add(click);
//        ghostEventList.add(smoothSlide);
//        ghostEventList.add(slideTop);
//        ghostEventList.add(slideBottom);
//        ghostEventList.add(groupEvent);
//        ghostEventList.add(redirectClick);
//        ghostEventList.add(slideTop);
//        ghostEventList.add(goBackEvent);
//        ghostEventList.add(click);
    }

    private final EventExecutor.ExecuteCallBack executeCallBack = new EventExecutor.ExecuteCallBack() {
        @Override
        public void onSuccess(int eventId) {

        }

        @Override
        public void onFail(int eventId) {
            tryTimes++;
            if (tryTimes <= MAX_TRY_TIMES) {
                retry();
            }
        }
    };

    private void retry() {
        isRecord = false;
        eventExecutor.retry(ghostEventList);
    }

    public void start(String url) {
        isRecord = false;
        loadUrl(url);
    }

    public void reload(String url) {
        isRecord = false;
        loadUrl(url);
    }

    public void stop() {
        isRecord = false;
        eventExecutor.shutDown();
    }

    private boolean isRecord = false;

    public void record() {
        ghostEventList.clear();
        isRecord = true;
    }

    public void play() {
        isRecord = false;
        eventExecutor.execute(ghostEventList);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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


    private class GhostWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }

    private boolean first = true;

    private class GhostWebViewClient extends WebViewClient {
        private boolean isError = false;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isError = false;
            redirectHandler.notifyStart();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtil.d(TAG, "onPageFinished");
            LogUtil.d(TAG, "haha:" + getWidth() + " " + getHeight() + " " + displayWidth + " " + displayHeight);
            if (!isError) {
                onSuccess();
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            LogUtil.d(TAG, "onReceivedError");
            isError = true;
            redirectHandler.notifyFail();
        }

        /**
         * 加载成功
         */
        private void onSuccess() {
            LogUtil.d(TAG, "load onSuccess");
            redirectHandler.notifySuccess();
            if (first) {
                firstEvent();
            }
        }

    }

    private void firstEvent() {
        first = false;
//        eventExecutor.execute(ghostEventList);
    }

    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private long lastMoveTime = 0;
    private List<TouchPoint> moves = new ArrayList<>();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG, "Web-dispatchTouchEvent ev:" + MotionEvent.actionToString(ev.getAction()));
        LogUtil.d(TAG, "Web-dispatchTouchEvent x-y :" + ev.getX() + "-" + ev.getY() + " rawX-rawY:" + ev.getRawX() + "-" + ev.getRawY());

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastMoveTime = System.currentTimeMillis();
                downX = ev.getX();
                downY = ev.getY();
                moves.clear();
                break;
            case MotionEvent.ACTION_MOVE:
                long moveTime = System.currentTimeMillis() - lastMoveTime;
                LogUtil.d(TAG, "moveTime :" + moveTime);
                if (moveTime > 0 && moveTime < 2000) {
                    TouchPoint move = new TouchPoint(new PointF(ev.getX(), ev.getY()), moveTime);
                    moves.add(move);
                }
                lastMoveTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                upX = ev.getX();
                upY = ev.getY();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d(TAG, "Web-dispatchTouchEvent scrollY " + getScrollY());
                    }
                }, 1000);
                if (!isRecord) {
                    break;
                }
                if (Math.abs(upX - downX) < 5 && Math.abs(upY - downY) < 5) {
                    // click
                    LogUtil.d(TAG, "add click");

                    BaseEvent clickRecdirect = new ClickRedirectEvent(redirectHandler, this, TouchPoint.obtainClick(upX, upY));
                    LogUtil.d(TAG, "Web-dispatchTouchEvent cllick:" + clickRecdirect.toString());

                    ghostEventList.add(clickRecdirect);
                } else {
                    // slide
                    LogUtil.d(TAG, "add slide");
                    TouchPoint from = TouchPoint.obtainDown(downX, downY);
                    TouchPoint to = TouchPoint.obtainDown(upX, upY);
//                    BaseEvent slideEvent = new SlideEvent(this, from, to);
                    BaseEvent slideEvent = new SlideEvent(this, from, to, moves);
                    LogUtil.d(TAG, "Web-dispatchTouchEvent slide:" + slideEvent.toString());
                    ghostEventList.add(slideEvent);
                }
                moves.clear();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
