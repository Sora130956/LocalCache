package com.github.sora.strategy.serialize.factofy;

import com.github.sora.core.CacheContext;
import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.mediator.CacheContextSerialMediator;
import com.github.sora.strategy.expire.BasicExpire;
import com.github.sora.strategy.expire.ExpireConst;
import com.github.sora.strategy.expire.IExpire;
import com.github.sora.strategy.expire.SortExpire;
import com.github.sora.strategy.serialize.BasicSerial;
import com.github.sora.strategy.serialize.ISerial;
import com.github.sora.strategy.serialize.SerialConst;

import java.util.Map;

/**
 * 持久化策略工厂类
 * @author Sora
 */
public class SerailFactory {

    public static <K,V> ISerial<K,V> getSerial(String serialType, CacheContextSerialMediator cacheContextSerialMediator) throws CacheRuntimeException {

        switch (serialType){
            case SerialConst.BASIC_SERIAL:{
                return new BasicSerial<>(cacheContextSerialMediator);
            }
            default:{
                throw new CacheRuntimeException("构建持久化策略失败,因为找不到指定的持久化策略。");
            }
        }

    }

}
