package com.dbvr.baselibrary.model

import java.io.File
import java.io.Serializable

class DepVoRz : Serializable {

    var deptId: Int = 0
    //部门名称（入驻商名称）
    var name: String? = null
    //姓名
    var username: String? = null
    //手机号码
    var phone: String? = null
    //身份证号码
    var idcard: String? = null
    //经营地址省
    var productionProvince: String? = null
    //经营地址市
    var productionCity: String? = null
    //经营地址 县/区
    var productionCounty: String? = null
    //经营地址 详细地址
    var productionAddress: String? = null
    //经营类别(对应门店分类下Id)
    var foodType: Int = 0
    //APP登录userId
    var userId: String? = null
    //店铺邮箱
    var email: String? = null
    var logo: String? = null
    //经营地址国家
    var country: String? = null
    //商家简介
    var synopsis: String? = null
    //上架经营类别
    var mainCategory: String? = null

    var fileFont:File?=null
    var fileBack:File?=null
    var threeCertificates:File?=null
}
