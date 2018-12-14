package com.example.user.paydemo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.example.user.paydemo.bean.AlipayEntity;
import com.example.user.paydemo.bean.PayResult;
import com.example.user.paydemo.bean.PayZfbResultEntity;
import com.example.user.paydemo.bean.WxPayEntity;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by user on 2018/10/16.
 * 支付工具类
 */

public class PayUtils {
    //开发者的appid,拿到正式的appid，就要替换
    public static final String WECHAT_APP_ID = "*************";
    //商户id
    public static final String PARENT_ID = "*************";

    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;
    private Activity activity;

    public PayUtils(IWXAPI api, Activity activity) {
        this.activity = activity;
        this.api = api;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SDK_PAY_FLAG:// 支付宝
                    PayZfbResultEntity payZfbResultEntity = (PayZfbResultEntity) msg.obj;
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    PayResult payResult = payZfbResultEntity.payResult;
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        if (null != resultListener) {
                            resultListener.aliPayCallBack();
                        }
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        if (null != resultListener) {
                            resultListener.aliPayCancle();
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(activity, "支付失败或未安装支付宝", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * 支付宝支付
     *
     * @param alipayEntity 服务器传回来的支付数据
     */
    public void payByAliPay(final AlipayEntity alipayEntity) {
        //需要处理后台服务器返回的数据信息
        // 后台返回的zfb 信息
        if (TextUtils.isEmpty(alipayEntity.getOrderid())) return;

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(alipayEntity.getOrderstring(), true);

                PayZfbResultEntity payZfbResultEntity = new PayZfbResultEntity();
                payZfbResultEntity.payResult = new PayResult(result);
                payZfbResultEntity.orderid = alipayEntity.getOrderid();
                try {
                    JSONObject jsonObject = new JSONObject(payZfbResultEntity.payResult.getResult());
                    //   LogUtils.LOG_D(PayUtils.class, "支付宝返回结果" + jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = payZfbResultEntity;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 微信支付
     */
    public void payByWechat(final WxPayEntity wxPayEntity) {
        if (api.isWXAppInstalled() && api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
        } else {
            // mView.showToast("未安装微信或微信版本过低！", R.mipmap.icon_fail);
            return;
        }
        if (null == wxPayEntity)
            return;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //  LogUtils.LOG_D(getClass(), "正在生成订单，请稍等.");
                PayReq request = new PayReq();
                //
                request.appId = WECHAT_APP_ID;
                request.partnerId = PARENT_ID;
                // 后台返回
                request.prepayId = wxPayEntity.getPrepay_id();
                request.packageValue = wxPayEntity.getPackageName();
                request.nonceStr = wxPayEntity.getNoncestr();
                request.timeStamp = wxPayEntity.getTimestamp();
                request.sign = wxPayEntity.getSign();
                //  LogUtils.LOG_D(getClass(), "正常调起支付.");
                boolean sendReq = api.sendReq(request);
                // LogUtils.LOG_D(getClass(), "支付请求发送-----" + sendReq);

                return null;
            }
        }.execute();

    }

    public interface PayResultListener {
        /**
         * 阿里支付完成回掉
         */
        void aliPayCallBack();

        /**
         * 取消支付
         */
        void aliPayCancle();
    }

    private PayResultListener resultListener;

    public void setResultListener(PayResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public void release() {
        mHandler.removeCallbacksAndMessages(null);
    }
}
