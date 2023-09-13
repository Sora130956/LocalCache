package com.github.sora.strategy.evict.map;

/**
 * 保存缓存元素键值对
 * @param <K> 缓存元素的Key
 * @param <V> 缓存元素的Value
 */
public class Entry<K,V>{
    private K key;
    private V value;

    public Entry(K key, V value){
        this.key = key;
        this.value = value;
    }

    public Entry(){}

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
