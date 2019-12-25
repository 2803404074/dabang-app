package com.dbvr.baselibrary.model

import java.io.Serializable

class GoodsVo :Serializable{
    /**
     * 商品id
     */
    var id: Int = 0

    /**
     * 商品名称
     */
    var name: String? = null
    /**
     * 推广标题
     */
    var title: String? = null

    /**
     * 商品小视频
     */
    var video: String? = null
    /**
     * 零售价
     */
    var retailPrice: String? = null

    /**
     * 市场价
     */
    var marketPrice: String? = null

    /**
     * 物流价格
     */
    var logisticsPrice: String? = null
    /**
     * 剩余库存
     */
    var remainingInventory: Int? = null

    /**
     * 销售量
     */
    var salesVolume: Int? = null
    /**
     * 所属商家ID
     */
    var merchantsId: Int? = null

    /**
     * 商品的轮播图
     */
    var goodsImgs: List<String>? = null
    /**
     * 商品的详情内容
     */
    var goodsDesc: String? = null

    var productName: String? = null

    /**
     * 分类名称
     */
    var categoryName: String? = null

    /**
     * 产品列表
     */
    var goodsProductList: List<GoodsProductVo>? = null

    /**
     * 所属商家名称
     */
    var merchantsName: String? = null

    /**
     * 所属商家logo
     */
    var merchantsLogo: String? = null

    var receivingAddress:String?=null
    var receivingAddressId:String?=null

    var supportService:String?=null

    var introduce:String?=null

}