package com.sora.mediator;

import com.sora.cache.CacheContext;

import java.util.Map;

/**
 * CacheContext中介类,保存了CacheContext的各种属性,用于在各个类之间传参,保护了CacheContext的属性不对外暴露。
 * @author Sora
 */
public class CacheContextMediator<K,V> implements ICacheContextMediator {

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

    public static CacheContextMediator newInstence(){
        return new CacheContextMediator();
    }

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

    public CacheContextMediator<K, V> cacheDataMap(Map<K, V> cacheDataMap) {
        this.cacheDataMap = cacheDataMap;
        return this;
    }

    public CacheContextMediator<K, V> evictType(String evictType) {
        this.evictType = evictType;
        return this;
    }

    public CacheContextMediator<K, V> expectRemoveRate(float expectRemoveRate) {
        this.expectRemoveRate = expectRemoveRate;
        return this;
    }

    public CacheContextMediator<K, V> maxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }
}
