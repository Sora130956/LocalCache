package com.sora.expire;

import com.sora.mediator.CacheContextMediator;

import java.util.Map;

/**
 * 使用贪心策略进行定时扫描
 * @author Sora
 */
public class GreedyExpire<K,V> extends BasicExpire<K,V> implements IExpire<K,V>{
    public GreedyExpire(Map<K, V> cacheMap) {
        super(cacheMap);
    }
//TODO
}
