package com.nicolls.ghostevent.ghost.parse.advert;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSON;
import com.nicolls.ghostevent.ghost.parse.DomNode;
import com.nicolls.ghostevent.ghost.parse.JsBaseInterface;
import com.nicolls.ghostevent.ghost.parse.ViewNode;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.io.InputStream;
import java.nio.charset.Charset;

public class AdvertJsInterface extends JsBaseInterface {
    private static final String TAG = "AdvertJsInterface";
    private static final String NAME = "advertParser";
    private static final String FILE_NAME = "advertParser.js";
    private final Context context;
    private final IAdvertTarget target;

    public AdvertJsInterface(Context context, IAdvertTarget target) {
        super(context, target);
        this.context = context.getApplicationContext();
        this.target = target;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getJsText() {
        return getJsScript(context);
    }

    private String getJsScript(Context context) {
        String js = "";
        try {
            InputStream inputStream = context.getAssets().open(FILE_NAME);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data, 0, data.length);
            String str = new String(data, Charset.forName("UTF-8"));
            str = str.trim();
//            LogUtil.d(TAG, "js code:" + str);
            js = "javascript:" + str;

        } catch (Exception e) {
            LogUtil.e(TAG, "load js file error ", e);
        }
        return js;
    }

    @JavascriptInterface
    public void onFoundItem(String item) {
        LogUtil.d(TAG, "onFoundItem " + item);
        try {
            DomNode domNode = JSON.parseObject(item, DomNode.class);
            ViewNode.Type type = ViewNode.Type.OTHER;
            if (domNode.className.contains(Constants.DIV_CLASSNAME_NEWS)) {
                type = ViewNode.Type.NEWS;
            } else if (domNode.className.contains(Constants.DIV_CLASSNAME_ADVERT)) {
                type = ViewNode.Type.ADVERT;
            } else if (domNode.className.contains(Constants.DIV_CLASSNAME_VIDEO)) {
                type = ViewNode.Type.VIDEO;
            }
            ViewNode viewNode = new ViewNode(domNode, type);
            LogUtil.d(TAG, "onFoundItem :" + viewNode.toString());
            target.onFoundItem(viewNode);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "onFoundItem json parse error " + e);
            target.onJsCallBackHandleError();
        }
    }

    @JavascriptInterface
    public void onFoundItemHtml(String item) {
//        LogUtil.d(TAG, "onFoundItemHtml " + item);
        target.onFoundItemHtml(item);

    }

    @JavascriptInterface
    public void onMessage(String message) {
        LogUtil.d(TAG, "onMessage:" + message);
        target.onMessage(message);
    }

    @JavascriptInterface
    public void onFoundIdItem(String item) {
        LogUtil.d(TAG, "onFoundIdItem:" + item);

    }

    @JavascriptInterface
    public void onFoundClassItem(String item) {
        LogUtil.d(TAG, "onFoundClassItem:" + item);
        try {
            DomNode domNode = JSON.parseObject(item, DomNode.class);
            if (TextUtils.equals(Constants.DIV_CLASSNAME_ARROW_TOP, domNode.className)) {
                ViewNode viewNode = new ViewNode(domNode, ViewNode.Type.ARROW_TOP);
                target.onFoundClassItem(viewNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "onFoundItem json parse error " + e);
            target.onJsCallBackHandleError();
        }
    }

    @JavascriptInterface
    public void onPrintContext(String context) {
        LogUtil.d(TAG, "onPrintContext:" + context);
        target.onPrintContext(context);
    }
}
