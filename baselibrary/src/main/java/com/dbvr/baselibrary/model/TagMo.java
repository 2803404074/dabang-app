package com.dbvr.baselibrary.model;

/**
 * 标签
 */
public class TagMo {
    private String id;
    private String categoryImg;
    private int showIndex;
    private String name;
    private boolean isCheck;

    public TagMo() {
    }

    public TagMo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }

    public int getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(int showIndex) {
        this.showIndex = showIndex;
    }

    public TagMo(String id, boolean isCheck, String name) {
        this.id = id;
        this.isCheck = isCheck;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
