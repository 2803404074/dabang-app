package com.dbvr.baselibrary.model;

import java.util.List;

public class MenuBean {

    /**
     * errno : 0
     * data : [{"id":27,"iconUrl":"http://image.vrzbgw.com/upload/20191016/16314041395a8a.png","title":"我的订单","jumpUrl":"client_wddd","state":1,"sort":1,"mallSpeciesId":8},{"id":25,"iconUrl":"http://image.vrzbgw.com/upload/20191016/16204586d5151.png","title":"优惠券","jumpUrl":"client_yhq","state":1,"sort":2,"mallSpeciesId":8},{"id":26,"iconUrl":"http://image.vrzbgw.com/upload/20191016/1621461498dfe8.png","title":"我的评价","jumpUrl":"client_wdpj","state":1,"sort":3,"mallSpeciesId":8},{"id":28,"iconUrl":"http://image.vrzbgw.com/upload/20191016/1622157564ba99.png","title":"购物车","jumpUrl":"client_gwc","state":1,"sort":4,"mallSpeciesId":8},{"id":29,"iconUrl":"http://image.vrzbgw.com/upload/20191016/16224415735362.png","title":"个人资料","jumpUrl":"client_grzl","state":1,"sort":5,"mallSpeciesId":8},{"id":30,"iconUrl":"http://image.vrzbgw.com/upload/20191016/1623079132804a.png","title":"商家入驻","jumpUrl":"client_sjrz","state":1,"sort":6,"mallSpeciesId":8},{"id":31,"iconUrl":"http://image.vrzbgw.com/upload/20191016/162338481b4f20.png","title":"主播申请","jumpUrl":"client_zbsq","state":1,"sort":7,"mallSpeciesId":8},{"id":32,"iconUrl":"http://image.vrzbgw.com/upload/20191016/16235980010dc2.png","title":"设置","jumpUrl":"client_sz","state":1,"sort":8,"mallSpeciesId":8},{"id":33,"iconUrl":"http://image.vrzbgw.com/upload/20191016/16243287266ddf.png","title":"反馈 ","jumpUrl":"client_fk","state":1,"sort":9,"mallSpeciesId":8},{"id":34,"iconUrl":"http://image.vrzbgw.com/upload/20191016/16252588953d15.png","title":"官方客服","jumpUrl":"client_gfkf","state":1,"sort":10,"mallSpeciesId":8},{"id":35,"iconUrl":"http://image.vrzbgw.com/upload/20191016/1626164868b71.png","title":"关于我们","jumpUrl":"client_gywm","state":1,"sort":11,"mallSpeciesId":8}]
     * errmsg : 执行成功
     */


    private int id;
    private String iconUrl;
    private String title;
    private String jumpUrl;
    private int state;
    private int sort;
    private int mallSpeciesId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getMallSpeciesId() {
        return mallSpeciesId;
    }

    public void setMallSpeciesId(int mallSpeciesId) {
        this.mallSpeciesId = mallSpeciesId;
    }

}
