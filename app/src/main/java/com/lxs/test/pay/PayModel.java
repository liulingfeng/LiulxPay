package com.lxs.test.pay;

import java.io.Serializable;

/**
 * Created by liuxiaoshuai on 2018/5/22.
 */

public class PayModel implements Serializable{
    public int payType;
    //应用id
    public String appId;

    //商户id
    public String partnerid;

    //预支付交易会话id
    public String prepayid;

    //扩展字段
    public String packageValue;

    //随机字符串
    public String noncestr;

    //时间戳
    public String timestamp;

    //签名
    public String sign;
}
