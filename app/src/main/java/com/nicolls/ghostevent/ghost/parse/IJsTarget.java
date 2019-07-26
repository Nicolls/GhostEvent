package com.nicolls.ghostevent.ghost.parse;

public interface IJsTarget {
    void onParseStart();
    void onParseSuccess();
    void onParseFail();
    void onCurrentPageHtml(String result);
    void onJsCallBackHandleError();
}
