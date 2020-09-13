package com.xie.miaosha.rabbitmq;

import com.xie.miaosha.domain.MiaoshaUser;
import lombok.Data;

/**
 * @author 14423
 */
public class MiaoshaMessage {
    private MiaoshaUser miaoshaUser;
    private Long goodsId;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
