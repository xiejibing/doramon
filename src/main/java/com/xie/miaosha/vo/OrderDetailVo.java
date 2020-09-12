package com.xie.miaosha.vo;

import com.xie.miaosha.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {
    private GoodsVo goodsVo;
    private OrderInfo orderInfo;
}
