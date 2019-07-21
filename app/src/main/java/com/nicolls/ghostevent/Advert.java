package com.nicolls.ghostevent;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.nicolls.ghostevent.ghost.ActivityGhost;
import com.nicolls.ghostevent.ghost.BackgroundGhost;
import com.nicolls.ghostevent.ghost.Ghost;
import com.nicolls.ghostevent.ghost.ViewGroupGhost;
import com.nicolls.ghostevent.ghost.real.GhostUtils;

public class Advert {

    public static void attchToAppContext(Context appContext){
        GhostUtils.init(appContext);
        Ghost ghost=new BackgroundGhost(appContext);
        ghost.init();
    }

    public static void attachToActivity(Activity activity){
        GhostUtils.init(activity);
        Ghost ghost=new ActivityGhost(activity);
        ghost.init();
    }

    public static void attachToView(ViewGroup container){
        GhostUtils.init(container.getContext());
        Ghost ghost=new ViewGroupGhost();
        ghost.init();
    }
}
