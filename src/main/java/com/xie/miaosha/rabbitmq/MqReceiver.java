package com.xie.miaosha.rabbitmq;

import com.xie.miaosha.domain.MiaoshaOrder;
import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.redis.RedisService;
import com.xie.miaosha.service.GoodsService;
import com.xie.miaosha.service.MiaoshaService;
import com.xie.miaosha.service.OrderService;
import com.xie.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqReceiver {
    @Autowired
    RedisService redisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;
    private static Logger logger = LoggerFactory.getLogger(MqReceiver.class);


    @RabbitListener(queues = MqConfig.MIAOSHA_QUEUE)//监听哪个队列的消息
    public void receiveMiaoshaMsg(String message) {
        MiaoshaMessage miaoshaMessage = RedisService.stringToBean(message, MiaoshaMessage.class);
        //获取秒杀信息
        MiaoshaUser miaoshaUser = miaoshaMessage.getMiaoshaUser();
        Long goodsId = miaoshaMessage.getGoodsId();
        //查询库存
        GoodsVo goodsVo= goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stockCount = goodsVo.getStockCount();
        if (stockCount<=0) {
            return ;
        }
        //判断是否重复秒杀
        MiaoshaOrder order = orderService.getOrderByUserIdAndGoodsId(miaoshaUser.getId(), goodsId);
        if (order!=null) {
            return;
        }
        //秒杀---减库存，下订单
        miaoshaService.miaosha(miaoshaUser, goodsVo);
    }


    /**
     * ***********************************************学习内容******************************
     *
     * @param message
     */
    @RabbitListener(queues = MqConfig.QUEUE)//监听哪个队列的消息
    public void receive(String message) {
        logger.info("receive message:" + message);
    }

    @RabbitListener(queues = MqConfig.TOPIC_QUEUE1)//监听哪个队列的消息
    public void receiveTopic1(String message) {
        logger.info("topic1 receive message:" + message);
    }

    @RabbitListener(queues = MqConfig.TOPIC_QUEUE2)//监听哪个队列的消息
    public void receiveTopic2(String message) {
        logger.info("topic2 receive message:" + message);
    }

    @RabbitListener(queues = MqConfig.Headers_QUEUE)//监听哪个队列的消息
    public void receiveHeaders(byte[] message) {
        logger.info("Headers receive message:" + new String(message));
    }

}
