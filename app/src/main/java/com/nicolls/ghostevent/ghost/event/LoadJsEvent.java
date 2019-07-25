package com.nicolls.ghostevent.ghost.event;

import android.content.Context;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.Constants;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.JsParser;
import com.nicolls.ghostevent.ghost.parse.SingleParser;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class LoadJsEvent extends BaseEvent {
    private static final String TAG = "LoadJsEvent";
    private static final long PARSE_WAIT_TIME = 2; // 秒
    private final Semaphore semaphore = new Semaphore(0, true);
    private IWebTarget target;

    public LoadJsEvent(IWebTarget target) {
        super(target);
        this.target = target;
        final WebView webView = (WebView) target;
        this.setName(TAG);
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
                        webView.loadUrl(getJsScript(webView.getContext()));
                        LogUtil.d(TAG, "do load js");
                        sleepTimes(100);
                        webView.loadUrl(Constants.JS_FETCH_WEB_HTML);
                        semaphore.release();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                LogUtil.d(TAG, "exe load js subscribe ");
                boolean ok = semaphore.tryAcquire(PARSE_WAIT_TIME, TimeUnit.SECONDS);
                if (!ok) {
                    LogUtil.w(TAG, "load js time out!");
                } else {
                    LogUtil.d(TAG, "load js completed");
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public boolean needRetry() {
        return true;
    }

    private String getJsScript(Context context){
        String jsFile="parse.js";
        String js="";
        try {
            InputStream inputStream=context.getAssets().open(jsFile);
            byte[] data=new byte[inputStream.available()];
            inputStream.read(data,0,data.length);
            String str=new String(data, Charset.forName("UTF-8"));
            str=str.trim();
            LogUtil.d(TAG,"js code:"+str);
            js="javascript:"+str;

        }catch (Exception e){
            LogUtil.e(TAG,"load js file error ",e);
        }
        return js;
    }
}
