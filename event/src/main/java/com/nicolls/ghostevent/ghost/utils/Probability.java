package com.nicolls.ghostevent.ghost.utils;

import com.nicolls.ghostevent.ghost.core.EventBuilder;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;
import com.nicolls.ghostevent.ghost.request.EventReporter;

import java.util.Random;

public class Probability {
    private static final String TAG = "Probability";
    private int maxAdvertClick = 0;
    private int maxAdvertShow = 0;
    private int maxNeedClickAdvertPageCount = 0;
    private EventBuilder eventBuilder;

    public Probability(EventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
        Random random = new Random();
        maxAdvertClick = random.nextInt(3) + 1;
        maxAdvertShow = random.nextInt(4) + 1;
        maxNeedClickAdvertPageCount = random.nextInt(10) + 20;
    }

    private int homeSlideCount = 0;
    private int secondNewsSlideCount = 0;
    private int secondAdvertSlideCount = 0;
    private int otherSlideCount = 0;
    private int advertShowCount = 0;
    private int advertClickCount = 0;
    private int clickLoadPageCount = 0;

    public void init() {
        clickLoadPageCount = 0;
    }

    public BaseEvent generateEvent(IWebTarget webTarget, String url) {
        Random random = new Random();
        int factor = 0;
        GhostUtils.Page page = GhostUtils.currentPage(url);
        LogUtil.d(TAG, "page " + page);
        switch (page) {
            case HOME:
                secondNewsSlideCount = 0;
                secondAdvertSlideCount = 0;
                otherSlideCount = 0;
                factor = random.nextInt(10);
                LogUtil.d(TAG, "factor:" + factor);
                if ((homeSlideCount == 0 && factor == 0) || (homeSlideCount > 4 && advertShowCount >= 3 && factor == advertShowCount)) {
                    LogUtil.d(TAG, "HOME ,hit exit");
                    EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_HOME_EXIT);
                    return null;
                } else if (factor == 1) {
                    LogUtil.d(TAG, "HOME hit down fresh");
                    return eventBuilder.getSlideDownEvent(webTarget);
                } else if (factor >= 0 && factor < 5) {
                    homeSlideCount++;
                    Random r2 = new Random();
                    factor = r2.nextInt(100);
                    if (clickLoadPageCount >= maxNeedClickAdvertPageCount) {
                        LogUtil.d(TAG, "HOME clickLoadPageCount enough " + clickLoadPageCount);
                        if (factor > 50) {
                            LogUtil.d(TAG, "HOME hit click advert");
                            clickLoadPageCount=0;
                            return eventBuilder.getHomeSelectClickEvent(webTarget, ViewNode.Type.ADVERT);
                        }
                    } else {
                        if (factor > 60 && factor < 65) {
                            LogUtil.d(TAG, "HOME hit click");
                            return eventBuilder.getClickEvent(webTarget);
                        } else {
                            LogUtil.d(TAG, "HOME hit click news");
                            clickLoadPageCount++;
                            return eventBuilder.getHomeSelectClickEvent(webTarget, ViewNode.Type.NEWS);
                        }
                    }

                } else {
                    homeSlideCount++;
                    if (homeSlideCount > 4 && factor == 8) {
                        LogUtil.d(TAG, "HOME hit arrow top icon");
                        return eventBuilder.getHomeClickArrowTopNodeEvent(webTarget);
                    }
                    LogUtil.d(TAG, "HOME hit slide up");
                    return eventBuilder.getSlideUpEvent(webTarget);
                }
            case SECOND_NEWS:
                homeSlideCount = 0;
                secondAdvertSlideCount = 0;
                otherSlideCount = 0;
                factor = random.nextInt(10);
                LogUtil.d(TAG, "factor:" + factor);
                if (secondNewsSlideCount == 0 && factor == 0) {
                    LogUtil.d(TAG, "SECOND_NEWS ,hit exit");
                    return eventBuilder.getGoBackEvent(webTarget);
                } else {
                    Random nextRandom = new Random();
                    factor = nextRandom.nextInt(100);
                    LogUtil.d(TAG, "next factor:" + factor);
                    if (factor >= 0 && factor < (10 - advertShowCount * 3) && secondNewsSlideCount == 0) {
                        LogUtil.d(TAG, "SECOND_NEWS hit head advert");
                        return eventBuilder.getSecondNewsAdvertHeadClickEvent(webTarget);
                    } else {
                        if (secondNewsSlideCount == 0) {
                            secondNewsSlideCount++;
                            LogUtil.d(TAG, "SECOND_NEWS hit slide up first");
                            return eventBuilder.getSlideUpEvent(webTarget);
                        } else {
                            if (factor >= 0 && factor <= 25) {
                                LogUtil.d(TAG, "SECOND_NEWS ,hit go back");
                                return eventBuilder.getGoBackEvent(webTarget);
                            } else if (secondNewsSlideCount <= 3 && factor > 25 && factor < 50) {
                                LogUtil.d(TAG, "SECOND_NEWS ,hit scroll to read more and click");
                                return eventBuilder.getSecondNewsScrollAndClickReadMoreNodeEvent(webTarget);
                            } else if (factor >= 50 && factor < 60) {
                                Random r2 = new Random();
                                factor = r2.nextInt(10);
                                if (factor < (7 + advertShowCount)) {
                                    LogUtil.d(TAG, "SECOND_NEWS ,hit news click");
                                    clickLoadPageCount++;
                                    return eventBuilder.getSecondNewsSelectClickEvent(webTarget, ViewNode.Type.NEWS);
                                } else {
                                    LogUtil.d(TAG, "SECOND_NEWS ,hit click");
                                    clickLoadPageCount++;
                                    return eventBuilder.getClickEvent(webTarget);
                                }
                            } else {
                                secondNewsSlideCount++;
                                if (secondNewsSlideCount > 7 && (factor >= 60 && factor < (60 + secondNewsSlideCount))) {
                                    LogUtil.d(TAG, "SECOND_NEWS hit arrow top icon");
                                    return eventBuilder.getSecondNewsClickArrowTopNodeEvent(webTarget);
                                }

                                if (secondNewsSlideCount > 5 && (factor >= 70 && factor < (70 + secondNewsSlideCount))) {
                                    LogUtil.d(TAG, "SECOND_NEWS hit home icon");
                                    return eventBuilder.getSecondNewsClickMainIconNodeEvent(webTarget);
                                }
                                if (factor > 85 && factor < 90) {
                                    LogUtil.d(TAG, "SECOND_NEWS ,hit slide down");
                                    return eventBuilder.getSlideDownEvent(webTarget);

                                }
                                LogUtil.d(TAG, "SECOND_NEWS ,hit slide up");
                                return eventBuilder.getSlideUpEvent(webTarget);
                            }
                        }
                    }
                }
            case SECOND_ADVERT:
                homeSlideCount = 0;
                secondNewsSlideCount = 0;
                otherSlideCount = 0;
                if (secondAdvertSlideCount == 0) {
                    advertShowCount++;
                }

                if (advertShowCount >= maxAdvertShow) {
                    LogUtil.d(TAG, "enough show ", true);
                    LogUtil.d(TAG, "advertShowCount enough exist max " + maxAdvertShow);
                    EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_ENOUGH_SHOW_ADVERT);
                    return null;
                }
                factor = random.nextInt(10);
                LogUtil.d(TAG, "factor:" + factor);
                if (secondAdvertSlideCount == 0 && factor >= 0 && factor < (3 + 2 * advertShowCount)) {
                    LogUtil.d(TAG, "SECOND_ADVERT ,hit back");
                    return eventBuilder.getGoBackEvent(webTarget);
                } else {
                    Random nextRandom = new Random();
                    factor = nextRandom.nextInt(100);
                    LogUtil.d(TAG, "next factor:" + factor);
                    if (factor >= (10 * advertShowCount) && factor < 30) {
                        LogUtil.d(TAG, "SECOND_ADVERT hit advert");
                        clickLoadPageCount++;
                        return eventBuilder.getClickEvent(webTarget);
                    } else {
                        secondAdvertSlideCount++;
                        if (factor > 90) {
                            LogUtil.d(TAG, "SECOND_ADVERT ,hit slide down");
                            return eventBuilder.getSlideDownEvent(webTarget);
                        }
                        LogUtil.d(TAG, "SECOND_ADVERT ,hit slide up");
                        return eventBuilder.getSlideUpEvent(webTarget);
                    }
                }
            case OTHER:
                homeSlideCount = 0;
                secondNewsSlideCount = 0;
                secondAdvertSlideCount = 0;
                if (otherSlideCount == 0) {
                    LogUtil.d(TAG, "other page advertClickCount:" + advertClickCount);
                    advertClickCount++;
                    if (advertClickCount >= maxAdvertClick) {
                        LogUtil.d(TAG, "advertClickCount enough exist max" + maxAdvertClick);
                        EventReporter.getInstance().uploadEvent(Constants.EVENT_TYPE_ENOUGH_CLICK_ADVERT);
                        return null;
                    }
                }

                factor = random.nextInt(10);
                LogUtil.d(TAG, "factor:" + factor);
                if (factor >= 0 && factor < 3) {
                    LogUtil.d(TAG, "OTHER ,hit go back");
                    return eventBuilder.getGoBackEvent(webTarget);
                } else if (factor >= 3 && factor < 7) {

                    LogUtil.d(TAG, "OTHER ,hit go home");
                    return eventBuilder.getGoHomeEvent(webTarget);

                } else {
                    otherSlideCount++;
                    LogUtil.d(TAG, "OTHER ,hit slide up");
                    return eventBuilder.getSlideUpEvent(webTarget);
                }
        }
        return null;
    }

    public int getAdvertClickCount() {
        LogUtil.d(TAG, "advertClickCount " + advertClickCount);
        return advertClickCount;
    }

    public int getAdvertShowCount() {
        LogUtil.d(TAG, "advertShowCount " + advertShowCount);
        return advertShowCount;
    }
}
