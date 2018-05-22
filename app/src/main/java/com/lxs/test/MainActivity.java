package com.lxs.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lxs.test.pay.PayModel;
import com.lxs.test.pay.PaymentActivity;
import com.lxs.test.pay.PingPay;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void weChatPay(View view) {
        //传一下支付方式和支付信息就好
        PayModel payModel = new PayModel();
        payModel.payType = PingPay.TYPE_WeCHAT;
        PingPay.createPayment(this, payModel);
    }

    public void alipay(View view) {
        PayModel payModel = new PayModel();
        payModel.payType = PingPay.TYPE_ALIPAY;
        PingPay.createPayment(this, payModel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PingPay.REQUEST_CODE_PAYMENT) {
            int type = data.getIntExtra(PingPay.PAY_TYPE, -1);
            switch (type) {
                case PingPay.TYPE_WeCHAT:
                    if (resultCode == PaymentActivity.PAY_SUCCESS) {
                        Toast.makeText(this, "微信支付成功", Toast.LENGTH_SHORT).show();
                    } else if (resultCode == PaymentActivity.PAY_CANCEL) {
                        Toast.makeText(this, "微信支付取消", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "微信支付失败" + data.getStringExtra(PaymentActivity.FAIL_DESC), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PingPay.TYPE_ALIPAY:
                    if (resultCode == PaymentActivity.PAY_SUCCESS) {
                        Toast.makeText(this, "支付宝支付成功", Toast.LENGTH_SHORT).show();
                    } else if (resultCode == PaymentActivity.PAY_CANCEL) {
                        Toast.makeText(this, "支付宝支付取消", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "支付宝支付失败" + data.getStringExtra(PaymentActivity.FAIL_DESC), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(this, "暂时没有这种方式", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
