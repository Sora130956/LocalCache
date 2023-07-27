package com.sora.strategy.expire;

/**
 * 过期策略接口
 * @param <K> CacheMap的Key
 * @param <V> CacheMap的Value
 * @author Sora
 */
public interface IExpire<K,V> {

    /**
     * @param key 设置过期时间的key
     * @param ttl 设置的过期时间
     * @return 返回是否设置成功
     */
    boolean expire(K key,Long ttl);

    /**
     * @param timeStamp 在哪个时间戳过期
     */
    boolean expireAt(K key,Long timeStamp);

    /**
     * @param key 如果某个key对应的键值对过期了,将其从缓存Map和过期Map中移除
     */
    boolean expire(K key);

    /**
     * 清空所有的过期键值对,这是定时扫描所执行的方法
     * @return 返回成功清空的键值对的数量
     */
    int refresh();

}
