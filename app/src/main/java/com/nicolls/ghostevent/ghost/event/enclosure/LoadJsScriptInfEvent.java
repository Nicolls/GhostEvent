package com.nicolls.ghostevent.ghost.event.enclosure;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.parse.JsBaseInterface;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONObject;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class LoadJsScriptInfEvent extends BaseEvent {
    private static final String TAG = "LoadJsInfEvent";
    private final Semaphore semaphore = new Semaphore(0, true);
    private IWebTarget target;
    private JsBaseInterface jsInterface;

    public LoadJsScriptInfEvent(IWebTarget target, JsBaseInterface jsInterface) {
        super(target);
        this.target = target;
        this.jsInterface = jsInterface;
    }

    @Override
    public Completable exe(final AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    return;
                }
                LogUtil.d(TAG, "start to load js!");
                Completable.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) target;
                        webView.loadUrl(jsInterface.getJsText());
                        LogUtil.d(TAG, "do load js");
                        target.getEventHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d(TAG, "semaphore release");
                                semaphore.release();
                            }
                        }, Constants.TIME_DEFAULT_LOAD_JS_INIT);
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "exe load js subscribe ");
                boolean ok = semaphore.tryAcquire(getExecuteTimeOut(), TimeUnit.MILLISECONDS);
                if (!ok) {
                    LogUtil.w(TAG, "load js time out!");
                    throw new RuntimeException("load js time out");
                } else {
                    LogUtil.d(TAG, "load js completed");
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public boolean needRetry() {
        return true;
    }

    @Override
    public long getExecuteTimeOut() {
        return getExtendsTime() + Constants.TIME_DEFAULT_LOAD_JS_INIT;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public String getDetail() {
        JSONObject jsonObject=new JSONObject();
        return jsonObject.toString();
    }
}