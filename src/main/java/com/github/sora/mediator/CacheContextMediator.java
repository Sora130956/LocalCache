package com.github.sora.mediator;

import com.github.sora.strategy.evict.AbstractCacheEvict;
import com.github.sora.strategy.expire.IExpire;

import java.util.Map;

/**
 * {@inheritDoc}
 * @author Sora
 */
public class CacheContextMediator<K,V> extends BaseCacheContextMediator {

    /**
     * 过期策略
     */
    private IExpire<K,V> expire;

    /**
     * 缓存Map
     */
    private Map<K,V> cacheDataMap;

    /**
     * 驱逐策略类型
     */
    private String evictType = null;

    /**
     * 希望数据规模在驱逐策略执行后为{@code maxSize}的百分之多少
     */
    private float expectRemoveRate;

    /**
     * 预期的缓存数据规模
     */
    private int maxSize;

    /**
     * 驱逐策略
     */
    private AbstractCacheEvict cacheEvict;

    public static <K,V> CacheContextMediator<K,V> newInstance(){
        return new CacheContextMediator<>();
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

    public IExpire<K,V> getExpire() {
        return expire;
    }

    public AbstractCacheEvict getCacheEvict() {
        return cacheEvict;
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

    public CacheContextMediator<K, V> cacheEvict(AbstractCacheEvict cacheEvict) {
        this.cacheEvict = cacheEvict;
        return this;
    }

    public CacheContextMediator<K, V> expire(IExpire<K,V> expire) {
        this.expire = expire;
        return this;
    }

}
