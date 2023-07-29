package com.github.sora.strategy.expire;

/**
 * 过期策略接口
 * @param <K> CacheMap的Key
 * @param <V> CacheMap的Value
 * @author Sora
 */
public interface IExpire<K,V> {

    /**
     * 为某个缓存Map中的键值对设置过期时间
     * @param key 希望设置过期时间的缓存Map的key
     * @param ttl 希望在多少毫秒后过期
     * @return 设置是否成功
     */
    boolean expire(K key,Long ttl);

    /**
     * 被expire调用,timeStamp是某个具体的时间戳,key将在这个时间戳过期
     * @param key 被设置了过期时间的缓存Map的key
     * @param timeStamp 在哪个时间戳过期
     * @return 返回是否设置成功
     */
    boolean expireAt(K key,Long timeStamp);

    /**
     * 如果某个key对应的键值对过期了,将其从缓存Map和过期Map中移除
     * @param key 被设置了过期时间的缓存Map的key
     * @return 返回是否成功删除过期键值对
     */
    boolean expire(K key);

    /**
     * 从缓存Map中清空所有的过期键值对
     * @return 返回成功清空的键值对的数量
     */
    int refresh();

}
