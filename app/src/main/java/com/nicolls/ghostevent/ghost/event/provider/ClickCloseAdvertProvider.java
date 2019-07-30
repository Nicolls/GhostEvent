package com.nicolls.ghostevent.ghost.event.provider;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickWebEvent;
import com.nicolls.ghostevent.ghost.event.HomePageEvent;
import com.nicolls.ghostevent.ghost.event.enclosure.PageGoHomeEvent;
import com.nicolls.ghostevent.ghost.event.RedirectClickEvent;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertParser;
import com.nicolls.ghostevent.ghost.parse.advert.IAdvertTarget;

import java.util.ArrayList;
import java.util.List;

public class ClickCloseAdvertProvider extends EventParamsProvider<List<BaseEvent>> {
    private static final String TAG = "ClickCloseAdvertProvider";
    private final IWebTarget target;
    private final RedirectHandler redirectHandler;
    private final EventExecutor.ExecuteCallBack executeCallBack;
    private final IAdvertTarget advertTarget;
    public ClickCloseAdvertProvider(IWebTarget target, EventExecutor.ExecuteCallBack executeCallBack,
                                    RedirectHandler redirectHandler, IAdvertTarget advertTarget) {
        this.target = target;
        this.redirectHandler = redirectHandler;
        this.executeCallBack=executeCallBack;
        this.advertTarget=advertTarget;
    }

    @Override
    public List<BaseEvent> getParams() {
        final List<BaseEvent> list = new ArrayList<>();
        HomePageEvent homePageEvent=new HomePageEvent(target,executeCallBack,new GoHomeEventProvider(target,advertTarget,redirectHandler));
        IWebParser parseAdvert = new AdvertParser();
        WebParseEvent parseEvent = new WebParseEvent(target, parseAdvert);
        ScrollVerticalEvent scrollToAdvert = new ScrollVerticalEvent(target, new SlideToAdvertEventProvider(target));
        WebParseEvent updateNodesEvent = new WebParseEvent(target, parseAdvert);
        RedirectClickEvent clickAdvert = new RedirectClickEvent(new ClickWebEvent(target, new AdvertClickEventParamsProvider(target)), redirectHandler);
        list.add(homePageEvent);
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
