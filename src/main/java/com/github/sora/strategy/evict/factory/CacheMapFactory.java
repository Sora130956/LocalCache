package com.github.sora.strategy.evict.factory;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.strategy.evict.CacheEvictConst;
import com.github.sora.strategy.evict.map.FIFOMap;
import com.github.sora.strategy.evict.map.LRUMap;

import java.util.Map;

/**
 * 简单工厂模式创建CacheMap
 * @author Sora
 */
public class CacheMapFactory {

    public static <K,V> Map<K,V> getCacheMap(String evictType, int maxSize, float expectRemoveRate) throws CacheRuntimeException {
        switch (evictType){

            case CacheEvictConst.FIFO_EVICT:{
                return new FIFOMap<>(maxSize);
            }

            case CacheEvictConst.LRU:{
                return new LRUMap<>(maxSize,expectRemoveRate);
            }

            default:{
                throw new CacheRuntimeException("CacheContext构建失败,因为传入的驱逐策略不存在");
            }

        }
    }

}
