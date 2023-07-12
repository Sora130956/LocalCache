package com.sora.cache;


import com.sora.exception.CacheRuntimeException;
import com.sora.mediator.CacheEvictMediator;
import com.sora.strategy.evict.AbstractCacheEvict;

import java.util.Map;


/**
 * Cache上下文,维护一个Map来保存缓存数据
 * @param <K> 缓存Map的Key
 * @param <V> 缓存Map的Value
 * @author Sora
 */
public class CacheContext<K,V>{

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

    private CacheContext(){}

    public String getEvictType(){
        return this.evictType;
    }

    public V get(K key){
        return cacheDataMap.get(key);
    }

    public V put(K key,V value) throws CacheRuntimeException {
        // 缓存Map容量超过maxSize,触发驱逐策略
        if (cacheDataMap.size() >= maxSize){
            cacheEvict.<K,V>doEvict(new CacheEvictMediator<K,V>()
                    .cacheDataMap(this.cacheDataMap)
                    .evictType(this.evictType)
                    .expectRemoveRate(this.expectRemoveRate)
                    .maxSize(this.maxSize));
        }

        // 触发驱逐策略后,如果还无法容纳数据,则抛出异常。
        if (cacheDataMap.size() < maxSize){
            V oldVal = cacheDataMap.put(key, value);
            return oldVal;
        } else {
            throw new CacheRuntimeException("缓存已满,无法添加更多数据。");
        }
    }
}
