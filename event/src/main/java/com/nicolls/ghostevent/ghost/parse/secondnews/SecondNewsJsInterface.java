package com.nicolls.ghostevent.ghost.parse.secondnews;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.nicolls.ghostevent.ghost.parse.IJsInterface;
import com.nicolls.ghostevent.ghost.parse.model.DomNode;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.Charset;

public class SecondNewsJsInterface implements IJsInterface {
    private static final String TAG = "SecondNewsJsInterface";
    private static final String NAME = "secondNewsParser";
    private static final String FILE_NAME = "secondNewsParser.js";
    private final Context context;
    private final ISecondNewsTarget target;

    public SecondNewsJsInterface(Context context, ISecondNewsTarget target) {
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
            DomNode domNode = new DomNode(new JSONObject(item));
            ViewNode.Type type = ViewNode.Type.OTHER;
            if (domNode.className.contains(Constants.DIV_CLASSNAME_MAIN_ICON)) {
                type = ViewNode.Type.MAIN_ICON;
            } else if (domNode.className.contains(Constants.DIV_CLASSNAME_READ_MORE)) {
                type = ViewNode.Type.READ_MORE;
            } else if (domNode.className.contains(Constants.DIV_CLASSNAME_ARROW_TOP)) {
                type = ViewNode.Type.ARROW_TOP;
            } else if (domNode.className.contains(Constants.DIV_CLASSNAME_ADVERT_TOP)) {
                type = ViewNode.Type.ADVERT_TOP;
            } else if (domNode.className.contains(Constants.DIV_CLASSNAME_NEWS)) {
                type = ViewNode.Type.NEWS;
            } else if (domNode.className.contains(Constants.DIV_CLASSNAME_ADVERT)) {
                type = ViewNode.Type.ADVERT;
            } else if (domNode.className.contains(Constants.DIV_CLASSNAME_VIDEO)) {
                type = ViewNode.Type.NEWS;
            }
            ViewNode viewNode = new ViewNode(domNode, type);
            LogUtil.d(TAG, "onFoundItem :" + viewNode.toString());
            target.onFoundItem(viewNode);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "onFoundItem json parse error " + e);
        }
    }

    @JavascriptInterface
    public void onFoundItemHtml(String item) {
        LogUtil.d(TAG, "onFoundItemHtml " + item);

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

}
