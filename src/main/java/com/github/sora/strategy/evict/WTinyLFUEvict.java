package com.github.sora.strategy.evict;

import com.github.sora.mediator.CacheContextMediator;

/**
 * @author Sora
 */
public class WTinyLFUEvict extends AbstractCacheEvict {
    @Override
    public <K, V> void doEvict(CacheContextMediator<K, V> context) {
        // NOTHING TO DO
    }

}