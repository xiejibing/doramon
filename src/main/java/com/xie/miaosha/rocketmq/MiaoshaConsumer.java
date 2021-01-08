package com.xie.miaosha.rocketmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xie.miaosha.domain.MiaoshaOrder;
import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.service.GoodsService;
import com.xie.miaosha.service.MiaoshaService;
import com.xie.miaosha.service.OrderService;
import com.xie.miaosha.vo.GoodsVo;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消费者
 */
@Component
@RocketMQMessageListener(topic = "miaoshaTopic", consumerGroup = "miaosha_consumer_group")
public class MiaoshaConsumer implements RocketMQListener<String> {

    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;

    private final static Logger logger = LoggerFactory.getLogger(MiaoshaConsumer.class);

    @Override
    public void onMessage(String s) {
        logger.info("开始消费消息"+s);
        //如果抛出异常会重复去消费消息
        //throw new RuntimeException("认为制造异常");
        MiaoshaMessage message = JSON.parseObject(s, MiaoshaMessage.class);
        long goodsId = message.getGoodsId();
        MiaoshaUser user = message.getUser();
        //查询库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getGoodsStock();
        if(stock <= 0){
            return;
        }
        //判断是否重复秒杀
        MiaoshaOrder order = orderService.getOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if(order != null){
            return;
        }
        //执行减库存下订单操作
        logger.info("开始减库存下订单。。。");
        miaoshaService.miaosha(user, goodsVo);
    }
}
