package com.nicolls.ghostevent.ghost.parse;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

public abstract class JsBaseInterface implements IJsInterface {
    private static final String TAG="JsBaseInterface";
    private Context context;
    private IJsTarget target;
    public JsBaseInterface(Context context, IJsTarget target){
        this.context=context.getApplicationContext();
        this.target=target;
    }

    @JavascriptInterface
    public void onParseStart(){
        LogUtil.d(TAG,"onParseStart");
        target.onParseStart();
    }

    @JavascriptInterface
    public void onParseSuccess(){
        LogUtil.d(TAG,"onParseSuccess");
        target.onParseSuccess();
    }

    @JavascriptInterface
    public void onParseFail(){
        LogUtil.d(TAG,"onParseFail");
        target.onParseFail();
    }

    @JavascriptInterface
    public void onCurrentPageHtml(String html){
        LogUtil.d(TAG,"onCurrentPageHtml");
//        LogUtil.d(TAG,html);
        target.onCurrentPageHtml(html);
    }
}
