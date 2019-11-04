package com.dbvr.baselibrary.model;


import java.io.Serializable;
import java.util.Date;

/**
 * 主播表实体
 * 表名 db_anchor
 *
 * @author LMQ
 * @email
 * @date 2019-10-28 10:55:08
 */

public class AnchorVo implements Serializable {

    //主播ID
    private Long id;
    //用户id
    private Long userId;
    //真实姓名
    private String name;
    //身份证号
    private String idcard;
    //银行卡号
    private String creditCard;
    //身份证正面
    private String idcardFace;
    //身份证背面
    private String idcardBack;
    //手持身份证
    private String idcardHand;
    //电话
    private String phone;
    //主播协议：0不同意 1同意
    private Integer agreedAgreement;
    //状态：审核结果 -3已删除 -2禁用 -1未审核 1审核通过 0 审核不通过
    private Integer auditStatus;
    //审核时间
    private String auditTime;
    //有效时间
    private String validTime;
    //审核人员
    private String auditUserId;
    //创建时间
    private String createTime;
    //主播类型（0公司合作，1申请入驻）
    private String anchorType;
    //备注
    private String remarks;
    //审核失败描述
    private String auditDescribe;

    public String getAuditDescribe() {
        return auditDescribe;
    }

    public void setAuditDescribe(String auditDescribe) {
        this.auditDescribe = auditDescribe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
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

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getIdcardFace() {
        return idcardFace;
    }

    public void setIdcardFace(String idcardFace) {
        this.idcardFace = idcardFace;
    }

    public String getIdcardBack() {
        return idcardBack;
    }

    public void setIdcardBack(String idcardBack) {
        this.idcardBack = idcardBack;
    }

    public String getIdcardHand() {
        return idcardHand;
    }

    public void setIdcardHand(String idcardHand) {
        this.idcardHand = idcardHand;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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


    public String getAnchorType() {
        return anchorType;
    }

    public void setAnchorType(String anchorType) {
        this.anchorType = anchorType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
