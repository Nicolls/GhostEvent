package com.nicolls.ghostevent.ghost.parse.advert;

import com.nicolls.ghostevent.ghost.parse.WebNode;
import com.nicolls.ghostevent.ghost.parse.IJsTarget;

public interface IAdvertTarget extends IJsTarget {
    void onFoundItem(WebNode result);
    void onFoundItemHtml(String result);
    void onFoundAdvert(WebNode result);
}
