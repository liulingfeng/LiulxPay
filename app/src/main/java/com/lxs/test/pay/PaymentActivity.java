package com.lxs.test.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by liuxiaoshuai on 2018/5/18.
 * 支付发起页，也是微信的支付回调页
 */

public class PaymentActivity extends Activity implements IWXAPIEventHandler, PayResultListener {
    private IWXAPI wxApi;
    private PayHelp payHelp;
    public static final String FAIL_DESC = "desc";
    public static final int PAY_SUCCESS = 0;
    public static final int PAY_CANCEL = 1;
    public static final int PAY_FAIL = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wxApi = WXAPIFactory.createWXAPI(this, PayConstants.APP_ID);
        wxApi.handleIntent(getIntent(), this);

        Intent intent = getIntent();
        PayModel payModel = (PayModel) intent.getSerializableExtra("payModel");
        payHelp = new PayHelp(this);
        payHelp.setPayResultListener(this);
        if (payModel.payType == PingPay.TYPE_WeCHAT) {
            payHelp.memberPay(PingPay.TYPE_WeCHAT, payModel);
        } else {
            payHelp.memberPay(PingPay.TYPE_ALIPAY, payModel);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        wxApi.handleIntent(intent, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payHelp.removeAlipay();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            /*
            0：成功
            -1：错误
            -2：用户取消
             */
            int errorCode = baseResp.errCode;
            Intent intent = new Intent();
            intent.putExtra(PingPay.PAY_TYPE, PingPay.TYPE_WeCHAT);
            switch (errorCode) {
                case 0:
                    setResult(PAY_SUCCESS, intent);
                    break;
                case -2:
                    setResult(PAY_CANCEL, intent);
                    break;
                default:
                    intent.putExtra(FAIL_DESC, errorCode + "");
                    setResult(PAY_FAIL, intent);
                    break;
            }
            finish();
        }
    }

    @Override
    public void paySuccess() {
        Intent intent = new Intent();
        intent.putExtra(PingPay.PAY_TYPE, PingPay.TYPE_ALIPAY);
        setResult(PAY_SUCCESS, intent);
        finish();
    }

    @Override
    public void payFail(String status, String msg) {
        if ("toast".equals(status)) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(PingPay.PAY_TYPE, PingPay.TYPE_ALIPAY);
        intent.putExtra(FAIL_DESC, status);
        setResult(PAY_FAIL, intent);
        finish();
    }

    @Override
    public void payCancel() {
        Intent intent = new Intent();
        intent.putExtra(PingPay.PAY_TYPE, PingPay.TYPE_ALIPAY);
        setResult(PAY_CANCEL, intent);
        finish();
    }
}
