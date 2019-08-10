package com.nicolls.ghostevent.ghost.utils;

import android.text.TextUtils;

import com.nicolls.ghostevent.ghost.core.EventBuilder;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.view.GhostWebViewClient;

import java.util.Random;

public class Probability {
    private static final String TAG="Probability";

    private EventBuilder eventBuilder;
    public Probability(EventBuilder eventBuilder){
        this.eventBuilder=eventBuilder;
    }

    private int homeSlideCount=0;
    private int secondNewsSlideCount=0;
    private int secondAdvertSlideCount=0;
    private int otherSlideCount=0;
    private int advertShowCount=0;
    private int advertClickCount=0;
    public BaseEvent generateEvent(IWebTarget webTarget,String url){
        Random random=new Random();
        int factor=0;
        GhostUtils.Page page=GhostUtils.currentPage(url);
        LogUtil.d(TAG,"page "+page);
        switch (page){
            case HOME:
                secondNewsSlideCount=0;
                secondAdvertSlideCount=0;
                otherSlideCount=0;
                factor=random.nextInt(10);
                LogUtil.d(TAG,"factor:"+factor);
                if(homeSlideCount==0&&factor==0){
                    LogUtil.d(TAG,"HOME ,hit exit");
                    return null;
                }else if(factor==1){
                    LogUtil.d(TAG,"HOME hit down fresh");
                    return eventBuilder.getSlideDown(webTarget);
                } else if(factor>=0&&factor<5){
                    homeSlideCount++;
                    LogUtil.d(TAG,"HOME hit click");
                    return eventBuilder.getClickEvent(webTarget);
                } else {
                    homeSlideCount++;
                    LogUtil.d(TAG,"HOME hit slide up");
                    return eventBuilder.getSlideUp(webTarget);
                }
            case SECOND_NEWS:
                homeSlideCount=0;
                secondAdvertSlideCount=0;
                otherSlideCount=0;
                factor=random.nextInt(10);
                LogUtil.d(TAG,"factor:"+factor);
                if(secondNewsSlideCount==0&&factor==0){
                    LogUtil.d(TAG,"SECOND_NEWS ,hit exit");
                    return eventBuilder.getGoBackEvent(webTarget);
                }else {
                    Random nextRandom=new Random();
                    factor=nextRandom.nextInt(100);
                    LogUtil.d(TAG,"next factor:"+factor);
                    if(factor>=0&&factor<5&&secondNewsSlideCount==0){
                        LogUtil.d(TAG,"SECOND_NEWS hit head advert");
                        return eventBuilder.getSecondAdvertHeadClickEvent(webTarget);
                    } else {
                        if(secondNewsSlideCount==0){
                            secondNewsSlideCount++;
                            LogUtil.d(TAG,"SECOND_NEWS hit slide up first");
                            return eventBuilder.getSlideUp(webTarget);
                        }else {
                            if(factor>=0&&factor<=25){
                                secondNewsSlideCount++;
                                LogUtil.d(TAG,"SECOND_NEWS ,hit go back");
                                return eventBuilder.getGoBackEvent(webTarget);
                            } else if(factor>25&&factor<50){
                                LogUtil.d(TAG,"SECOND_NEWS ,hit click");
                                return eventBuilder.getClickEvent(webTarget);
                            }else {
                                LogUtil.d(TAG,"SECOND_NEWS ,hit slide up");
                                secondNewsSlideCount++;
                                return eventBuilder.getSlideUp(webTarget);
                            }
                        }
                    }
                }
            case SECOND_ADVERT:
                homeSlideCount=0;
                secondNewsSlideCount=0;
                otherSlideCount=0;
                if(secondAdvertSlideCount==0){
                    advertShowCount++;
                }
                factor=random.nextInt(10);
                LogUtil.d(TAG,"factor:"+factor);
                if(secondAdvertSlideCount==0&&factor>=0&&factor<3){
                    LogUtil.d(TAG,"SECOND_ADVERT ,hit exit");
                    return eventBuilder.getGoBackEvent(webTarget);
                }else {
                    Random nextRandom=new Random();
                    factor=nextRandom.nextInt(100);
                    LogUtil.d(TAG,"next factor:"+factor);
                    if(factor>=0&&factor<30){
                        LogUtil.d(TAG,"SECOND_ADVERT hit advert");
                        return eventBuilder.getClickEvent(webTarget);
                    } else {
                        LogUtil.d(TAG,"SECOND_ADVERT ,hit slide up");
                        secondAdvertSlideCount++;
                        return eventBuilder.getSlideUp(webTarget);
                    }
                }
            case OTHER:
                homeSlideCount=0;
                secondNewsSlideCount=0;
                secondAdvertSlideCount=0;
                if(otherSlideCount==0){
                    LogUtil.d(TAG,"other page advertClickCount:"+advertClickCount);
                    advertClickCount++;
                    if(advertClickCount>=3){
                        LogUtil.d(TAG,"advertClickCount enough exist");
                        return null;
                    }
                }

                factor=random.nextInt(10);
                LogUtil.d(TAG,"factor:"+factor);
                if(factor>=0&&factor<7){
                    LogUtil.d(TAG,"OTHER ,hit exit");
                    return eventBuilder.getGoBackEvent(webTarget);
                }else {
                    LogUtil.d(TAG,"OTHER ,hit go back");
                    otherSlideCount++;
                    return eventBuilder.getSlideUp(webTarget);
                }
        }
        return null;
    }

    public int getAdvertClickCount(){
        LogUtil.d(TAG,"advertClickCount "+advertClickCount);
        return advertClickCount;
    }
}
