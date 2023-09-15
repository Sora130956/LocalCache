package com.github.sora.strategy.evict.map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sora
 */
public class ProtectedMap<K,V> extends LinkedHashMap<K,V> {

    private int protectedSize;

    SketchFilter<K,V> sketchFilter;

    public ProtectedMap(int protectedSize, SketchFilter<K,V> sketchFilter){
        super((int)(protectedSize*1.4),0.75F,true);
        this.protectedSize = protectedSize;
        this.sketchFilter = sketchFilter;
    }

    /**
     * probation区的元素被晋升到protected区中
     */
    void promotion(Entry<K,V> entry){
        this.put(entry.getKey(), entry.getValue());
    }

    /**
     * protected区淘汰的元素，将作为candidate送入filter中
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (this.size() > protectedSize){
            try {
                sketchFilter.candidate(eldest.getKey(), eldest.getValue());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    /**
     * 当key存在于protected时更新值
     * @return 如果key存在，返回旧值。如果key不存在，返回null。
     */
    public V update(K key, V value) {
        if (this.containsKey(key)){
            return this.put(key, value);
        }
        return null;
    }

}
