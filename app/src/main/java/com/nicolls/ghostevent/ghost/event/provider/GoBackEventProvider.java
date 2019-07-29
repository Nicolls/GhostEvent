package com.nicolls.ghostevent.ghost.event.provider;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.LoadJsScriptInfEvent;
import com.nicolls.ghostevent.ghost.event.PageGoBackEvent;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertJsInterface;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertParser;
import com.nicolls.ghostevent.ghost.parse.advert.IAdvertTarget;

import java.util.ArrayList;
import java.util.List;

public class GoBackEventProvider extends EventParamsProvider<List<BaseEvent>> {
    private static final String TAG = "GoBackEventProvider";
    private final IWebTarget target;
    private final IAdvertTarget advertTarget;
    private final RedirectHandler redirectHandler;

    public GoBackEventProvider(IWebTarget target, IAdvertTarget advertTarget,
                               RedirectHandler redirectHandler) {
        this.target = target;
        this.advertTarget = advertTarget;
        this.redirectHandler = redirectHandler;
    }

    @Override
    public List<BaseEvent> getParams() {
        final List<BaseEvent> list = new ArrayList<>();
        final WebView webView = (WebView) target;
        PageGoBackEvent goBackEvent = new PageGoBackEvent(target, redirectHandler);
        AdvertJsInterface advertInterface = new AdvertJsInterface(webView.getContext(), advertTarget);
        LoadJsScriptInfEvent loadJsScriptInfEvent = new LoadJsScriptInfEvent(target, advertInterface);
        IWebParser parseAdvert = new AdvertParser();
        WebParseEvent parseEvent = new WebParseEvent(target, parseAdvert);
        list.add(goBackEvent);
        list.add(loadJsScriptInfEvent);
        list.add(parseEvent);
        return list;
    }

    @Override
    public String getName() {
        return TAG;
    }

}
