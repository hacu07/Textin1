package com.hacu.textin1.mainModule.events;

import com.hacu.textin1.common.pojo.User;

public class MainEvent {
    public static final int USER_ADDED = 0;
    public static final int USER_UPDATE = 1;
    public static final int USER_REMOVE = 2;
    public static final int REQUEST_ADDED = 3;
    public static final int REQUEST_UPDATE = 4;
    public static final int REQUEST_REMOVE = 5;
    public static final int REQUEST_ACCEPTED = 6;
    public static final int REQUEST_DENIED = 7;
    public static final int ERROR_SERVER = 100;


    private User user;
    private int typeEvent;
    private int resMsg;

    public MainEvent() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(int typeEvent) {
        this.typeEvent = typeEvent;
    }

    public int getResMsg() {
        return resMsg;
    }

    public void setResMsg(int resMsg) {
        this.resMsg = resMsg;
    }
}
