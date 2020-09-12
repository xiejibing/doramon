package com.xie.miaosha.controller;

import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.domain.OrderInfo;
import com.xie.miaosha.result.CodeMsg;
import com.xie.miaosha.result.Result;
import com.xie.miaosha.service.GoodsService;
import com.xie.miaosha.service.OrderService;
import com.xie.miaosha.vo.GoodsVo;
import com.xie.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> getOrderDetail(MiaoshaUser user, @RequestParam("orderId") long orderId){
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if (orderInfo == null)
            return Result.error(CodeMsg.ORDER_NOT_EXISTS);
        Long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        orderDetailVo.setOrderInfo(orderInfo);
        orderDetailVo.setGoodsVo(goodsVo);
        return Result.success(orderDetailVo);
    }
}
