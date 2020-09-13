package com.xie.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MqConfig {

    public static final String QUEUE = "queue";
    public static final String MIAOSHA_QUEUE = "miaosha.queue";
    public static final String Headers_QUEUE = "headersQueue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";

    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String Fanout_EXCHANGE = "fanoutExchange";
    public static final String Headers_EXCHANGE = "headersExchange";


    //秒杀队列
    @Bean
    public Queue miaoshaQueue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }


    /***
     * **********下面为学习内容*****************
     */
    //ｄｉｒｅｃｔ exchange模式  直接交换模式
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    //-----------topic exchange模式------------//
    //定义队列１
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);
    }

    //定义队列２
    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);
    }

    //定义交换机
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    //绑定交换机和队列
    @Bean
    public Binding topicBinding1() {
        //绑定topicQueue1到交换机--->名字为topic.key1
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }

    @Bean
    public Binding topicBinding2() {
        //绑定topicQueue1到交换机--->名字为topic.#
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");//routingKey支持通配符
    }

    //-----------Fanout exchange模式 广播模式:这种模式下，发送一条消息，绑定额所有队列都会接受到消息------------//
    //定义交换机
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(Fanout_EXCHANGE);
    }

    //绑定队列
    //绑定交换机和队列
    @Bean
    public Binding fanoutBinding1() {
        //绑定topicQueue1到交换机--->名字为topic.key1
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        //绑定topicQueue1到交换机--->名字为topic.#
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    //-----------Headers exchange模式 广播模式:这种模式下，发送一条消息，绑定额所有队列都会接受到消息------------//
    //定义交换机
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(Headers_EXCHANGE);
    }
    //定义队列
    @Bean
    public Queue headerQueue() {
        return new Queue(Headers_QUEUE, true);
    }

    @Bean
    public Binding headersBinding() {

        Map<String,Object> map = new HashMap<>();
        map.put("header1","value1");
        map.put("header2","value2");
        return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();

    }

}
