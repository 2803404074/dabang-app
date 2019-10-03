package com.dbvr.baselibrary.model;

import java.util.List;

/**
 * 首页-发现；
 * 多类型数据列表
 */
public class HomeFindMo extends MoreItemType{

    private List<String>mResources;
    private List<TypeMo>mTypeMo;


    public List<TypeMo> getmTypeMo() {
        return mTypeMo;
    }

    public void setmTypeMo(List<TypeMo> mTypeMo) {
        this.mTypeMo = mTypeMo;
    }

    public List<String> getmResources() {
        return mResources;
    }

    public void setmResources(List<String> mResources) {
        this.mResources = mResources;
    }

    public HomeFindMo() {
    }

    public HomeFindMo(int mType, int layoutId) {
        super(mType, layoutId);
    }
    public HomeFindMo(int mType, int layoutId,List<String>mResources) {
        super(mType, layoutId);
        this.mResources = mResources;
    }

    public static class TypeMo{
        private int color;

        public TypeMo(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
