package com.example.user.paydemo.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/***
 * 调起微信支付的实体类
 *
 */
public class WxPayEntity implements Serializable{
    @Expose
    private String prepay_id;
    @Expose
    private String orderid;
    @Expose
    private String noncestr;
    @Expose
    private String timestamp;
    @Expose
    private String sign;
    @SerializedName("package")
    private String packageName = "Sign=WXPay"; //固定写死

    public String getPrepay_id() {
        return prepay_id;
    }

    public WxPayEntity setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
        return this;
    }

    public String getOrderid() {
        return orderid;
    }

    public WxPayEntity setOrderid(String orderid) {
        this.orderid = orderid;
        return this;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public WxPayEntity setNoncestr(String noncestr) {
        this.noncestr = noncestr;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public WxPayEntity setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public WxPayEntity setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public WxPayEntity setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }
}
