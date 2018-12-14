package com.example.user.paydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.user.paydemo.bean.AlipayEntity;
import com.example.user.paydemo.bean.WxPayEntity;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class MainActivity extends AppCompatActivity implements PayUtils.PayResultListener {
    private PayUtils payUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        IWXAPI api = WXAPIFactory.createWXAPI(this, PayUtils.WECHAT_APP_ID);
        api.registerApp(PayUtils.WECHAT_APP_ID);// 将该app注册到微信

        payUtils = new PayUtils(api, this);
        String jsonData = "这个是后台返回给我们的支付数据";

        //支付方式为支付宝
        payByAliPay(jsonData);

        //微信支付
        payByWeChat(jsonData);

    }

    /**
     * 微信支付
     *
     * @param jsonData 后台返回的支付数据
     */
    private void payByWeChat(String jsonData) {
        WxPayEntity wxPayEntity = JsonUtils.json2Object(jsonData, WxPayEntity.class);
        payUtils.payByWechat(wxPayEntity);
    }

    /**
     * 支付宝支付
     *
     * @param jsonData 后台返回的支付数据
     */
    private void payByAliPay(String jsonData) {
        //将支付数据转换成为实体对象
        AlipayEntity alipayEntity = JsonUtils.json2Object(jsonData, AlipayEntity.class);
        payUtils.payByAliPay(alipayEntity);
    }

    @Override
    public void aliPayCallBack() {
        //付款成功的回调
    }

    @Override
    public void aliPayCancle() {
        //取消支付的回调
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payUtils.release();
    }

    /**
     * 微信支付成功
     *
     * @param payResp
     */
    public void wxPaySuccess(PayResp payResp) {

    }

    /**
     * 微信支付取消
     */
    public void wxPayCancle() {
        aliPayCancle();
    }
}
