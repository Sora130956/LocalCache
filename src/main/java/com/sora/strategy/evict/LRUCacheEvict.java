package com.sora.strategy.evict;

import com.sora.exception.CacheRuntimeException;
import com.sora.mediator.CacheContextMediator;

/**
 * LRU驱逐策略类
 * @author Sora
 */
public class LRUCacheEvict extends AbstractCacheEvict {

    @Override
    public <K, V> boolean doEvict(CacheContextMediator<K, V> context) throws CacheRuntimeException {
        //DO NOTHING
        return true;
    }

}
