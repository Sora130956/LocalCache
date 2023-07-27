package com.sora.strategy.evict.factory;

import com.sora.exception.CacheRuntimeException;
import com.sora.strategy.evict.AbstractCacheEvict;
import com.sora.strategy.evict.CacheEvictConsts;
import com.sora.strategy.evict.FIFOCacheEvict;
import com.sora.strategy.evict.LRUCacheEvict;

/**
 * 简单工厂模式创建驱逐策略类
 * @author Sora
 */
public class CacheEvictFactory {

    public static AbstractCacheEvict getCacheEvict(String evictType) throws CacheRuntimeException {

        switch (evictType){
            case CacheEvictConsts.FIFO_EVICT:{
                return new FIFOCacheEvict();
            }
            case CacheEvictConsts.LRU:{
                return new LRUCacheEvict();
            }
            default:{
                throw new CacheRuntimeException("构建缓存失败,因为找不到相应的驱逐策略类。");
            }
        }

    }

}
