package com.sora.strategy.evict.map;

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

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if(this.size() >= maxSize){
            return true;
        }
        return false;
    }

}
