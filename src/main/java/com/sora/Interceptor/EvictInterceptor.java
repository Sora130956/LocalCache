package com.sora.Interceptor;

import com.sora.exception.CacheRuntimeException;
import com.sora.mediator.CacheContextMediator;
import com.sora.strategy.evict.AbstractCacheEvict;

/**
 * @author Sora
 */
public class EvictInterceptor extends BaseCacheInterceptor{

    private boolean before = false;
    private boolean after = true;

    public final int PRIORITY = 0;


    @Override
    public boolean isBefore() {
        return before;
    }

    @Override
    public boolean isAfter() {
        return after;
    }

    @Override
    public <K, V> Object beforeProcess(CacheContextMediator<K, V> cacheContextMediator) throws CacheRuntimeException {
        return null;
    }

    @Override
    public <K, V> Object afterProcess(CacheContextMediator<K, V> cacheContextMediator) throws CacheRuntimeException {
        // 在原方法执行之后进行驱逐
        return cacheContextMediator.getCacheEvict().doEvict(cacheContextMediator);
    }

}
