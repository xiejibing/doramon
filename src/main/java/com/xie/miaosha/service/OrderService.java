package com.xie.miaosha.service;

import com.xie.miaosha.dao.OrderDao;
import com.xie.miaosha.domain.MiaoshaOrder;
import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.domain.OrderInfo;
import com.xie.miaosha.redis.OrderKey;
import com.xie.miaosha.redis.RedisService;
import com.xie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;
    @Autowired
    RedisService redisService;

    public MiaoshaOrder getOrderByUserIdAndGoodsId(long userId, long goodsId){
        //return orderDao.getByUserIdAndGoodsId(userId,goodsId);
        //优化，直接从缓存里面找,因为生成订单数据库时也写了缓存,缓存里有那就有，缓存没有就没有
        return redisService.get(OrderKey.getByUidAndGid,""+userId+"_"+goodsId,MiaoshaOrder.class);
    }

    public OrderInfo getOrderById(long orderId){
        return orderDao.getOrderById(orderId);
    }


    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(502200L);
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        //写入数据库
        orderDao.insertOrderInfo(orderInfo);
        long orderId = orderInfo.getId();
        //生成秒杀订单
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setOrderId(orderId);
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        //写入缓存
        redisService.set(OrderKey.getByUidAndGid,""+user.getId()+"_"+goodsVo.getId(),miaoshaOrder);
        return orderInfo;
    }
}
