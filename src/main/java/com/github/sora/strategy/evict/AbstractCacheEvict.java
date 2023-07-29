package com.github.sora.strategy.evict;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.mediator.CacheContextMediator;

/**
 * 驱逐策略抽象类
 * @author Sora
 */
public abstract class AbstractCacheEvict {

    /**
     * 当数据量达到maxSize时进行驱逐
     * @param context {@code CacheContext}的中介者实例
     * @param <K> 缓存Map的key
     * @param <V> 缓存Map的value
     * @throws CacheRuntimeException 执行驱逐策略出现异常
     */
    public abstract <K,V> void doEvict(CacheContextMediator<K,V> context) throws CacheRuntimeException;

}
