package com.lxs.test.pay;

/**
 * Created by liuxiaoshuai on 2018/5/22.
 */

public interface PayResultListener {
    void paySuccess();

    void payFail(String status, String msg);

    void payCancel();
}
