package com.sora.cache;

import com.sora.exception.CacheRuntimeException;
import com.sora.expire.BasicExpire;
import com.sora.expire.IExpire;
import com.sora.map.CacheEvictFactory;
import com.sora.map.CacheMapFactory;
import com.sora.proxy.CacheContextProxy;
import com.sora.strategy.evict.CacheEvictConsts;

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
     * Context的成员
     */
    private int maxSize = 16;

    /**
     * Context的成员
     */
    private float expectRemoveRate = 0.8F;

    private IExpire<K,V> expire = null;

    public static CacheContextBuilder startBuilding(){
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

    public CacheContextBuilder<K,V> expire(IExpire<K,V> expire) {
        this.expire = expire;
        return this;
    }

    /**
     * @return 返回构建好的CacheContext
     */
    public CacheContext<K,V> build() throws CacheRuntimeException {
        Map<K, V> cacheMap = CacheMapFactory.getCacheMap(this.evictType, this.maxSize, this.expectRemoveRate);
        if (Objects.isNull(expire)){
            expire = new BasicExpire<>(cacheMap);
        }

        CacheContext<K,V> cacheContext = new CacheContext<>(
                cacheMap,
                CacheEvictFactory.getCacheEvict(this.evictType),
                this.evictType,
                this.maxSize,
                this.expectRemoveRate,
                this.expire
        );

        // 返回代理对象
        return new CacheContextProxy<>(cacheContext).proxy();
    }

}
