package com.nicolls.ghostevent.ghost.event.provider;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickEvent;
import com.nicolls.ghostevent.ghost.event.RedirectClickEvent;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertParser;

import java.util.ArrayList;
import java.util.List;

public class SlideToAdvertAndClickProvider extends EventParamsProvider<List<BaseEvent>> {
    private static final String TAG = "SlideToAdvertAndClickProvider";
    private final IWebTarget target;
    private final RedirectHandler redirectHandler;

    public SlideToAdvertAndClickProvider(IWebTarget target, RedirectHandler redirectHandler) {
        this.target = target;
        this.redirectHandler = redirectHandler;

    }

    @Override
    public List<BaseEvent> getParams() {
        final List<BaseEvent> list = new ArrayList<>();
        IWebParser parseAdvert = new AdvertParser();
        WebParseEvent parseEvent = new WebParseEvent(target, parseAdvert);
        ScrollVerticalEvent scrollToAdvert = new ScrollVerticalEvent(target, new SlideToAdvertEventProvider(target));
        WebParseEvent updateNodesEvent = new WebParseEvent(target, parseAdvert);
        RedirectClickEvent clickAdvert = new RedirectClickEvent(new ClickEvent(target, new AdvertClickEventParamsProvider(target)), redirectHandler);
        list.add(parseEvent);
        list.add(scrollToAdvert);
        list.add(updateNodesEvent);
        list.add(clickAdvert);
        return list;
    }

    @Override
    public String getName() {
        return TAG;
    }
}
