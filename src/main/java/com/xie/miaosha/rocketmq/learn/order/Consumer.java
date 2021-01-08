package com.xie.miaosha.rocketmq.learn.order;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 14423
 * 顺序消费,针对同一个队列里的消息，使用同一个线程去消费，保证消息是按顺序消费的
 */
public class Consumer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        consumer.setNamesrvAddr("47.103.208.206:9876");
        consumer.subscribe("OrderTopic","*");
        //注册消息监听器
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                for (int i = 0; i < msgs.size(); i++) {
                    MessageExt msg =  msgs.get(i);
                    System.out.println("Tread:"+Thread.currentThread().getName()+", queueId="+msg.getQueueId()+", content:"+
                            new String(msg.getBody()));
                    //模拟业务逻辑处理
                    try{
                        TimeUnit.SECONDS.sleep(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        System.out.println("Consumer Started.");
    }

}
