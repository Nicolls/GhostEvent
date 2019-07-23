package com.nicolls.ghostevent;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.nicolls.ghostevent.ghost.ActivityGhost;
import com.nicolls.ghostevent.ghost.BackgroundGhost;
import com.nicolls.ghostevent.ghost.Ghost;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;

public class Advert {

    public static Advert instance = new Advert();

    private Ghost ghost;

    public void attchToAppContext(Context appContext) {
        GhostUtils.init(appContext);
        detach();
        ghost = new BackgroundGhost(appContext);
        ghost.init();
    }

    public void attachToActivity(Activity activity) {
        GhostUtils.init(activity);
        detach();
        ghost = new ActivityGhost(activity);
        ghost.init();
    }

    public void detach() {
        if (ghost != null) {
            ghost.exit();
            ghost = null;
        }
    }

    public void reload(){
        if (ghost != null) {
            ghost.reload();
        }
    }

    public void record(){
        if (ghost != null) {
            ghost.record();
        }
    }

    public void play() {
        if (ghost != null) {
            ghost.play();
        }
    }

    public void goHome() {
        if (ghost != null) {
            ghost.goHome();
        }
    }
}
