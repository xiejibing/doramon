package com.xie.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {
    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        JedisPool jedisPool = new JedisPool(poolConfig,redisConfig.getHost(),redisConfig.getPort(),
                redisConfig.getTimeout(),redisConfig.getPassword());
        return jedisPool;
    }
}
