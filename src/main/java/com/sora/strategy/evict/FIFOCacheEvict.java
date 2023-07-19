package com.sora.strategy.evict;

import com.sora.exception.CacheRuntimeException;
import com.sora.map.FIFOMap;
import com.sora.mediator.CacheContextMediator;

/**
 * FIFO驱逐策略类
 * @author Sora
 */
public class FIFOCacheEvict extends AbstractCacheEvict{

    @Override
    public <K,V> boolean doEvict(CacheContextMediator<K,V> context) throws CacheRuntimeException {
        if (!CacheEvictConsts.FIFO_EVICT.equals(context.getEvictType())){
            throw new CacheRuntimeException("驱逐策略不匹配,尝试驱逐数据失败");
        }

        FIFOMap<K,V> cacheDataMap = (FIFOMap<K,V>)context.getCacheDataMap();
        boolean full = cacheDataMap.size() >= context.getMaxSize()*context.getExpectRemoveRate();
        if(full){
            // 如果数据数目大于maxSize,则开始驱逐数据
            int expectSize = (int)(context.getMaxSize() * context.getExpectRemoveRate());
            int distence = cacheDataMap.size() - expectSize;
            // 预期的驱逐后的大小是expectSize,需要删除的元素数量为cacheDataMap.size() - expectSize
            for (;distence>0;distence--){
                cacheDataMap.deleteOldestValue();
            }
        }

        return true;
    }

}
