package com.sora.cache;


import com.sora.annotation.CacheInterceptor;
import com.sora.exception.CacheRuntimeException;
import com.sora.expire.IExpire;
import com.sora.mediator.CacheContextMediator;
import com.sora.strategy.evict.AbstractCacheEvict;

import java.util.Map;
import java.util.Set;


/**
 * Cache上下文,维护一个Map来保存缓存数据
 * 一般来说,CacheContext构建好了之后,其中的成员就不允许变更了。
 * @param <K> 缓存Map的Key
 * @param <V> 缓存Map的Value
 * @author Sora
 */
public class CacheContext<K,V>{

    /**
     * 过期策略类
     */
    private IExpire<K,V> expire;

    /**
     * 保存缓存数据的Map
     */
    private Map<K,V> cacheDataMap;

    /**
     * 缓存驱逐策略
     */
    private AbstractCacheEvict cacheEvict;

    /**
     * 驱逐策略类型
     */
    private String evictType = null;

    /**
     * 指定的缓存最大容量,元素数量超过maxSize就会触发驱逐策略。
     * {@link CacheContext#put}
     */
    private int maxSize;

    /**
     * 期望数据量的规模,表示触发驱逐策略时期望删除百分之多少的数据
     */
    private float expectRemoveRate;

    public CacheContext(){}

    public CacheContext(Map<K, V> cacheDataMap, AbstractCacheEvict cacheEvict, String evictType, int maxSize, float expectRemoveRate) {
        this.cacheDataMap = cacheDataMap;
        this.cacheEvict = cacheEvict;
        this.evictType = evictType;
        this.maxSize = maxSize;
        this.expectRemoveRate = expectRemoveRate;
    }

    public String getEvictType(){
        return this.evictType;
    }

    public V get(K key){
        return cacheDataMap.get(key);
    }

    /**
     * 在put方法上开启evict
     */
    @CacheInterceptor(evict = true)
    public V put(K key,V value) throws CacheRuntimeException {
        // 触发驱逐策略后,如果还无法容纳数据,则抛出异常。
        if (cacheDataMap.size() < maxSize){
            return cacheDataMap.put(key, value);
        } else {
            throw new CacheRuntimeException("缓存已满,无法添加更多数据。");
        }
    }

    public int size(){
        return cacheDataMap.size();
    }

    /**
     * @return 返回cacheDataMap中的键值对内容
     */
    @Override
    public String toString() {
        Set<Map.Entry<K, V>> entries = cacheDataMap.entrySet();
        StringBuilder sb = new StringBuilder();
        sb.append("CacheSize:").append(cacheDataMap.size()).append("\r\n");
        sb.append("MaxSize:").append(this.maxSize).append("\r\n");
        for(Map.Entry<K,V> entry : entries){
            sb.append("Key:").append(entry.getKey()).append("   Value:").append(entry.getValue()).append("\r\n");
        }
        return sb.toString();
    }


    /**
     * get方法只有同一个包下的类才能访问
     */
    Map<K, V> getCacheDataMap() {
        return cacheDataMap;
    }

    AbstractCacheEvict getCacheEvict() {
        return cacheEvict;
    }

    int getMaxSize() {
        return maxSize;
    }

    float getExpectRemoveRate() {
        return expectRemoveRate;
    }
}
