package com.lxs.test.pay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by liuxiaoshuai on 2018/5/21.
 */

public class PayHelp {
    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI iwxapi; //微信支付api
    private WeakReference<Activity> activity;
    private PayResultListener payResultListener;
    private String orderInfo;

    public PayHelp(Activity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public void setPayResultListener(PayResultListener payResultListener) {
        this.payResultListener = payResultListener;
    }

    public void memberPay(int payType, PayModel payModel) {
        switch (payType) {
            case PingPay.TYPE_ALIPAY:
                aliPay(payModel);
                break;
            case PingPay.TYPE_WeCHAT:
                weChatPay(payModel);
                break;
        }
    }

    private MyHandler sHandler = new MyHandler(payResultListener);

    private static class MyHandler extends Handler {
        private PayResultListener payResultListener;

        private MyHandler(PayResultListener payResultListener) {
            this.payResultListener = payResultListener;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            Map<String, String> result = (Map<String, String>) msg.obj;
            String memo = result.get("memo");
            String resultStatus = result.get("resultStatus");
            switch (resultStatus) {
                case "9000":
                    //成功
                    payResultListener.paySuccess();
                    break;
                case "6001":
                    //取消
                    payResultListener.payCancel();
                    break;
                default:
                    //失败
                    payResultListener.payFail(resultStatus, "支付宝支付" + memo);
                    break;
            }
        }
    }

    private Runnable payRunnable = new Runnable() {
        @Override
        public void run() {
            PayTask aliPay = new PayTask(activity.get());
            Map<String, String> result = aliPay.payV2(orderInfo, true);
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            sHandler.sendMessage(msg);
        }
    };

    /**
     * 支付宝支付/签名在服务端
     */
    private void aliPay(PayModel payModel) {
        this.orderInfo = payModel.sign;//服务端完成签名后的支付信息就是订单信息
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //Activity退出的时候取消支付请求，避免造成内存泄漏
    public void removeAlipay() {
        if (sHandler != null && payRunnable != null) {
            sHandler.removeCallbacks(payRunnable);
        }
    }

    private void weChatPay(PayModel payModel) {
        if (iwxapi == null) {
            iwxapi = WXAPIFactory.createWXAPI(activity.get(), PayConstants.APP_ID);
            iwxapi.registerApp(PayConstants.APP_ID);// 将该app注册到微信
        }

        if (iwxapi.isWXAppInstalled() || iwxapi.isWXAppSupportAPI()) {
            PayReq payReq = new PayReq();
            payReq.appId = payModel.appId;
            payReq.partnerId = payModel.partnerid;
            payReq.prepayId = payModel.prepayid;
            payReq.packageValue = payModel.packageValue;
            payReq.nonceStr = payModel.noncestr;
            payReq.timeStamp = payModel.timestamp;
            payReq.sign = payModel.sign;
            iwxapi.sendReq(payReq);
        } else {
            //错误回调
            payResultListener.payFail("toast", "您未安装微信");
        }
    }
}
