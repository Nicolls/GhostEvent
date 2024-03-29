package com.nicolls.ghostevent;

import android.app.Activity;
import android.content.Context;

import com.nicolls.ghostevent.ghost.ActivityGhost;
import com.nicolls.ghostevent.ghost.Ghost;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

public class Advert {
    private static final String TAG = "Advert";
    public static Advert instance = new Advert();

    private Advert() {
    }

    private Ghost ghost;

    public void attach(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            LogUtil.w(TAG, "attach activity null or finished");
            return;
        }
        GhostUtils.init(activity);
        attachToActivity(activity);
//        attachToService(activity);
    }

    private void attachToService(Context appContext) {

    }

    private void attachToActivity(Activity activity) {
        ghost = new ActivityGhost(activity);
        ghost.init();
    }

    public void detach() {
        if (ghost != null) {
            ghost.exit();
            ghost = null;
        }
    }

}
