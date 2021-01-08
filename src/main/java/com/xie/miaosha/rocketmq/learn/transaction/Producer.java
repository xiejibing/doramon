package com.xie.miaosha.rocketmq.learn.transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author 14423
 * 同步生产者
 */
public class Producer {

    public static void main(String[] args) throws MQClientException, InterruptedException {
        //1.创建生产者并指定组名
        TransactionMQProducer producer = new TransactionMQProducer("group3");
        //2.指定namesrv地址
        producer.setNamesrvAddr("47.103.208.206:9876");
        //设置事务监听器,消息发送完毕后会执行到这里。
        producer.setTransactionListener(new TransactionListener() {
            //半发送消息后，执行本地事务
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                System.out.println("执行本地事务");
                if (StringUtils.equals("TagA", msg.getTags())){
                    //消息提交，对消费者可见
                    return LocalTransactionState.COMMIT_MESSAGE;
                }else if(StringUtils.equals("TagB", msg.getTags())){
                    //消息回滚，删除
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }else {
                    //中间状态
                    return LocalTransactionState.UNKNOW;
                }
            }

            //如果是中间状态，就要再次回来查询本地事务的执行结果
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                System.out.println("MQ检查消息Tag【"+msg.getTags()+"】的本地的事务执行结果");
                //返回一个结果
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        //3.启动producer
        producer.start();
        //4.创建消息对象，指定主题topic,Tag和消息体
        String[] tags = new String[]{"TagA", "TagB", "TagC"};
        for(int i =0; i < 3; i++){
            Message message = new Message("TransactionTopic", tags[i], ("hello"+i+1).getBytes());
            SendResult result = producer.sendMessageInTransaction(message,null);
            System.out.println("发送结果"+result);
            TimeUnit.SECONDS.sleep(1);
        }
        //关闭生产者
        //producer.shutdown();
    }
}
