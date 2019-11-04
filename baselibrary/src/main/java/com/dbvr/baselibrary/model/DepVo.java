package com.dbvr.baselibrary.model;

import java.io.Serializable;
import java.util.Date;

public class DepVo  implements Serializable {

    //部门名称（入驻商名称）
    private String name;
    //姓名
    private String username;
    private String mainCategory;//主营类目
    //手机号码
    private String phone;
    //身份证号
    private String idcard;
    //经营地址省
    private String productionProvince;
    //经营地址市
    private String productionCity;
    //经营地址 县/区
    private String productionCounty;
    //经营地址 详细地址
    private String productionAddress;
    //身份证正面
    private String idcartFacial;
    //身份证反面
    private String idcartBehind;
    //三证合一
    private String threeCertificates;
    //入驻协议：0不同意、1同意
    private Integer agreedAgreement;
    //审核结果 -1未审核 1审核通过 0 审核不通过
    private Integer auditStatus;
    //审核描述
    private String auditDescribe;
    //审核时间
    private Date auditTime;
    //有效时间
    private Date validTime;
    //创建时间
    private Date createTime;
    //APP登录userId
    private String userId;
    private String zipCode;
    //商家简介
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getProductionProvince() {
        return productionProvince;
    }

    public void setProductionProvince(String productionProvince) {
        this.productionProvince = productionProvince;
    }

    public String getProductionCity() {
        return productionCity;
    }

    public void setProductionCity(String productionCity) {
        this.productionCity = productionCity;
    }

    public String getProductionCounty() {
        return productionCounty;
    }

    public void setProductionCounty(String productionCounty) {
        this.productionCounty = productionCounty;
    }

    public String getProductionAddress() {
        return productionAddress;
    }

    public void setProductionAddress(String productionAddress) {
        this.productionAddress = productionAddress;
    }

    public String getIdcartFacial() {
        return idcartFacial;
    }

    public void setIdcartFacial(String idcartFacial) {
        this.idcartFacial = idcartFacial;
    }

    public String getIdcartBehind() {
        return idcartBehind;
    }

    public void setIdcartBehind(String idcartBehind) {
        this.idcartBehind = idcartBehind;
    }

    public String getThreeCertificates() {
        return threeCertificates;
    }

    public void setThreeCertificates(String threeCertificates) {
        this.threeCertificates = threeCertificates;
    }

    public Integer getAgreedAgreement() {
        return agreedAgreement;
    }

    public void setAgreedAgreement(Integer agreedAgreement) {
        this.agreedAgreement = agreedAgreement;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditDescribe() {
        return auditDescribe;
    }

    public void setAuditDescribe(String auditDescribe) {
        this.auditDescribe = auditDescribe;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "DepVo{" +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", idcard='" + idcard + '\'' +
                ", productionProvince='" + productionProvince + '\'' +
                ", productionCity='" + productionCity + '\'' +
                ", productionCounty='" + productionCounty + '\'' +
                ", productionAddress='" + productionAddress + '\'' +
                ", idcartFacial='" + idcartFacial + '\'' +
                ", idcartBehind='" + idcartBehind + '\'' +
                ", threeCertificates='" + threeCertificates + '\'' +
                ", agreedAgreement=" + agreedAgreement +
                ", auditStatus=" + auditStatus +
                ", auditDescribe='" + auditDescribe + '\'' +
                ", auditTime=" + auditTime +
                ", validTime=" + validTime +
                ", createTime=" + createTime +
                ", userId='" + userId + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
