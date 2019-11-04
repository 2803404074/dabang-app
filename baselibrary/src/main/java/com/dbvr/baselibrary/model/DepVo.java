package com.dbvr.baselibrary.model;

import java.io.Serializable;
import java.util.Date;

public class DepVo  implements Serializable {

    /**
     * deptId : 182
     * parentId : 3
     * name : 富贵花黄金季节
     * orderNum : 0
     * username : 急急急
     * phone : 18655167655
     * idcard : null
     * productionProvince : 内蒙古自治区
     * productionCity : 鄂尔多斯市
     * productionCounty : 准格尔旗
     * productionAddress : null
     * idcartFacial : http://image.vrzbgw.com/idcartFacial-17fee37f-e8ad-4462-90a8-40a792ae9c2b
     * idcartBehind : http://image.vrzbgw.com/idcartBehind-8ac9e2a5-6c4d-464a-a498-c399104bb839
     * threeCertificates : http://image.vrzbgw.com/threeCertificates-a709aebc-8eba-4431-bf00-b158dabefd2f
     * foodType : 0
     * agreedAgreement : 1
     * auditStatus : 0
     * auditDescribe : null
     * auditTime : null
     * validTime : null
     * auditUserId : null
     * createTime : 2019-11-04 11:13:13
     * userId : 234
     * email : null
     * logo : null
     * country : null
     * zipCode : null
     * synopsis : null
     * mainCategory : null
     */

    private int deptId;
    private int parentId;
    //部门名称（入驻商名称）
    private String name;
    //排序
    private int orderNum;
    //姓名
    private String username;
    //手机号码
    private String phone;
    //身份证号码
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
    //经营类别(对应门店分类下Id)
    private int foodType;
    //入驻协议：0不同意、1同意
    private int agreedAgreement;
    //审核结果 -1未审核 1审核通过 0 审核不通过
    private int auditStatus;
    //审核描述
    private String auditDescribe;
    //审核时间
    private String auditTime;
    //有效时间
    private String validTime;
    //审核人员
    private String auditUserId;
    //创建时间
    private String createTime;
    //APP登录userId
    private String userId;
    //店铺邮箱
    private String email;
    private String logo;
    //经营地址国家
    private String country;
    //邮编
    private String zipCode;
    //商家简介
    private String synopsis;
    //上架经营类别
    private String mainCategory;

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
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

    public int getFoodType() {
        return foodType;
    }

    public void setFoodType(int foodType) {
        this.foodType = foodType;
    }

    public int getAgreedAgreement() {
        return agreedAgreement;
    }

    public void setAgreedAgreement(int agreedAgreement) {
        this.agreedAgreement = agreedAgreement;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditDescribe() {
        return auditDescribe;
    }

    public void setAuditDescribe(String auditDescribe) {
        this.auditDescribe = auditDescribe;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }
}
