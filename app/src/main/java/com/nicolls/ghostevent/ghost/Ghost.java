package com.nicolls.ghostevent.ghost;

public abstract class Ghost {
    protected static final String URL="https://cpu.baidu.com/1001/be900f73?scid=33854";
    protected static final String DEFAULT_URL="http://jandan.net/";
    protected static final String LOCAL_URL="file:////android_asset/advert.html";

    public abstract void init();
    public abstract void exit();

    public abstract void reload();

    public abstract void record();

    public abstract void play();

    public abstract void goHome();

    public abstract void onParse();

    public abstract void onPlayParse();

}
