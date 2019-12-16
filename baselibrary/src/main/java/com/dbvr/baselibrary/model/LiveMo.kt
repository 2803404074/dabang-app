package com.dbvr.baselibrary.model

import java.io.Serializable

/**
 * 直播短视频商品类型
 */
class LiveMo : Serializable {

    var id: Int = 0//直播的ID
    var labelId:String?=null
    var praseCount: String? = null//点赞数量
    //var isFollow: Boolean = false//是否已关注
    var title: String? = null//直播 标题
    var coverUrl: String? = null//直播封面
    var userId: String? = null//用户ID
    var nickName: String? = null//用户昵称
    var headUrl: String? = null//用户头像

    //直播属性
    var permanentResidence: String? = null//常住地
    var jumpNo: String? = null//跳跳号
    var fansNumber: String? = null//粉丝量
    var followNumber: String? = null//关注量
    var goodsNumber: String? = null//商品数量
    var roomId: String? = null//房间id
    var lookNum: String? = null//观看人数

}
