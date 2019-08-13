package com.nicolls.ghostevent.ghost.parse;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.parse.home.HomeJsInterface;
import com.nicolls.ghostevent.ghost.parse.home.IHomeTarget;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

public class ParseManager {

    private static final String TAG = "ParseManager";
    private static final ParseManager instance = new ParseManager();

    public static final ParseManager getInstance() {
        return instance;
    }

    public ParseManager() {
    }
    private HomeJsInterface homeJsInterface;
    public void init(WebView webView){
        homeJsInterface = new HomeJsInterface(webView.getContext(), homeTarget);
        webView.removeJavascriptInterface(homeJsInterface.getName());
        webView.addJavascriptInterface(homeJsInterface, homeJsInterface.getName());
    }

    public void loadJsInterface(WebView webView, String url) {
        GhostUtils.Page page = GhostUtils.currentPage(url);
        switch (page) {
            case HOME:
                webView.loadUrl(homeJsInterface.getJsText());
                break;
            case SECOND_NEWS:

                break;
            case SECOND_ADVERT:

                break;
            case OTHER:

                break;

        }
    }

    private ViewNode homeArrowTop;

    private ViewNode currentParseNode;

    private final IHomeTarget homeTarget = new IHomeTarget() {
        @Override
        public void onFoundItem(ViewNode result) {

        }

        @Override
        public void onFoundItemHtml(String result) {

        }

        @Override
        public void onMessage(String message) {

        }

        @Override
        public void onPrintContext(String context) {

        }

        @Override
        public void onFoundIdItem(ViewNode result) {

        }

        @Override
        public void onFoundClassItem(ViewNode result) {
            LogUtil.d(TAG, "onFoundClassItem " + result.toString());
            if (ViewNode.Type.ARROW_TOP == result.type) {
                homeArrowTop = result;
                currentParseNode = homeArrowTop;
            }
        }
    };

    public ViewNode getHomeArrowTop() {
        return homeArrowTop;
    }

    public ViewNode getCurrentParseNode() {
        return currentParseNode;
    }

}
