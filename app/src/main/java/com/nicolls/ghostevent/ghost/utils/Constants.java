package com.nicolls.ghostevent.ghost.utils;

public class Constants {

    /**
     * test
     */
    // url
    public static final String DEFAULT_URL = "http://jandan.net/";
    public static final String DEFAULT_URL_ZAKER = "https://cpu.baidu.com/1001/be900f73?scid=33854";
    public static final String LOCAL_URL = "file:////android_asset/advert.html";

    /**
     * event
     */
    public static final long TIME_NOTIFY_PAGE_LOADED_DELAY = 2 * 1000; // 毫秒
    public static final long TIME_LOAD_JS_INIT = 1 * 1000; // 毫秒

    /**
     * parse
     */
    // html
    public static final String JS_CURRENT_PAGE_HTML = "javascript:window.currentPageHtml()";
    // find item
    public static final String JS_FUNCTION_FIND_ITEM = "javascript:window.findItemLocation()";
    // message
    public static final String JS_FUNCTION_MESSAGE = "javascript:window.printMessage()";

    // div class name
    public static final String DIV_CLASSNAME_ADVERT = "ad-item";
    public static final String DIV_CLASSNAME_NEWS = "news-item";
    public static final String DIV_CLASSNAME_VIDEO = "video-item";

}
