package com.dbvr.baselibrary.model

/**
 * 产品
 */
class GoodsProductVo {

    var isCheck:Boolean?= false
    var id: Int? = null
    //产品名称
    var productName: String? = null
    //剩余库存
    var remainingInventory: Int? = null
    //零售价格
    var retailPrice: String? = null
    //市场价格
    var marketPrice: String? = null
    //物流费
    var logisticsPrice: String? = null
    //产品图片
    var pictureUrl: String? = null
}