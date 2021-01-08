package com.xie.miaosha.rocketmq.learn.base.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

/**
 * @author 14423
 * 异步生产者
 */
public class AsynProducer {
    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException {
        //1.创建生产者并指定组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        //2.指定namesrv地址
        producer.setNamesrvAddr("47.103.208.206:9876");
        //3.启动producer
        producer.start();
        for (int i = 0; i < 10; i++){
            int index = i;
            //消息
            Message message = new Message("base", "Tag2",("hello"+i+1).getBytes());
            //发送消息，SendCallback接收异步返回结果的回调
            producer.send(message, new SendCallback() {
                //成功回调
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("OK, index:"+index+",msgId:"+sendResult.getMsgId());
                }

                //失败回调
                @Override
                public void onException(Throwable e) {
                    System.out.println("error, index:"+index+e);
                    e.printStackTrace();
                }
            });
        }
        //
        Thread.sleep(3000);
        producer.shutdown();
    }
}
