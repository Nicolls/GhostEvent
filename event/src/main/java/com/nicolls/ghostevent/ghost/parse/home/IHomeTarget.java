package com.nicolls.ghostevent.ghost.parse.home;

import com.nicolls.ghostevent.ghost.parse.model.ViewNode;

public interface IHomeTarget {
    void onFoundItem(ViewNode result);

    void onFoundItemHtml(String result);

    void onMessage(String message);

    void onPrintContext(String context);

    void onFoundIdItem(ViewNode result);

    void onFoundClassItem(ViewNode result);
}
