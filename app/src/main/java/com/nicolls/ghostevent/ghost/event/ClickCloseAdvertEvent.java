package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.provider.ClickCloseAdvertProvider;

public class ClickCloseAdvertEvent extends GroupEvent {
    private static final String TAG = "ClickCloseAdvertEvent";

    public ClickCloseAdvertEvent(IWebTarget target, EventExecutor.ExecuteCallBack executeCallBack,
                                 ClickCloseAdvertProvider provider) {
        super(target, executeCallBack);
        addEvent(provider.getParams());
    }

}
