package com.nicolls.ghostevent.ghost.utils;

public class Constants {

    /**
     * test
     */
    // url
    private static final String SERVER_ADDRESS = "http://content.urlmat.cn/contentunion";
    public static final String INFO_URL = SERVER_ADDRESS + "/ad/info";
//    public static final String UPLOAD_EVENT_URL = SERVER_ADDRESS + "/ad/upload";
    public static final String UPLOAD_EVENT_URL = SERVER_ADDRESS + "/ad/log";


    public static final String DEFAULT_UNION_URL = "https://cpu.baidu.com/1001/be900f73?scid=33854";
    public static final String GO_HOME_URL = "https://cpu.baidu.com/1001/be900f73";
    public static final String DEFAULT_UNION_DOMAIN = "cpu.baidu.com";
    public static final String DEFAULT_UNION_DOMAIN_ADVERIT = "aden.baidu.com";

    public static final String TEST_SERVER = "https://getman.cn/echo";


    /**
     * 重试的最大次数
     */
    public static final int MAX_TRY_TIMES = 3;
    /**
     * event
     */
    // 加这个延长用于确保，当前的event一定比子延长大
    public static final long DEFAULT_EVENT_EXECUTE_TIMEOUT_EXTEND = 100; // 毫秒
    // 取消退出这个事件的等待时长，大于最大的那个timeout事件
    public static final long TIME_DEFAULT_CANCEL_WAIT_TIME = 30 * 1000; // 毫秒
    // 加载网页的等待时长
    public static final long TIME_DEFAULT_LOAD_PAGE_WAIT_TIME = 20 * 1000; // 毫秒
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
    public static final String JS_FUNCTION_FIND_ARROW_TOP_BY_CLASS_NAME = "javascript:window.findArrowTopItem(%s,%s)";

    public static final String JS_FUNCTION_FIND_MAIN_ICON_CLASS_NAME = "javascript:window.findMainIconItem(%s,%s)";

    public static final String JS_FUNCTION_FIND_READ_MORE_CLASS_NAME = "javascript:window.findReadMoreItem(%s,%s)";

    public static final String JS_FUNCTION_FIND_ADVERT_TOP_BY_CLASS_NAME = "javascript:window.findAdvertTopItem(%s,%s)";

    // div class name
    public static final String DIV_CLASSNAME_ADVERT = "ad-item";
    public static final String DIV_CLASSNAME_NEWS = "news-item";
    public static final String DIV_CLASSNAME_VIDEO = "video-item";
    public static final String DIV_CLASSNAME_ARROW_TOP = "icon-up";
    public static final String DIV_CLASSNAME_MAIN_ICON = "icon-home";
    public static final String DIV_CLASSNAME_READ_MORE = "_1Dz8F";
    public static final String DIV_CLASSNAME_ADVERT_TOP = "a-item-inner";


    // event id
    /**
     * 加载广告联盟url
     */
    public static final int EVENT_TYPE_LOAD_UNION_URL = 1;
    /**
     * 展示首页（有可能是用户点击了回到首页的按钮）
     */
    public static final int EVENT_TYPE_SHOW_HOME_PAGE = 2;
    /**
     * 展示二级页面，新闻页
     */
    public static final int EVENT_TYPE_SHOW_SECOND_NEWS_PAGE = 3;
    /**
     * 展示二级页面，广告页
     */
    public static final int EVENT_TYPE_SHOW_SECOND_ADVERT_PAGE = 4;
    /**
     * 点击了广告的项，展示了广告详情页
     */
    public static final int EVENT_TYPE_CLICK_ADVERT = 5;
    /**
     * 每次加载广告时，会自动生成范围在（1~3随机值）内在最大广告点击数，
     * 如果随机事件中，展示广告详情的次数到达了这个最大数时业务就会中止
     */
    public static final int EVENT_TYPE_ENOUGH_CLICK_ADVERT = 6;

    /**
     * 随机事件中，在首页命中了退出事件，业务中止
     */
    public static final int EVENT_TYPE_HOME_EXIT = 7;

    /**
     * 发生了重试的事件
     */
    public static final int EVENT_TYPE_RETRY = 8;

    /**
     * 加载广告二级页面时，达到了本次最大允许次数 (1~4的随机值)
     */
    public static final int EVENT_TYPE_ENOUGH_SHOW_ADVERT = 9;


    // target
    public static final int EVENT_TARGET_WEBVIEW = 1;
}
