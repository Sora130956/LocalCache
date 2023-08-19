package com.github.sora.strategy.expire.factory;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.strategy.expire.ExpireConst;
import com.github.sora.strategy.expire.BasicExpire;
import com.github.sora.strategy.expire.IExpire;
import com.github.sora.strategy.expire.SortExpire;

import java.util.HashMap;
import java.util.Map;

/**
 * 驱逐策略工厂类
 * @author Sora
 */
public class ExpireFactory {

    public static <K,V> IExpire<K,V> getExpire(String expireType, Map<K,V> cacheMap, Map expireMap) throws CacheRuntimeException {

        switch (expireType){
            case ExpireConst.BASIC_EXPIRE:{
                return new BasicExpire<>(cacheMap,expireMap);
            }
            case ExpireConst.SORT_EXPIRE:{
                return new SortExpire<>(cacheMap);
            }
            default:{
                throw new CacheRuntimeException("构建过期策略失败,因为找不到指定的过期策略。");
            }
        }

    }

}
