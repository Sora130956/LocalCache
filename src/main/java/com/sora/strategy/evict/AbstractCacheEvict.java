package com.sora.strategy.evict;

import com.sora.exception.CacheRuntimeException;
import com.sora.mediator.CacheEvictMediator;

/**
 * @author Sora
 */
public abstract class AbstractCacheEvict {

    public abstract <K,V> boolean doEvict(CacheEvictMediator<K,V> context) throws CacheRuntimeException;

}
