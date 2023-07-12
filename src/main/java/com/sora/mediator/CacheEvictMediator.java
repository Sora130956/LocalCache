package com.sora.mediator;

import java.util.Map;

/**
 * @author Sora
 */
public class CacheEvictMediator<K,V> implements ICacheEvictMediator{

    /**
     * 保存缓存数据的Map
     */
    private Map<K,V> cacheDataMap;

    /**
     * 驱逐策略类型
     */
    private String evictType = null;

    /**
     * 触发驱逐策略时希望删除百分之多少的数据
     */
    private float expectRemoveRate;

    /**
     * 缓存最大容量
     */
    private int maxSize;

    public Map<K, V> getCacheDataMap() {
        return cacheDataMap;
    }


    public String getEvictType() {
        return evictType;
    }

    public float getExpectRemoveRate() {
        return expectRemoveRate;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public CacheEvictMediator<K, V> cacheDataMap(Map<K, V> cacheDataMap) {
        this.cacheDataMap = cacheDataMap;
        return this;
    }

    public CacheEvictMediator<K, V> evictType(String evictType) {
        this.evictType = evictType;
        return this;
    }

    public CacheEvictMediator<K, V> expectRemoveRate(float expectRemoveRate) {
        this.expectRemoveRate = expectRemoveRate;
        return this;
    }

    public CacheEvictMediator<K, V> maxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }
}
