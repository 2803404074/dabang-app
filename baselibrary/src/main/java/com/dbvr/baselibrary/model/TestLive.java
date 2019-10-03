package com.dbvr.baselibrary.model;

import com.dbvr.baselibrary.R;

import java.util.List;

public class TestLive {
    private String name;
    private String host;
    private String url;

    public TestLive() {
    }

    @Override
    public String toString() {
        return "TestLive{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public TestLive(String name, String host, String url) {
        this.name = name;
        this.host = host;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
