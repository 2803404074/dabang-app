package com.dbvr.baselibrary.model;

/**
 * 开播创建推流
 */
public class StreamMo {
    private String publishURL;
    private String tag;

    public StreamMo() {
    }

    public String getPublishURL() {
        return publishURL;
    }

    public void setPublishURL(String publishURL) {
        this.publishURL = publishURL;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
