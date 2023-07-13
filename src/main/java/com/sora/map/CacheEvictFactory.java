package com.sora.map;

import com.sora.exception.CacheRuntimeException;
import com.sora.strategy.evict.AbstractCacheEvict;
import com.sora.strategy.evict.CacheEvictConsts;
import com.sora.strategy.evict.FIFOCacheEvict;

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
            default:{
                throw new CacheRuntimeException("构建缓存失败,因为找不到相应的驱逐策略类。");
            }
        }

    }

}
