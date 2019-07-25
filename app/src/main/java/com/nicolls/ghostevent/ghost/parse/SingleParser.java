package com.nicolls.ghostevent.ghost.parse;

import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSON;
import com.nicolls.ghostevent.ghost.event.WebNode;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

public class SingleParser {
    private static final String TAG="SingleParser";
    public static final SingleParser singleParser=new SingleParser();
    public static final String NAME="jsParser";

    @JavascriptInterface
    public void onParseStart(){
        LogUtil.d(TAG,"onParseStart");
    }

    @JavascriptInterface
    public void onParseSuccess(){
        LogUtil.d(TAG,"onParseSuccess");
    }

    @JavascriptInterface
    public void onParseFail(){
        LogUtil.d(TAG,"onParseFail");

    }

    @JavascriptInterface
    public void onFoundItem(String item){
        LogUtil.d(TAG,"onFoundItem "+item);
        WebNode webNode= JSON.parseObject(item,WebNode.class);
    }

    @JavascriptInterface
    public void onFoundItemHtml(String item){
        LogUtil.d(TAG,"onFoundItemHtml "+item);

    }

    @JavascriptInterface
    public void onFoundAdvert(String item){
        LogUtil.d(TAG,"onFoundAdvert "+item);
        WebNode webNode=JSON.parseObject(item,WebNode.class);
    }

    @JavascriptInterface
    public void onFetchHtml(String html){
        LogUtil.d(TAG,"onFetchHtml");
        LogUtil.d(TAG,html);
    }

}
