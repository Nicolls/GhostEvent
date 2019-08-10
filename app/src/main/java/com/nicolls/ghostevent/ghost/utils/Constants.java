package com.nicolls.ghostevent.ghost.utils;

public class Constants {

    /**
     * test
     */
    // url
    private static final String SERVER_ADDRESS = "http://content.urlmat.cn/contentunion";
    public static final String INFO_URL = SERVER_ADDRESS +"/ad/info";
    public static final String UPLOAD_EVENT_URL = SERVER_ADDRESS +"/ad/upload";

    public static final String DEFAULT_UNION_URL = "https://cpu.baidu.com/1001/be900f73?scid=33854";
    public static final String DEFAULT_UNION_DOMAIN = "cpu.baidu.com";
    public static final String DEFAULT_UNION_DOMAIN_ADVERIT = "aden.baidu.com";

    public static final String TEST_SERVER = "https://getman.cn/echo";


    /**
     * 重试的最大次数
     *
     */
    public static final int MAX_TRY_TIMES = 3;
    /**
     * event
     */
    // 加这个延长用于确保，当前的event一定比子延长大
    public static final long DEFAULT_EVENT_EXECUTE_TIMEOUT_EXTEND = 100; // 毫秒
    // 取消退出这个事件的等待时长，大于最大的那个timeout事件
    public static final long TIME_DEFAULT_CANCEL_WAIT_TIME = 50 * 1000; // 毫秒
    // 加载网页的等待时长
    public static final long TIME_DEFAULT_LOAD_PAGE_WAIT_TIME = 40 * 1000; // 毫秒
    // 加载网页触发了success后，延迟时长再通知加载完成
    public static final long TIME_NOTIFY_PAGE_LOADED_DELAY = 3 * 1000; // 毫秒
    // 加载js等待的时长
    public static final long TIME_DEFAULT_LOAD_JS_INIT = 1 * 1000; // 毫秒
    // 执行加载js代码后，延迟的时长再通知加载js完成
    public static final long TIME_DEFAULT_JS_PARSED_DELAY = 1 * 1000;


//    public static final int CLICK_INTERVAL_TIME = 100;
//    public static final int CLICK_EXECUTE_TIMEOUT = CLICK_INTERVAL_TIME * 2;

//    public static final long CANCEL_EXECUTE_TIMEOUT = 30 * 1000; // 毫秒
//    public static final long LOAD_JS_INTERFACE_EXECUTE_TIMEOUT = 0; // 毫秒
//    public static final long LOAD_JS_SCRIPT_EXECUTE_TIMEOUT = Constants.TIME_DEFAULT_LOAD_JS_INIT * 2; // 毫秒
//    public static final long LOAD_GO_BACK_PAGE_EXECUTE_TIMEOUT = Constants.TIME_NOTIFY_PAGE_LOADED_DELAY * 2; // 毫秒
//    public static final long LOAD_GO_HOME_PAGE_EXECUTE_TIMEOUT = Constants.TIME_NOTIFY_PAGE_LOADED_DELAY * 2; // 毫秒
//    public static final long LOAD_PAGE_EXECUTE_TIMEOUT = Constants.TIME_NOTIFY_PAGE_LOADED_DELAY * 4; // 毫秒
//    public static final long REDIRECT_CLICK_EXECUTE_TIMEOUT = Constants.TIME_NOTIFY_PAGE_LOADED_DELAY * 2+CLICK_EXECUTE_TIMEOUT; // 毫秒

//    public static final long DEFAULT_VERTICAL_SCROLL_ANIM_DURATION = 1000; // 毫秒
//    public static final long VERTICAL_SCROLL_EXECUTE_TIMEOUT = DEFAULT_VERTICAL_SCROLL_ANIM_DURATION +200; // 毫秒


//    public static final long DEFAULT_SLIDE_TIMEOUT = 200; // 毫秒
    /**
     * parse
     */
    // html
    public static final String JS_CURRENT_PAGE_HTML = "javascript:window.currentPageHtml()";
    // find nodes
    public static final String JS_FUNCTION_FIND_ITEM = "javascript:window.findItemLocation(%s,%s)";
    // context
    public static final String JS_FUNCTION_PRINT_CONTEXT = "javascript:window.printContext(%s,%s)";
    // find id node
    public static final String JS_FUNCTION_FIND_ITEM_BY_ID = "javascript:window.findItemById(%s,%s,%s)";

    // find class name node
    public static final String JS_FUNCTION_FIND_ITEM_BY_CLASS_NAME = "javascript:window.findItemByClassName(%s,%s)";

    // div class name
    public static final String DIV_CLASSNAME_ADVERT = "ad-item";
    public static final String DIV_CLASSNAME_NEWS = "news-item";
    public static final String DIV_CLASSNAME_VIDEO = "video-item";
    public static final String DIV_CLASSNAME_ARROW_TOP = "icon-up";


    // div id name


}
