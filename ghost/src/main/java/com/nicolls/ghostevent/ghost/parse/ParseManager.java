package com.nicolls.ghostevent.ghost.parse;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.parse.home.HomeJsInterface;
import com.nicolls.ghostevent.ghost.parse.home.IHomeTarget;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;
import com.nicolls.ghostevent.ghost.parse.secondnews.ISecondNewsTarget;
import com.nicolls.ghostevent.ghost.parse.secondnews.SecondNewsJsInterface;
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
    private SecondNewsJsInterface secondNewsJsInterface;

    public void init(WebView webView) {
        homeJsInterface = new HomeJsInterface(webView.getContext(), homeTarget);
        secondNewsJsInterface = new SecondNewsJsInterface(webView.getContext(), secondNewsTarget);

        webView.removeJavascriptInterface(homeJsInterface.getName());
        webView.removeJavascriptInterface(secondNewsJsInterface.getName());
        webView.addJavascriptInterface(homeJsInterface, homeJsInterface.getName());
        webView.addJavascriptInterface(secondNewsJsInterface, secondNewsJsInterface.getName());
    }

    public void loadJsInterface(WebView webView, String url) {
        GhostUtils.Page page = GhostUtils.currentPage(url);
        switch (page) {
            case HOME:
                webView.loadUrl(homeJsInterface.getJsText());
                break;
            case SECOND_NEWS:
                webView.loadUrl(secondNewsJsInterface.getJsText());
                break;
            case SECOND_ADVERT:

                break;
            case OTHER:

                break;

        }
    }

    private ViewNode homeArrowTop;

    private ViewNode mainIcon;

    private ViewNode readMore;

    private ViewNode currentParseNode;

    private ViewNode advertTop;

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
            LogUtil.d(TAG, "homeTarget onFoundClassItem " + result.toString());
            if (ViewNode.Type.ARROW_TOP == result.type) {
                homeArrowTop = result;
                currentParseNode = homeArrowTop;
            }
        }
    };

    private final ISecondNewsTarget secondNewsTarget = new ISecondNewsTarget() {
        @Override
        public void onMessage(String message) {

        }

        @Override
        public void onFoundItem(ViewNode result) {
            LogUtil.d(TAG, "secondNewsTarget onFoundItem " + result.toString());
            currentParseNode = result;

            if (result.type == ViewNode.Type.READ_MORE) {
                readMore = result;
            } else if(result.type == ViewNode.Type.ADVERT_TOP){
                advertTop=result;
            }
        }
    };

    public ViewNode getHomeArrowTop() {
        return homeArrowTop;
    }

    public ViewNode getCurrentParseNode() {
        return currentParseNode;
    }

    public ViewNode getMainIcon() {
        return mainIcon;
    }

    public ViewNode getReadMore() {
        return readMore;
    }

    public ViewNode getAdvertTop() {
        return advertTop;
    }

}
