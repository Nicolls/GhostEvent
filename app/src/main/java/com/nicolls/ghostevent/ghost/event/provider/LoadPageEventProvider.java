package com.nicolls.ghostevent.ghost.event.provider;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.LoadJsInterfaceEvent;
import com.nicolls.ghostevent.ghost.event.LoadJsScriptInfEvent;
import com.nicolls.ghostevent.ghost.event.LoadPageEvent;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertJsInterface;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertParser;
import com.nicolls.ghostevent.ghost.parse.advert.IAdvertTarget;

import java.util.ArrayList;
import java.util.List;

public class LoadPageEventProvider extends EventParamsProvider<List<BaseEvent>> {
    private static final String TAG="LoadPageEventProvider";
    private final IWebTarget target;
    private final IAdvertTarget advertTarget;
    private final RedirectHandler redirectHandler;
    private final String url;

    public LoadPageEventProvider(IWebTarget target, IAdvertTarget advertTarget,
                                 RedirectHandler redirectHandler, String url) {
        this.target = target;
        this.advertTarget = advertTarget;
        this.redirectHandler = redirectHandler;
        this.url = url;

    }

    @Override
    public List<BaseEvent> getParams() {
        final List<BaseEvent> list = new ArrayList<>();
        final WebView webView = (WebView) target;
        AdvertJsInterface advertInterface = new AdvertJsInterface(webView.getContext(), advertTarget);
        LoadJsInterfaceEvent loadJsInterfaceEvent = new LoadJsInterfaceEvent(target, advertInterface);
        LoadPageEvent loadPageEvent = new LoadPageEvent(target, redirectHandler, url);
        LoadJsScriptInfEvent loadJsScriptInfEvent = new LoadJsScriptInfEvent(target, advertInterface);
        IWebParser parseAdvert = new AdvertParser();
        WebParseEvent parseEvent = new WebParseEvent(target, parseAdvert);
        list.add(loadJsInterfaceEvent);
        list.add(loadPageEvent);
        list.add(loadJsScriptInfEvent);
        list.add(parseEvent);
        return list;
    }

    @Override
    public String getName() {
        return TAG;
    }
}
