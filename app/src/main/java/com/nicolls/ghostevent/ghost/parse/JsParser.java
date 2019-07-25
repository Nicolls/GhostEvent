package com.nicolls.ghostevent.ghost.parse;

import android.content.Context;
import android.graphics.Rect;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.nicolls.ghostevent.ghost.Constants;
import com.nicolls.ghostevent.ghost.event.IWebTarget;
import com.nicolls.ghostevent.ghost.event.WebNode;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.acl.LastOwnerException;
import java.util.concurrent.Semaphore;

public class JsParser implements IWebParser {
    private static final String TAG = "JsParser";
    private final Semaphore semaphore;
    private final IWebTarget target;

    public JsParser(IWebTarget target, Semaphore semaphore) {
        this.semaphore = semaphore;
        this.target = target;
    }

    @JavascriptInterface
    public void parseStart(){
        LogUtil.d(TAG,"parseStart");
        target.onParseWebStart();
    }

    @JavascriptInterface
    public void parseSuccess(){
        LogUtil.d(TAG,"parseSuccess");
        semaphore.release();
        target.onParseWebSuccess();
    }

    @JavascriptInterface
    public void parseFail(){
        LogUtil.d(TAG,"parseFail");
        semaphore.release();
        target.onParseWebFail();
    }

    @JavascriptInterface
    public void foundItem(String item){
        LogUtil.d(TAG,"foundItem "+item);
        WebNode webNode=JSON.parseObject(item,WebNode.class);
        target.foundItem(webNode);
    }

    @JavascriptInterface
    public void foundAdvert(String item){
        LogUtil.d(TAG,"foundAdvert "+item);
        WebNode webNode=JSON.parseObject(item,WebNode.class);
        target.foundAdvert(webNode);
    }

    @Override
    public void parse() {
        final WebView webView = (WebView) target;
        webView.loadUrl(Constants.JS_FUNCTION_FIND_ITEM);
        LogUtil.d(TAG, "parse load javascript wait to found");
    }
}