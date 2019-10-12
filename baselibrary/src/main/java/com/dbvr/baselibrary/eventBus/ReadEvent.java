package com.dbvr.baselibrary.eventBus;

/**
 * Created by dc on 2017/3/31.
 */

public class ReadEvent {
    private String state;
    private int type ;
    private String info;

    public ReadEvent(String state, int type, String info) {
        this.state = state;
        this.type = type;
        this.info = info;
    }

    public String getState() {
        return state;
    }

    public String getInfo() {
        return info;
    }

    public int getType() {
        return type;
    }
}
