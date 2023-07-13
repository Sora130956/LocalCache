package com.sora.map;

import com.sora.exception.CacheRuntimeException;
import com.sora.strategy.evict.CacheEvictConsts;

import java.util.Map;

/**
 * 简单工厂模式创建CacheMap
 * @author Sora
 */
public class CacheMapFactory {

    public static <K,V> Map<K,V> getCacheMap(String evictType,int maxSize) throws CacheRuntimeException {
        switch (evictType){

            case CacheEvictConsts.FIFO_EVICT:{
                return new FIFOMap<K,V>(maxSize);
            }

            default:{
                throw new CacheRuntimeException("CacheContext构建失败,因为传入的驱逐策略不存在");
            }

        }
    }

}
