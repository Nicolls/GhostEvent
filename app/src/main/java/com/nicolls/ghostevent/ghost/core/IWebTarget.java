package com.nicolls.ghostevent.ghost.core;

import com.nicolls.ghostevent.ghost.core.ITarget;

public interface IWebTarget extends ITarget {
    void executeJs(String js);
}
