package com.nicolls.ghostevent.ghost.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.EventBuilder;
import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.IEventHandler;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.core.ViewEventHandler;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.parse.ViewNode;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

/**
 * author:mengjiankang
 * date:2018/11/21
 * <p>
 * </p>
 */
public class GhostWebView extends BaseWebView implements IWebTarget {
    private static final String TAG = "GhostWebView";
    private int tryTimes = 0;
    private String url;
    private boolean isRetrying = false;
    private final EventExecutor.ExecuteCallBack executeCallBack = new EventExecutor.ExecuteCallBack() {
        @Override
        public void onSuccess(int eventId) {
            LogUtil.d(TAG, "onSuccess");
        }

        @Override
        public void onFail(int eventId) {
            LogUtil.d(TAG, "onFail");
            retry();
        }

        @Override
        public void onTimeOut(int eventId) {
            LogUtil.d(TAG, "onTimeOut");
            retry();
        }
    };
    private final IEventHandler eventHandler = new ViewEventHandler(this);
    private EventExecutor eventExecutor = new EventExecutor();
    private final RedirectHandler redirectHandler = new RedirectHandler();
    private final EventBuilder eventBuilder = new EventBuilder(getContext(), redirectHandler, executeCallBack);
    private boolean isRecord = false;
    private GhostWebViewTriggerEvent ghostWebViewTriggerEvent;

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
        eventExecutor.setExecuteCallBack(executeCallBack);
        setWebChromeClient(new GhostWebChromeClient());
        setWebViewClient(new GhostWebViewClient(redirectHandler));
        ghostWebViewTriggerEvent = new GhostWebViewTriggerEvent(this, redirectHandler, executeCallBack);
        setOnTouchListener(ghostWebViewTriggerEvent);
    }

    /**
     * 开始
     *
     * @param url
     */
    public void start(String url) {
        LogUtil.d(TAG, "start");
        this.url = url;
        isRecord = false;
        eventExecutor.execute(eventBuilder.buildAutoEvent(this, url, 10, true));
    }

    /**
     * 停止
     */
    public void stop() {
        LogUtil.d(TAG, "stop");
        isRecord = false;
        eventExecutor.shutDown().subscribe();
        eventHandler.quit();
        eventBuilder.quit();
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
        LogUtil.d(TAG, "parse");
        eventExecutor.execute(eventBuilder.getParseEvent(this));
    }

    /**
     * 播放解析
     */
    public void onPlayParse() {
        LogUtil.d(TAG, "playParse");
        BaseEvent event = eventBuilder.getCloseAdvertClickEvent(this);
        if (event != null) {
            eventExecutor.execute(event);
        }
    }

    /**
     * 录制
     */
    public void record() {
        LogUtil.d(TAG, "record");
        isRecord = true;
        ghostWebViewTriggerEvent.startRecord();
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
        eventExecutor.execute(ghostWebViewTriggerEvent.getRecordEvent());
    }

    /**
     * 随机
     */
    public void random() {
        LogUtil.d(TAG, "random");
        List<BaseEvent> randomEvents = eventBuilder.getRandomEvents(this, 10, true);
        eventExecutor.execute(randomEvents);
    }

    /**
     * 回首页
     */
    public void goHome() {
        LogUtil.d(TAG, "goHome");
        eventExecutor.execute(eventBuilder.getHomeEvent(this));
    }

    /**
     * 返回
     */
    public void runGoBack() {
        LogUtil.d(TAG, "runGoBack");
        eventExecutor.execute(eventBuilder.getGoBackEvent(this));
    }

    /**
     * 重试
     */

    private void retry() {
        tryTimes++;
        LogUtil.d(TAG,"retry times:"+tryTimes);
        if (tryTimes > Constants.MAX_TRY_TIMES) {
            LogUtil.d(TAG, "tryTimes>MAX_TRY_TIMES");
            return;
        }

        if (isRetrying) {
            LogUtil.d(TAG, "retrying...");
            return;
        }
        isRetrying = true;
        eventExecutor.reset().subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.d(TAG, "shutDown onSubscribe");
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, "reset completed");
                start(url);
                isRetrying = false;
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d(TAG, "shutdown onError");
            }
        });
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
    public void executeJs(String js) {
        LogUtil.d(TAG, "executeJs " + js);
        loadUrl(js);
    }

    @Override
    public List<ViewNode> getViewNodes() {
        return eventBuilder.getViewNodes();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        ghostWebViewTriggerEvent.onScrollChanged(l, t, oldl, oldt);
    }

    public void onScrollStateChanged(ScrollState state) {
        LogUtil.d(TAG, "onScrollStateChanged " + state);
    }
}
