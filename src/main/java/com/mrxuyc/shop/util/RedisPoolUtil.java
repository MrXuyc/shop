package com.mrxuyc.shop.util;

import com.mrxuyc.shop.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Created with IntelliJ IDEA.
 * Description: redis的操作都为单线程操作
 * User: mrxuyc
 * Date: 2018-04-27
 * Time: 14:18
 */
@Slf4j
public class RedisPoolUtil {

    public static String set(String key ,String value){
        Jedis jedis=null;
        String result=null;
        try {
            jedis= RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error" ,key,value,e);
        }finally {
            RedisPool.close(jedis);
        }
        return result;
    }

    public static String get(String key ){
        Jedis jedis=null;
        String result=null;
        try {
            jedis= RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error" ,key,e);
        }finally {
            RedisPool.close(jedis);
        }
        return result;
    }

    /**
     *
     * @param key
     * @param value
     * @param exTime 单位秒
     * @return
     */
    public static String setEx(String key ,String value,int exTime){
        Jedis jedis=null;
        String result=null;
        try {
            jedis= RedisPool.getJedis();
            result = jedis.setex(key,exTime, value);
        } catch (Exception e) {
            log.error("setEx key:{} value:{} time:{} error" ,key,value,exTime,e);
        }finally {
            RedisPool.close(jedis);
        }
        return result;
    }

    /***
     *
     * @param key
     * @param exTime 单位为秒
     * @return 1为成功  0为失败
     */
    public static Long expire(String key ,int exTime){
        Jedis jedis=null;
        Long result=null;
        try {
            jedis= RedisPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{}  error" ,key,e);
        }finally {
            RedisPool.close(jedis);
        }
        return result;
    }

    /**
     *
     * @param key
     * @return 1为成功  0为失败
     */
    public static Long del(String key ){
        Jedis jedis=null;
        Long result=null;
        try {
            jedis= RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error" ,key,e);
        }finally {
            RedisPool.close(jedis);
        }
        return result;
    }

}
