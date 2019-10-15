package com.dbvr.baselibrary.model;

public class FansMo {
    private String name;
    private String head;
    private String id;
    private String date;
    private boolean isFollow;

    public FansMo() {
    }

    public FansMo(String name, String head, String date, boolean isFollow) {
        this.name = name;
        this.head = head;
        this.date = date;
        this.isFollow = isFollow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }
}
