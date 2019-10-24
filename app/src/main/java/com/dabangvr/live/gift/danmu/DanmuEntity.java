package com.dabangvr.live.gift.danmu;


import android.net.Uri;

import com.orzangleli.xdanmuku.Model;


public class DanmuEntity extends Model {
    private String content;
    private String name;
    private String portrait;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}