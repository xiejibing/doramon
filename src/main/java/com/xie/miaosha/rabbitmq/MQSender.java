package com.xie.miaosha.rabbitmq;

import com.xie.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {
    @Autowired
    AmqpTemplate amqpTemplate;
    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    public  void sendMiaoshaMessage(MiaoshaMessage message){
        String msg = RedisService.beanToString(message);//ｂｅａｎ ---> string
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
        log.info("send message:"+msg);
    }

    public  void send(Object message){
        String msg = RedisService.beanToString(message);//ｂｅａｎ ---> string
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
        log.info("send message:"+msg);
    }

    public  void sendTopic(Object message){
        String msg = RedisService.beanToString(message);//ｂｅａｎ ---> string
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1","MQ1:"+msg);//发送给哪个交换机的，绑定的哪个队列
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2","MQ2:"+msg);
        log.info("send message:"+msg);
    }

    public  void sendFanout(Object message){
        String msg = RedisService.beanToString(message);//ｂｅａｎ ---> string
        amqpTemplate.convertAndSend(MQConfig.Fanout_EXCHANGE,"","MQ:"+msg);//发送给哪个交换机的
        log.info("send message:"+msg);
    }

    public  void sendHeaders(Object message){
        String msg = RedisService.beanToString(message);//ｂｅａｎ ---> string
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("header1","value1");
        messageProperties.setHeader("header2","value2");
        Message obj = new Message(msg.getBytes(),messageProperties);
        amqpTemplate.convertAndSend(MQConfig.Headers_EXCHANGE,"",obj);//发送给哪个交换机的
        log.info("send message:"+msg);
    }

}
