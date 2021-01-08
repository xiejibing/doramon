package com.xie.miaosha.rocketmq;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author 14423
 */
@Component
@RocketMQTransactionListener(txProducerGroup = "miaosha_producer_group1")
public class ProducerTxMsgListener implements RocketMQLocalTransactionListener {
    Logger logger = LoggerFactory.getLogger(ProducerTxMsgListener.class);
    /**
    消息发送成功后执行本地事务
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        logger.info("模拟执行本地事务");
        return RocketMQLocalTransactionState.COMMIT;
    }

    /**
     * 回查
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        logger.info("模拟回查");
        return RocketMQLocalTransactionState.COMMIT;
    }
}
