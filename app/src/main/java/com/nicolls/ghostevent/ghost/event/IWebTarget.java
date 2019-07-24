package com.nicolls.ghostevent.ghost.event;

public interface IWebTarget extends ITarget {
    void onParseWebStart();

    void foundAdvert(WebNode webNode);

    void foundItem(WebNode webNode);

    void onParseWebSuccess();

    void onParseWebFail();
}
