package com.sora.strategy.expire.factory;

import com.sora.exception.CacheRuntimeException;
import com.sora.strategy.evict.CacheEvictConsts;
import com.sora.strategy.evict.FIFOCacheEvict;
import com.sora.strategy.evict.LRUCacheEvict;
import com.sora.strategy.expire.BasicExpire;
import com.sora.strategy.expire.ExpireConsts;
import com.sora.strategy.expire.IExpire;
import com.sora.strategy.expire.SortExpire;

import java.util.Map;

/**
 * 驱逐策略工厂类
 * @author Sora
 */
public class ExpireFactory {

    public static <K,V> IExpire<K,V> getExpire(String expireType, Map<K,V> cacheMap) throws CacheRuntimeException {

        switch (expireType){
            case ExpireConsts.BASIC_EXPIRE:{
                return new BasicExpire<>(cacheMap);
            }
            case ExpireConsts.SORT_EXPIRE:{
                return new SortExpire<>(cacheMap);
            }
            default:{
                throw new CacheRuntimeException("构建过期策略失败,因为找不到指定的过期策略。");
            }
        }

    }

}
