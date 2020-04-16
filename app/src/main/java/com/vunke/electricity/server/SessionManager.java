package com.vunke.electricity.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018-03-22.
 */
public class SessionManager {
    private static SessionManager instance;

    private final ArrayList<WebSession> mSessionList;

    private SessionManager() {
        mSessionList = new ArrayList<WebSession>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public synchronized void add(WebSession session) {
        mSessionList.add(session);
    }

    public synchronized void remove(WebSession session) {
        mSessionList.remove(session);
    }

    public synchronized List<WebSession> getAll() {
        ArrayList<WebSession> dest = new ArrayList<>(mSessionList.size());
        Collections.copy(dest, mSessionList);
        return dest;
    }

    public void closeAll() {
        for(WebSession session : mSessionList) {
            session.close();
        }
    }
}
