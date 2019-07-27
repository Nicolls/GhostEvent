package com.nicolls.ghostevent.ghost.core;

import com.nicolls.ghostevent.ghost.parse.ViewNode;

import java.util.List;

public interface IWebTarget extends ITarget {
    void executeJs(String js);
    List<ViewNode> getViewNodes();
}
