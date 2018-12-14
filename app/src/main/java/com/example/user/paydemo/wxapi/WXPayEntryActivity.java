package com.example.user.paydemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.example.user.paydemo.AppManager;
import com.example.user.paydemo.MainActivity;
import com.example.user.paydemo.PayUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信回调页面
 */
public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new RelativeLayout(this));
        api = WXAPIFactory.createWXAPI(this, PayUtils.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int code = baseResp.errCode;
        //    LogUtils.LOG_D(PayUtils.class, "返回的code=" + code);
        PayResp payResp = (PayResp) baseResp;
        switch (code) {
            case 0://支付成功后的界面
                paySucess(payResp);
                finish();
                break;
            case -1:
                //   showToast("签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、您的微信账号异常等。");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
                break;
            case -2://用户取消支付后的界面
                payCancle();
                finish();
                break;
        }
    }

    private void payCancle() {
        //从activity栈中取出需要支付页面
        Activity activity = AppManager.getBaseActivity(MainActivity.class);
        if (activity != null && !activity.isFinishing()) {
            MainActivity payActivity = (MainActivity) activity;
            //payActivity.aliPayCancle();
            payActivity.wxPayCancle();
        }
    }

    private void paySucess(PayResp payResp) {
        //从activity栈中取出需要支付页面
        Activity activity = AppManager.getBaseActivity(MainActivity.class);
        if (activity != null && !activity.isFinishing()) {
            MainActivity payActivity = (MainActivity) activity;
            payActivity.wxPaySuccess(payResp);
        }
    }
}
