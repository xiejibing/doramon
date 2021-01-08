package com.xie.miaosha.rocketmq;

import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.domain.User;

/**
 * 秒杀消息对象
 * @author 14423
 */
public class MiaoshaMessage {
    private MiaoshaUser user;
    private Long goodsId;

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "MiaoshaMessage{" +
                "user=" + user +
                ", goodsId=" + goodsId +
                '}';
    }
}
