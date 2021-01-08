package com.xie.miaosha.rocketmq.learn.base.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author 14423
 * 同步生产者
 */
public class SyncProducer {

    public static void main(String[] args) throws MQClientException {
        //1.创建生产者并指定组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        //2.指定namesrv地址
        producer.setNamesrvAddr("47.103.208.206:9876");
        //3.启动producer
        producer.start();
        //4.创建消息对象，指定主题topic,Tag和消息体
        for(int i =0; i < 10; i++){
            Message message = new Message("base", "Tag1",("hello"+i+1).getBytes());
            //5.发送消息
            try {
                SendResult result = producer.send(message);
                SendStatus status = result.getSendStatus();
                String id = result.getMsgId();
                //消息接收队列ID
                int queueId = result.getMessageQueue().getQueueId();
                System.out.println("发送状态"+status+", 消息ID"+id +", 队列"+queueId);
                TimeUnit.SECONDS.sleep(1);
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //关闭生产者
        producer.shutdown();
    }
}
