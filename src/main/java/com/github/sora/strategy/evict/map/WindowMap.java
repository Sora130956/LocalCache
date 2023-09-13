package com.github.sora.strategy.evict.map;

import com.github.sora.strategy.evict.SketchFilter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * W-TinyLFU中的Window Cache区
 * 该区域使用LRU算法，容量只占整个Cache的1%，用以应对突发流量、访问模式的变化
 * @author Sora
 */
public class WindowMap<K,V> extends LinkedHashMap<K,V> {

    /**
     * WindowCache与一个特定的filter关联
     */
    SketchFilter<K,V> sketchFilter = null;

    int windowSize;

    public WindowMap(int maxSize, float expectRemoveRate){
        this.windowSize = maxSize/100;
        int probationSize = (int)(maxSize*0.2);
        int protectedSize = (int)(maxSize*0.8);
        sketchFilter = new SketchFilter<>(probationSize, protectedSize);
    }

    /**
     * Window Cache容量达到上限后，将最近最少用的元素作为candidate放入LFU filter中
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (this.size() >= windowSize){
            try {
                sketchFilter.candidate(eldest.getKey(), eldest.getValue());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        } else {
            return false;
        }
    }

}
