package com.nicolls.ghostevent.ghost.event.enclosure;

import android.annotation.SuppressLint;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.parse.JsBaseInterface;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class LoadJsInterfaceEvent extends BaseEvent {
    private static final String TAG = "LoadJsInterfaceEvent";
    private IWebTarget target;
    private List<JsBaseInterface> jsInterfaces = new ArrayList<>();

    public LoadJsInterfaceEvent(IWebTarget target, JsBaseInterface jsInterface) {
        super(target);
        this.target = target;
        if (jsInterface != null) {
            jsInterfaces.add(jsInterface);
        }
    }

    public LoadJsInterfaceEvent(IWebTarget target, List<JsBaseInterface> jsInterfaces) {
        super(target);
        this.target = target;
        this.jsInterfaces.clear();
        this.jsInterfaces.addAll(jsInterfaces);
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public Completable exe(final AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    return;
                }
                LogUtil.d(TAG, "start to load LoadJsInterface!");
                final WebView webView = (WebView) target;
                for (JsBaseInterface jsInterface : jsInterfaces) {
                    webView.removeJavascriptInterface(jsInterface.getName());
                    webView.addJavascriptInterface(jsInterface, jsInterface.getName());
                }
                LogUtil.d(TAG, "load LoadJsInterface completed");
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public boolean needRetry() {
        return true;
    }

    public long getExecuteTimeOut() {
        return getExtendsTime();
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