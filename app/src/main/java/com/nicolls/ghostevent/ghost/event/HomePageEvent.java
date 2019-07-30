package com.nicolls.ghostevent.ghost.event;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.provider.GoHomeEventProvider;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;

public class HomePageEvent extends GroupEvent {
    private static final String TAG = "HomePageEvent";

    private IWebTarget target;
    private GoHomeEventProvider provider;

    public HomePageEvent(IWebTarget target, EventExecutor.ExecuteCallBack executeCallBack,
                         GoHomeEventProvider provider) {
        super(target, executeCallBack);
        this.target = target;
        this.provider = provider;
    }

    @Override
    public Completable exe(AtomicBoolean cancel) {
        final Semaphore semaphore = new Semaphore(0, true);
        List<BaseEvent> list = new ArrayList<>();
        target.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                final WebView webView = (WebView) target;
                if (webView.canGoBack()) {
                    list.addAll(provider.getParams());
                }
                LogUtil.d(TAG, "semaphore release");
                semaphore.release();
            }
        });
        try {
            semaphore.acquire();
            LogUtil.d(TAG, "semaphore acquired");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (BaseEvent event : list) {
            addEvent(event);
        }
        return super.exe(cancel);
    }
}
