package com.nicolls.ghostevent.ghost.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
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

import com.nicolls.ghostevent.ghost.utils.DisplayUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;
import com.nicolls.ghostevent.ghost.old.IEvent;
import com.nicolls.ghostevent.ghost.old.ViewEvent;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickEvent;
import com.nicolls.ghostevent.ghost.event.EventExecutor;
import com.nicolls.ghostevent.ghost.event.SlideEvent;

/**
 * author:mengjiankang
 * date:2018/11/21
 * <p>
 * </p>
 */
public class GhostWebView extends BaseWebView {
    private static final String TAG = "GhostWebView";


    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private String url;
    private IEvent event;
    private int displayWidth;
    private int displayHeight;
    private EventExecutor eventExecutor=new EventExecutor();

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
        Point display= DisplayUtils.getDisplaySize(getContext());
        displayWidth=display.x;
        displayHeight=display.y;
        event = new ViewEvent(this);
    }

    public void start(String url) {
        loadUrl(url);
    }

    public void stop() {
        eventExecutor.shutDown();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    private class GhostWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }

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
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtil.d(TAG, "onPageFinished");
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
            onError();
        }

        /**
         * 加载成功
         */
        private void onSuccess() {
            LogUtil.d(TAG,"load onSuccess");

            final BaseEvent slideTop=new SlideEvent(GhostWebView.this, SlideEvent.Direction.TOP);
            final BaseEvent slideBottom=new SlideEvent(GhostWebView.this, SlideEvent.Direction.BOTTOM);
            eventExecutor.execute(slideTop);
            eventExecutor.execute(slideBottom);
            eventExecutor.execute(slideTop);
            eventExecutor.execute(slideBottom);
            eventExecutor.execute(slideTop);
            eventExecutor.execute(slideBottom);
            eventExecutor.execute(slideTop);
            eventExecutor.execute(slideBottom);
            eventExecutor.execute(slideTop);
            eventExecutor.execute(slideBottom);
            eventExecutor.execute(slideTop);
            eventExecutor.execute(slideBottom);
            BaseEvent click=new ClickEvent(GhostWebView.this,displayWidth/2,displayHeight/2);
            eventExecutor.execute(click);

        }

        /**
         * 加载失败
         */
        private void onError() {

        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG,"onInterceptTouchEvent ev:"+MotionEvent.actionToString(ev.getAction()));
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d(TAG,"onTouchEvent ev:"+MotionEvent.actionToString(event.getAction()));
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG,"dispatchTouchEvent ev:"+MotionEvent.actionToString(ev.getAction()));
        LogUtil.d(TAG,"dispatchTouchEvent x-y :"+ev.getX()+"-"+ev.getY() +" rawX-rawY:"+ev.getRawX()+"-"+ev.getRawY());
        return super.dispatchTouchEvent(ev);
    }


}
