package com.xie.miaosha.rocketmq.learn.transaction;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author 14423
 * 使用负载均衡模式消费,多个消费者共同消费队列消息，每个消费者处理的消息不同
 * 广播模式，所有消费者消费相同的消息
 */
public class Consumer {
    public static void main(String[] args) throws MQClientException {
        //1.实例化消费者，并指定消费组名：PUSH是broker向消费者推，PULL是主动去拉取
       DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group3");
        //指定nameserver
        consumer.setNamesrvAddr("47.103.208.206:9876");
        //订阅Topic
        consumer.subscribe("TransactionTopic","*");
        //设置负载均衡模式消费
        //consumer.setMessageModel(MessageModel.CLUSTERING);
        //设置广播模式的消费
        //consumer.setMessageModel(MessageModel.BROADCASTING);
        //指定消息到来时的需要执行的回调
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), list);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        //开启消费者
        consumer.start();
        System.out.printf("Consumer started.%n");
    }


}
