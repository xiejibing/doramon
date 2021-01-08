package com.xie.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取单个对象
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            //查询
            String str = jedis.get(realKey);
            T t = stringToBean(str,clazz);//字符串转换为对象
            return t;
        }finally {
            returnToPool(jedis);//jedis返回到池中
        }
    }

    /**
     *设置对象
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);//转化为字符串
            if (str == null||str.length()<=0)
                return false;
            String realKey = prefix.getPrefix() +key;
            int seconds = prefix.expireSeconds();//获得超时时间
            if (seconds<=0)
                jedis.set(realKey,str);
            else
                jedis.setex(realKey,seconds,str);
            return true;
        }finally {
            returnToPool(jedis);
        }
    }
    /**
     * 判断key是否存在
     */

    public<T> boolean exists(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     */

    public<T> Long incr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     */

    public<T> Long decr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * bean 转换为string
     * @param value
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T value){
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();//value的类型
        if (clazz == int.class ||clazz==Integer.class){
            return ""+value;
        }
        else if (clazz == String.class){
            return (String)value;
        }
        else if (clazz == long.class||clazz==Long.class){
            return ""+value;
        }else {
            return JSON.toJSONString(value);//由对象转化为ｊｓｏｎ字符串
        }
    }

    /**
     * string-->bean
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T stringToBean(String str, Class<T> clazz){
        if (str == null  || str.length()<=0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class){
           return (T) Integer.valueOf(str);
        }
        else if (clazz == Long.class || clazz==long.class){
            return (T)Long.valueOf(str);
        }
        else if (clazz == String.class){
            return (T)str;
        }
        else
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
    }

    private void returnToPool(Jedis jedis){
        if (jedis!=null) {
            jedis.close();
        }
    }

    //删除
    public boolean delete(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();//获取连接
            String realKey = keyPrefix.getPrefix()+key;
            Long del = jedis.del(realKey);
            return del>0;
        }finally {
            returnToPool(jedis);//回收连接
        }
    }

    public static void main(String[] args) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxWaitMillis(5000);
        poolConfig.setMaxTotal(50000);
        poolConfig.setMaxIdle(500);
        JedisPool jedisPool = new JedisPool(poolConfig, "47.103.208.206",6379,500, "197526");
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set("test","1");
        }catch (Exception e){
            System.out.println("error");
        }
        jedis.close();
    }
}
