package com.github.sora.strategy.evict;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.mediator.CacheContextMediator;
import com.github.sora.strategy.evict.map.FIFOMap;

/**
 * FIFO驱逐策略类
 * @author Sora
 */
public class FIFOCacheEvict extends AbstractCacheEvict{

    @Override
    public <K,V> void doEvict(CacheContextMediator<K,V> context) throws CacheRuntimeException {

        FIFOMap<K,V> cacheDataMap = (FIFOMap<K,V>)context.getCacheDataMap();
        boolean full = cacheDataMap.size() >= context.getMaxSize()*context.getExpectRemoveRate();

        if(full){
            // 如果数据数目大于maxSize,则开始驱逐数据
            int expectSize = (int)(context.getMaxSize() * context.getExpectRemoveRate());
            int distance = cacheDataMap.size() - expectSize;
            // 预期的驱逐后的大小是expectSize,需要删除的元素数量为cacheDataMap.size() - expectSize
            for (;distance>0;distance--){
                cacheDataMap.deleteOldestValue();
            }
        }

    }

}
