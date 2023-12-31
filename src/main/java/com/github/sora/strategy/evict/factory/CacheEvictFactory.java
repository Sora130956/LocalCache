package com.github.sora.strategy.evict.factory;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.strategy.evict.*;
import com.github.sora.strategy.evict.map.WindowMap;

/**
 * 简单工厂模式创建驱逐策略类
 * @author Sora
 */
public class CacheEvictFactory {

    public static AbstractCacheEvict getCacheEvict(String evictType) throws CacheRuntimeException {

        switch (evictType){

            case CacheEvictConst.FIFO_EVICT:{
                return new FIFOCacheEvict();
            }

            case CacheEvictConst.LRU:{
                return new LRUCacheEvict();
            }

            case CacheEvictConst.WTinyLFU:{
                return new WTinyLFUEvict();
            }

            default:{
                throw new CacheRuntimeException("构建缓存失败,因为找不到相应的驱逐策略类。");
            }
        }

    }

}
