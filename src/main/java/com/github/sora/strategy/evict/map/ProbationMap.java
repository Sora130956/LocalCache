package com.github.sora.strategy.evict.map;

import com.sun.org.apache.regexp.internal.RE;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Probation区
 * @author Sora
 */
public class ProbationMap<K,V> extends LinkedHashMap<K,V> {

    private final int probationSize;

    /**
     * 每个Probation与一个特定的filter关联
     */
    SketchFilter<K,V> sketchFilter;

    ProtectedMap<K,V> protectedZone;

    private Random random = new Random();

    public ProbationMap(int probationSize, SketchFilter<K,V> sketchFilter){
        super((int)(probationSize*1.4),0.75F,true);
        this.probationSize = probationSize;
        this.sketchFilter = sketchFilter;
        this.protectedZone = sketchFilter.getProtectedZone();
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

    /**
     * 当key存在于probation时更新值
     * @return 如果key存在，返回旧值。如果key不存在，返回null。
     */
    public V update(K key, V value) {
        if (this.containsKey(key)){
            return this.put(key, value);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        V stuff = super.get(key);
        if (Objects.nonNull(stuff) && random.nextBoolean()){
            // 在probation区被命中时，有50%的概率晋升到protected区
            super.remove(key);
            protectedZone.put((K) key, stuff);
        }
        return stuff;
    }

}
