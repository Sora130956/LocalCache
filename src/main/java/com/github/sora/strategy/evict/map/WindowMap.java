package com.github.sora.strategy.evict.map;

import java.util.*;

/**
 * W-TinyLFU中的Window Cache区
 * 该区域使用LRU算法，容量只占整个Cache的1%，用以应对突发流量、访问模式的变化
 * @author Sora
 */
public class WindowMap<K,V> extends LinkedHashMap<K,V> {

    /**
     * WindowCache与一个特定的filter、probation、protected关联
     */
    private SketchFilter<K,V> sketchFilter;

    private ProbationMap<K,V> probationZone;

    private ProtectedMap<K,V> protectedZone;

    int windowSize;

    public WindowMap(int maxSize, float expectRemoveRate){
        this.windowSize = Math.max(maxSize/100, 4);
        int probationSize = (int)(maxSize*0.2);
        int protectedSize = (int)(maxSize*0.8);
        sketchFilter = new SketchFilter<>(probationSize, protectedSize);
        probationZone = sketchFilter.getProbationZone();
        protectedZone = sketchFilter.getProtectedZone();
    }

    /**
     * Window Cache容量达到上限后，将最近最少用的元素作为candidate放入LFU filter中
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (super.size() >= windowSize){
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

    /**
     * windowMap所提供的接口，逻辑上应该将window、probation、protected三个map看成一个hashMap，同时对三个区域操作
     */
    @Override
    public V get(Object key) {
        V stuff = protectedZone.get(key);
        if (Objects.isNull(stuff)){
            stuff = probationZone.get(key);
        }
        if (Objects.isNull(stuff)){
            stuff = super.get(key);
        }
        if (Objects.nonNull(stuff)){
            sketchFilter.increment(stuff);
        }
        return stuff;
    }

    @Override
    public V put(K key, V value) {
        sketchFilter.increment(value);
        V oldValue = protectedZone.update(key, value);
        if (Objects.nonNull(oldValue)){
            return oldValue;
        }
        oldValue = probationZone.update(key, value);
        if (Objects.nonNull(oldValue)){
            return oldValue;
        }
        return super.put(key, value);
    }

    @Override
    public int size() {
        return super.size()+probationZone.size()+protectedZone.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && probationZone.isEmpty() && protectedZone.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return super.containsValue(value) || probationZone.containsValue(value) || protectedZone.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return protectedZone.containsKey(key) || probationZone.containsKey(key) || super.containsKey(key);
    }

    @Override
    public String toString() {
        Set<Map.Entry<K, V>> windowEntries = super.entrySet();
        Set<Map.Entry<K, V>> probationEntries = probationZone.entrySet();
        Set<Map.Entry<K, V>> protectedEntries = protectedZone.entrySet();
        StringBuilder sb = new StringBuilder();
        sb.append("CacheSize: " + this.size()).append("\r\n");
        sb.append("\r\n");
        sb.append(( "windowSize: " + windowEntries.size())).append("\r\n");
        for (Map.Entry<K,V> entry : windowEntries){
            sb.append("key: " + entry.getKey() + " value: " + entry.getValue()).append("\r\n");
        }
        sb.append("\r\n");
        sb.append( "probationSize: " + probationEntries.size()).append("\r\n");
        for (Map.Entry<K,V> entry : probationEntries){
            sb.append("key: " + entry.getKey() + " value: " + entry.getValue()).append("\r\n");
        }
        sb.append("\r\n");
        sb.append( "protectedSize: " + protectedEntries.size()).append("\r\n");
        for (Map.Entry<K,V> entry : protectedEntries){
            sb.append("key: " + entry.getKey() + " value: " + entry.getValue()).append("\r\n");
        }

        return sb.toString();
    }

    @Override
    public V remove(Object key) {
        V oldValue = protectedZone.remove(key);
        if (oldValue != null){
            return oldValue;
        }
        oldValue = probationZone.remove(key);
        if (oldValue != null){
            return oldValue;
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        super.clear();
        probationZone.clear();
        protectedZone.clear();
    }

    @Override
    public Set<K> keySet() {
        Set<K> kSet = new HashSet<>(protectedZone.keySet());
        Set<K> kSet1 = probationZone.keySet();
        Set<K> kSet2 = super.keySet();
        kSet.addAll(kSet1);
        kSet.addAll(kSet2);
        return kSet;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<>(protectedZone.values());
        Collection<V> values1 = probationZone.values();
        Collection<V> values2 = super.values();
        values.addAll(values1);
        values.addAll(values2);
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entries2 = super.entrySet();
        Set<Map.Entry<K, V>> entries1 = probationZone.entrySet();
        Set<Map.Entry<K, V>> entries = new HashSet<>(protectedZone.entrySet());
        entries.addAll(entries1);
        entries.addAll(entries2);
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Map)) {
            return false;
        }
        Map<?,?> m = (Map<?,?>) o;
        if (m.size() != size()) {
            return false;
        }

        try {
            Iterator<Map.Entry<K,V>> i = super.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<K,V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(m.get(key)==null && m.containsKey(key))){
                        return false;
                    }
                } else {
                    if (!value.equals(m.get(key))){
                        return false;
                    }
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int h = 0;

        Iterator<Map.Entry<K,V>> i = super.entrySet().iterator();
        while (i.hasNext())
            h += i.next().hashCode();

        return h;
    }
}