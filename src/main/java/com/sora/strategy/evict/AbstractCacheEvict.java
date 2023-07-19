package com.sora.strategy.evict;

import com.sora.exception.CacheRuntimeException;
import com.sora.mediator.CacheContextMediator;

/**
 * @author Sora
 */
public abstract class AbstractCacheEvict {

    public abstract <K,V> boolean doEvict(CacheContextMediator<K,V> context) throws CacheRuntimeException;

}
