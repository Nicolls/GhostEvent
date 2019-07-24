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
        final WebView webView= (WebView) target;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this,"jsParser");
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
        webView.loadUrl(getJsScript(webView.getContext()));
        LogUtil.d(TAG, "parse load javascript wait to found");
    }

    private String getJsScript(Context context){
        String html="file:////android_asset/advert.html";
        String jsFile="parse.js";
        String js="javascript:window.jsParser.foundItem('yes i am')";
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