package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.provider.ClickArrowToTopProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class ClickArrowTopEvent extends GroupEvent {
    private static final String TAG = "ClickArrowTopEvent";

    public ClickArrowTopEvent(IWebTarget target, EventExecutor.ExecuteCallBack executeCallBack,
                              ClickArrowToTopProvider provider) {
        super(target, executeCallBack);
        addEvent(provider.getParams());
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public String getDetail() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("group", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
