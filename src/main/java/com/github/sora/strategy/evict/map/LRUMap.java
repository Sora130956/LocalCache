package com.github.sora.strategy.evict.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU驱逐策略类
 * @author Sora
 */
public class LRUMap<K,V> extends LinkedHashMap<K,V> {

    int maxSize;

    public LRUMap(int maxSize,float expectRemoveRate){
        super((int)(maxSize*1.4),0.75F,true);
        this.maxSize=(int)(maxSize*expectRemoveRate);
    }

    /**
     * 缓存Map的数据量大于maxSize时,删除最近最少使用的元素。
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() >= maxSize;
    }

}
