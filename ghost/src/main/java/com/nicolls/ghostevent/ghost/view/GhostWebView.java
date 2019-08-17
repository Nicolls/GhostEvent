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
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.request.EventReporter;
import com.nicolls.ghostevent.ghost.request.IEventReport;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;
import com.nicolls.ghostevent.ghost.utils.Probability;

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
    private final IEventReport eventReport = EventReporter.getInstance();
    private final EventExecutor.ExecuteCallBack executeCallBack = new EventExecutor.ExecuteCallBack() {
        @Override
        public void onSuccess(BaseEvent event) {
            LogUtil.d(TAG, "onSuccess " + event.getName());
            if (event.getParent() == null) {
                LogUtil.d(TAG, "an instance event ");
                BaseEvent generateEvent = probability.generateEvent(GhostWebView.this, webViewClient.getCurrentUrl());
                if (generateEvent == null) {
                    LogUtil.d(TAG, "advert click count enough:" + probability.getAdvertClickCount());
                    stop();
                    return;
                }
                eventExecutor.execute(generateEvent);
            }
        }

        @Override
        public void onFail(BaseEvent event) {
            LogUtil.d(TAG, "onFail");
            retry();
        }

        @Override
        public void onTimeOut(BaseEvent event) {
            LogUtil.d(TAG, "onTimeOut");
            retry();
        }
    };
    private final IEventHandler eventHandler = new ViewEventHandler(this);
    private EventExecutor eventExecutor = new EventExecutor();
    private final RedirectHandler redirectHandler = new RedirectHandler();
    private final EventBuilder eventBuilder = new EventBuilder(getContext(), redirectHandler, executeCallBack);
    private boolean isRecord = false;
    private Probability probability;
    private GhostWebViewTriggerEvent ghostWebViewTriggerEvent;
    private GhostWebViewClient webViewClient;

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
        webViewClient = new GhostWebViewClient(this, redirectHandler);
        setWebViewClient(webViewClient);
        ghostWebViewTriggerEvent = new GhostWebViewTriggerEvent(this, redirectHandler, executeCallBack);
        setOnTouchListener(ghostWebViewTriggerEvent);
        probability = new Probability(eventBuilder);
        ParseManager.getInstance().init(this);
    }

    /**
     * 开始
     *
     * @param url
     */
    public void start(String url) {
        LogUtil.d(TAG, "start " + url);
        this.url = url;
        isRecord = false;
        EventReporter.getInstance().uploadEvent(Constants.EVENT_LOAD_UNION_URL,Constants.EVENT_TARGET_WEBVIEW,"");
        eventExecutor.execute(eventBuilder.getLoadPageEvent(this, url));
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
    }

    /**
     * 播放解析
     */
    public void onPlayParse() {
        LogUtil.d(TAG, "playParse");

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
//        List<BaseEvent> randomEvents = eventBuilder.getRandomEvents(this, 10, true);
//        eventExecutor.execute(randomEvents);
        eventExecutor.execute(eventBuilder.getSlideUp(this));
        eventExecutor.execute(eventBuilder.getSecondNewsScrollAndClickReadMoreNodeEvent(this));
    }

    /**
     * 回首页
     */
    public void goHome() {
        LogUtil.d(TAG, "goHome");
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
        LogUtil.d(TAG, "retry times:" + tryTimes);
        if (tryTimes > Constants.MAX_TRY_TIMES) {
            LogUtil.d(TAG, "tryTimes>MAX_TRY_TIMES");
            return;
        }

        if (isRetrying) {
            LogUtil.d(TAG, "retrying...");
            return;
        }
        isRetrying = true;
        eventReport.uploadEvent(Constants.EVENT_RETRY, Constants.EVENT_TARGET_WEBVIEW, "");
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
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        ghostWebViewTriggerEvent.onScrollChanged(l, t, oldl, oldt);
    }

    public void onScrollStateChanged(ScrollState state) {
        LogUtil.d(TAG, "onScrollStateChanged " + state);
    }


}
