package com.xie.miaosha.rabbitmq;

import com.xie.miaosha.domain.MiaoshaUser;
import lombok.Data;

@Data
public class MiaoshaMessage {
    private MiaoshaUser miaoshaUser;
    private Long goodsId;
}
