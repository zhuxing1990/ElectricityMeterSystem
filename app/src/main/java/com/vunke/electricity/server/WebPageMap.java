package com.vunke.electricity.server;

import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by Administrator on 2018-03-22.
 */
public class WebPageMap {
    private static final WebPageMap instance = new WebPageMap();

    private final HashMap<String, Class<? extends WebPage>> mPageMap;

    private WebPageMap() {
        mPageMap = new HashMap<>();
    }

    public static WebPageMap getInstance() {
        return instance;
    }

    public synchronized void registerWebPage(String name, Class<? extends WebPage> cls) {
        mPageMap.put(checkName(name), cls);
    }

    public synchronized void unregisterWebPage(String name) {
        mPageMap.put(checkName(name), null);
    }

    public synchronized boolean isRegisteredWebPage(String name) {
        return mPageMap.get(checkName(name)) != null;
    }

    @Nullable
    public Class<? extends WebPage> getWebPage(String name) {
        return mPageMap.get(checkName(name));
    }

    private String checkName(String name) {
        if (name == null) {
            return "";
        }

        return name.toLowerCase();
    }
}
