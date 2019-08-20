package com.nicolls.ghostevent.ghost.parse;

import com.nicolls.ghostevent.ghost.utils.Constants;

public abstract class WebBaseParser implements IWebParser {

    private static final String TAG = "WebBaseParser";

    @Override
    public long getParsedDelay() {
        return Constants.TIME_DEFAULT_JS_PARSED_DELAY;
    }
}
