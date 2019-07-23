package com.nicolls.ghostevent.ghost.event;

public interface IWebTarget extends ITarget {
    void foundAd(WebNode webNode);

    void foundItem(WebNode webNode);
}
