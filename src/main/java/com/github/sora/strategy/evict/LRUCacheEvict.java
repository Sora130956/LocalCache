package com.github.sora.strategy.evict;

import com.github.sora.mediator.CacheContextMediator;
import com.github.sora.strategy.evict.map.LRUMap;

/**
 * LRU驱逐策略类
 * @author Sora
 */
public class LRUCacheEvict extends AbstractCacheEvict {

    /**
     * LRU策略在{@link LRUMap}中实现了,doEvict不需要做任何事。
     */
    @Override
    public <K, V> void doEvict(CacheContextMediator<K, V> context) {
        // NOTHING TO DO
    }

}
