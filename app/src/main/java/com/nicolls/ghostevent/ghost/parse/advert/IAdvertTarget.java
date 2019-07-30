package com.nicolls.ghostevent.ghost.parse.advert;

import com.nicolls.ghostevent.ghost.parse.IJsTarget;
import com.nicolls.ghostevent.ghost.parse.ViewNode;

public interface IAdvertTarget extends IJsTarget {
    void onFoundItem(ViewNode result);

    void onFoundItemHtml(String result);

    void onMessage(String message);

    void onPrintContext(String context);

    void onFoundIdItem(ViewNode result);

    void onFoundClassItem(ViewNode result);
}
