package com.sora.cache;

import com.sora.exception.CacheRuntimeException;
import com.sora.strategy.expire.BasicExpire;
import com.sora.strategy.expire.ExpireConsts;
import com.sora.strategy.expire.IExpire;
import com.sora.strategy.evict.factory.CacheEvictFactory;
import com.sora.strategy.evict.factory.CacheMapFactory;
import com.sora.proxy.CacheContextProxy;
import com.sora.strategy.evict.CacheEvictConsts;
import com.sora.strategy.expire.factory.ExpireFactory;

import java.util.Map;
import java.util.Objects;


/**
 * Cache上下文的建造者类
 * @author Sora
 * @since 0.0.1
 */
public class CacheContextBuilder<K,V> {

    private CacheContextBuilder(){}

    /**
     * Context的成员 默认驱逐策略为FIFO
     */
    private String evictType = CacheEvictConsts.FIFO_EVICT;

    /**
     * 默认过期策略为定时扫描+惰性删除
     */
    private String expireType = ExpireConsts.BASIC_EXPIRE;

    /**
     * Context的成员
     */
    private int maxSize = 16;

    /**
     * Context的成员
     */
    private float expectRemoveRate = 0.8F;

    private IExpire<K,V> expire = null;

    public static <K,V> CacheContextBuilder<K,V> startBuilding(){
        return new CacheContextBuilder();
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
     * @return 返回构建好的CacheContext
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
