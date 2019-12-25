package com.dbvr.baselibrary.model;

import java.io.Serializable;

public class AddressMo implements Serializable {
    private int id;
    //省
    private String province;
    //市
    private String city;
    //县（区）
    private String area;
    //详细地址
    private String address;

    //收货人名称
    private String consigneeName;
    //收货人手机号
    private String consigneePhone;
    //默认：（1.是 0.否)
    private int isDefault;

    //标签
    private int addressTag;//0,1,2 家公司学校


    public AddressMo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAddressTag() {
        return addressTag;
    }

    public void setAddressTag(int addressTag) {
        this.addressTag = addressTag;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
