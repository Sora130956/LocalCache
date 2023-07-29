package com.github.sora.core;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.proxy.CacheContextProxy;
import com.github.sora.strategy.evict.CacheEvictConst;
import com.github.sora.strategy.evict.factory.CacheMapFactory;
import com.github.sora.strategy.expire.ExpireConst;
import com.github.sora.strategy.expire.factory.ExpireFactory;
import com.github.sora.strategy.evict.factory.CacheEvictFactory;

import java.util.Map;


/**
 * Cache上下文的建造者类
 * @author Sora
 * @since 0.0.1
 */
public class CacheContextBuilder<K,V> {

    private CacheContextBuilder(){}

    /**
     * 默认驱逐策略为LRU
     */
    private String evictType = CacheEvictConst.LRU;

    /**
     * 默认过期策略为定时扫描+惰性删除
     */
    private String expireType = ExpireConst.BASIC_EXPIRE;

    /**
     * 预期的数据规模,默认为16
     */
    private int maxSize = 16;

    /**
     * 希望数据规模在驱逐策略执行后为{@code maxSize}的百分之多少,默认为80%
     */
    private float expectRemoveRate = 0.8F;

    public static <K,V> CacheContextBuilder<K,V> startBuilding(){
        return new CacheContextBuilder<>();
    }

    public CacheContextBuilder<K,V> evictType(String evictType){
        this.evictType = evictType;
        return this;
    }

    public CacheContextBuilder<K,V> maxSize(int maxSize){
        this.maxSize = maxSize;
        return this;
    }

    public CacheContextBuilder<K,V> expectRemoveRate(float expectRemoveRate){
        this.expectRemoveRate = expectRemoveRate;
        return this;
    }

    public CacheContextBuilder<K,V> expireType(String expireType) {
        this.expireType = expireType;
        return this;
    }

    /**
     * @return 返回构建好的CacheContext的代理对象
     */
    public CacheContext<K,V> build() throws CacheRuntimeException {
        Map<K, V> cacheMap = CacheMapFactory.getCacheMap(this.evictType, this.maxSize, this.expectRemoveRate);

        CacheContext<K,V> cacheContext = new CacheContext<>(
                cacheMap,
                CacheEvictFactory.getCacheEvict(this.evictType),
                this.evictType,
                this.maxSize,
                this.expectRemoveRate,
                ExpireFactory.getExpire(expireType,cacheMap)
        );

        // 返回代理对象
        return new CacheContextProxy<>(cacheContext).proxy();
    }

}
