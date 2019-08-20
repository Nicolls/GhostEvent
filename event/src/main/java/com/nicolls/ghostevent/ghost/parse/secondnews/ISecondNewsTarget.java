package com.nicolls.ghostevent.ghost.parse.secondnews;

import com.nicolls.ghostevent.ghost.parse.model.ViewNode;

public interface ISecondNewsTarget {

    void onMessage(String message);

    void onFoundItem(ViewNode result);

}
