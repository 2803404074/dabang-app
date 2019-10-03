package com.dbvr.baselibrary.model;

/**
 * 多类型列表
 */
public class MoreItemType {
    private int mType;
    private int layoutId;

    public MoreItemType() {
    }

    public MoreItemType(int mType, int layoutId) {
        this.mType = mType;
        this.layoutId = layoutId;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
