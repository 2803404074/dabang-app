package com.dbvr.baselibrary.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值的model
 */
public class CzMo {
    private int dropNum;//跳币数量
    private int money;//充值价钱
    private boolean isYou;//是否优惠
    private boolean isCheck;

    public CzMo(int dropNum, int money, boolean isCheck,boolean isYou) {
        this.dropNum = dropNum;
        this.money = money;
        this.isCheck = isCheck;
        this.isYou = isYou;
    }

    public boolean isYou() {
        return isYou;
    }

    public void setYou(boolean you) {
        isYou = you;
    }

    public int getDropNum() {
        return dropNum;
    }

    public void setDropNum(int dropNum) {
        this.dropNum = dropNum;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public static List<CzMo> getData(){
        List<CzMo> list = new ArrayList<>();
        list.add(new CzMo(50,5,true,false));
        list.add(new CzMo(200,20,false,false));
        list.add(new CzMo(500,50,false,false));
        list.add(new CzMo(1000,100,false,false));
        list.add(new CzMo(2000,198,false,true));
        list.add(new CzMo(5000,488,false,true));
        list.add(new CzMo(10000,950,false,true));
        return list;
    }
}
