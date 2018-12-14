package com.example.user.paydemo.bean;

import com.google.gson.annotations.Expose;

/**
 * 调起支付宝支付的实体类
 */
public class AlipayEntity {
    @Expose
    private String orderstring;
    @Expose
    private String orderid;

    public String getOrderstring() {
        return orderstring;
    }

    public AlipayEntity setOrderstring(String orderstring) {
        this.orderstring = orderstring;
        return this;
    }

    public String getOrderid() {
        return orderid;
    }

    public AlipayEntity setOrderid(String orderid) {
        this.orderid = orderid;
        return this;
    }
}
