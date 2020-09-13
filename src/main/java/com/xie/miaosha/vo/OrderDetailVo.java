package com.xie.miaosha.vo;

import com.xie.miaosha.domain.OrderInfo;
import lombok.Data;


/**
 * @author 14423
 */
public class OrderDetailVo {
    private GoodsVo goodsVo;
    private OrderInfo orderInfo;

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }
}
