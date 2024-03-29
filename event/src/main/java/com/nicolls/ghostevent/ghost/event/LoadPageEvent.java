package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.behavior.LoadWebEventBehavior;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadPageEvent extends BaseEvent {
    private static final String TAG = "LoadPageEvent";
    private IWebTarget target;
    private LoadWebEventBehavior eventBehavior;
    private String url;

    public LoadPageEvent(IWebTarget target, String url, LoadWebEventBehavior eventBehavior) {
        this.target = target;
        this.url = url;
        this.eventBehavior = eventBehavior;
    }

    @Override
    public void exe(final AtomicBoolean cancel, final EventCallBack eventCallBack) {
        ExecutorService executorService = target.getEventTaskPool();
        if (executorService == null || executorService.isShutdown() || executorService.isTerminated()) {
            LogUtil.w(TAG, "executorService shutdown ");
            if (eventCallBack != null) {
                cancel.set(true);
                eventCallBack.onFail(null);
            }
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (eventBehavior != null) {
                    eventBehavior.onStart(cancel);
                }
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    if (eventCallBack != null) {
                        eventCallBack.onComplete();
                    }
                    return;
                }
                target.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) target;
                        webView.loadUrl(url);
                    }
                });
                if (eventBehavior != null) {
                    boolean isOk = eventBehavior.onEnd(cancel);
                    if (!isOk && eventCallBack != null) {
                        eventCallBack.onFail(new RuntimeException("load page error"));
                        return;
                    }
                }
                if (eventCallBack != null) {
                    eventCallBack.onComplete();
                }
            }
        });
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public String getDescribe() {
        return null;
    }

    @Override
    public long getExecuteTimeOut() {
        return getExtendsTime() + (eventBehavior == null ? 0 : eventBehavior.getTimeOut());
    }
}
