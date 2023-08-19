package com.github.sora.strategy.expire.factory;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.strategy.evict.CacheEvictConst;
import com.github.sora.strategy.evict.map.FIFOMap;
import com.github.sora.strategy.evict.map.LRUMap;
import com.github.sora.strategy.expire.ExpireConst;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 过期Map工厂
 * @author Sora
 */
public class ExpireMapFactory {

    public static Map getExpireMap(String expireType) throws CacheRuntimeException {
        switch (expireType){

            case ExpireConst.BASIC_EXPIRE:{
                return new HashMap();
            }

            case ExpireConst.SORT_EXPIRE:{
                return new TreeMap();
            }

            default:{
                throw new CacheRuntimeException("CacheContext构建失败,因为传入的持久化策略不存在");
            }

        }
    }

}
