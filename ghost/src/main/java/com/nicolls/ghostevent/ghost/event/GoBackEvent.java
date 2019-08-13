package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.behavior.IEventBehavior;
import com.nicolls.ghostevent.ghost.event.behavior.LoadWebEventBehavior;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class GoBackEvent extends BaseEvent {
    private static final String TAG = "GoBackEvent";
    private IWebTarget target;
    private IEventBehavior eventBehavior;

    public GoBackEvent(IWebTarget target, LoadWebEventBehavior eventBehavior) {
        this.target = target;
        this.eventBehavior = eventBehavior;
    }

    @Override
    public Completable exe(final AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (eventBehavior != null) {
                    eventBehavior.onStart();
                }
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    return;
                }
                target.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) target;
                        if (webView.canGoBack()) {
                            webView.goBack();
                        }
                    }
                });
                if (eventBehavior != null) {
                    eventBehavior.onEnd();
                }
            }
        }).subscribeOn(Schedulers.io());
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
