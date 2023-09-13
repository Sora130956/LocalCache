package com.github.sora.strategy.evict.map;

import com.github.sora.strategy.evict.SketchFilter;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProbationMap<K,V> extends LinkedHashMap<K,V> {

    private int probationSize;

    /**
     * 每个Probation与一个特定的filter关联
     */
    SketchFilter<K,V> sketchFilter;

    public ProbationMap(int probationSize, SketchFilter<K,V> sketchFilter){
        super((int)(probationSize*1.4),0.75F,true);
        this.probationSize = probationSize;
        this.sketchFilter = sketchFilter;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (this.size() > probationSize){
            // probation区域淘汰的元素进入LFU filter中与candidate竞争
            try {
                sketchFilter.victim(eldest.getKey(), eldest.getValue());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

}
