package com.nicolls.ghostevent.ghost.core;

import android.content.Context;

public interface IWebTarget extends ITarget {
    void executeJs(String js);
    Context getContext();
}
