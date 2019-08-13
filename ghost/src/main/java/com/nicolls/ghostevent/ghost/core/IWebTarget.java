package com.nicolls.ghostevent.ghost.core;

import com.nicolls.ghostevent.ghost.parse.model.ViewNode;

import java.util.List;

public interface IWebTarget extends ITarget {
    void executeJs(String js);
}
