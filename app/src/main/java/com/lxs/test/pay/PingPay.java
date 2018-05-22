package com.lxs.test.pay;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by liuxiaoshuai on 2018/5/18.
 */

public class PingPay {
    public static int REQUEST_CODE_PAYMENT = 1010;
    public static final String PAY_TYPE = "payType";
    public static final int TYPE_WeCHAT = 0;
    public static final int TYPE_ALIPAY = 1;

    public static void createPayment(Activity activity, PayModel payModel) {
        Intent intent = new Intent(activity, PaymentActivity.class);
        intent.putExtra("payModel", payModel);
        activity.startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }
}
