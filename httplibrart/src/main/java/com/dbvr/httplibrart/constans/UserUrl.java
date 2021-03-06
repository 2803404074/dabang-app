package com.dbvr.httplibrart.constans;

public class UserUrl {

    //登陆
    public static final String login ="api/login";

    //发送验证码
    public static final String sendCode ="api/sms/sendCode";
    //商家入驻验证手机号码
    public static final String toValidateDeptCode ="api/dept/toValidateDeptCode";
    public static final String addDeptState ="api/dept/addDeptState"; //查询商户入驻状态
    public static final String addDept ="api/dept/addDept"; //申请商户入驻
    public static final String addAnchorState ="api/anchor/addAnchorState"; //查询入驻状态
    public static final String addAnchor ="api/anchor/addAnchor"; //查询入驻状态
    public static final String submitFeedback ="api/config/submitFeedback"; //问题反馈
    public static final String updateApp ="api/config/updateApp"; //问题反馈
    public static final String updateUser ="/api/update";    //修改资料
}
