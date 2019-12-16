package com.dbvr.baselibrary.model

import java.io.Serializable

/**
 * 直播短视频商品类型
 */
class MainMo : Serializable {
    //公共属性
    var id: Int = 0//短视频或直播的ID
    var labelId : String ?= null//分类
    var live: Boolean = false//是否是直播
    var isFollow: Boolean = false//是否已关注
    var title: String? = null//直播/短视频 标题
    var coverUrl: String? = null//直播/短视频 封面
    var userId: String? = null//用户ID
    var nickName: String? = null//用户昵称
    var headUrl: String? = null//用户头像
    var goodsTitle: String? = null//商品标题
    var goodsPrice: String? = null//商品价钱
    var goodsCover: String? = null//商品封面

    //直播属性
    var permanentResidence: String? = null//常住地
    var jumpNo: String? = null//跳跳号
    var fansNumber: String? = null//粉丝量
    var followNumber: String? = null//关注量
    var goodsNumber: String? = null//商品数量
    var roomId: String? = null//房间id
    var lookNum: String? = null//观看人数

    //短视频属性
    var praseCount: String? = null//点赞数量
    var commentNum: String? = null//评论数量
    var videoUrl: String? = null//播放地址


}
