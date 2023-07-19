package com.sora.cache;

import com.sora.exception.CacheRuntimeException;
import com.sora.map.CacheEvictFactory;
import com.sora.map.CacheMapFactory;
import com.sora.proxy.CacheContextProxy;
import com.sora.strategy.evict.CacheEvictConsts;


/**
 * Cache上下文的建造者类
 * @author Sora
 * @since 0.0.1
 */
public class CacheContextBuilder {

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

    public static CacheContextBuilder startBuilding(){
        return new CacheContextBuilder();
    }

    public CacheContextBuilder evictType(String evictType){
        this.evictType = evictType;
        return this;
    }

    public CacheContextBuilder maxSize(int maxSize){
        this.maxSize = maxSize;
        return this;
    }

    public CacheContextBuilder expectRemoveRate(float expectRemoveRate){
        this.expectRemoveRate = expectRemoveRate;
        return this;
    }

    /**
     * @return 返回构建好的CacheContext
     * @param <K> 缓存数据Map中存放的键的类型
     * @param <V> 缓存数据Map中存放的值的类型
     */
    public <K,V> CacheContext<K,V> build() throws CacheRuntimeException {
        CacheContext<K,V> cacheContext = new CacheContext<>(
                CacheMapFactory.getCacheMap(this.evictType,this.maxSize,this.expectRemoveRate),
                CacheEvictFactory.getCacheEvict(this.evictType),
                this.evictType,
                this.maxSize,
                this.expectRemoveRate
        );
        // 返回代理对象
        return new CacheContextProxy<>(cacheContext).proxy();
    }

}
