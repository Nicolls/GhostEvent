package com.nicolls.ghostevent.ghost.event.provider;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickEvent;
import com.nicolls.ghostevent.ghost.event.ClickWebEvent;
import com.nicolls.ghostevent.ghost.event.HomePageEvent;
import com.nicolls.ghostevent.ghost.event.RedirectClickEvent;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.WebParseEvent;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.advert.AdvertParser;
import com.nicolls.ghostevent.ghost.parse.advert.ArrowTopParser;
import com.nicolls.ghostevent.ghost.parse.advert.IAdvertTarget;

import java.util.ArrayList;
import java.util.List;

public class ClickArrowToTopProvider extends EventParamsProvider<List<BaseEvent>> {
    private static final String TAG = "ClickArrowToTopProvider";
    private final IWebTarget target;
    private final RedirectHandler redirectHandler;
    private final EventExecutor.ExecuteCallBack executeCallBack;
    private final IAdvertTarget advertTarget;
    public ClickArrowToTopProvider(IWebTarget target, EventExecutor.ExecuteCallBack executeCallBack,
                                   RedirectHandler redirectHandler, IAdvertTarget advertTarget) {
        this.target = target;
        this.redirectHandler = redirectHandler;
        this.executeCallBack=executeCallBack;
        this.advertTarget=advertTarget;
    }

    @Override
    public List<BaseEvent> getParams() {
        final List<BaseEvent> list = new ArrayList<>();
        IWebParser parser = new ArrowTopParser();
        WebParseEvent parseEvent = new WebParseEvent(target, parser);
        ArrowTopClickEventParamsProvider provider=new ArrowTopClickEventParamsProvider(target);
        ClickEvent clickArrowTop = new ClickWebEvent(target,provider);
        list.add(parseEvent);
        list.add(clickArrowTop);
        return list;
    }

    @Override
    public String getName() {
        return TAG;
    }

}
