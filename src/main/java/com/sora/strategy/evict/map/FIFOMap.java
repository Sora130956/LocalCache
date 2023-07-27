package com.sora.strategy.evict.map;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

/**
 * FIFO策略使用的Map
 * @param <K>
 * @param <V>
 * @author Sora
 */
public class FIFOMap<K,V> extends HashMap<K,V> {

    /**
     * 用来记录键值对进入Map的顺序
     */
    Deque<K> FIFODeque;

    public FIFOMap(){

    }

    public FIFOMap(int maxSize) {
        super(maxSize);
        FIFODeque = new ArrayDeque<>(maxSize);
    }

    /**
     * 当数据加入Map时,也会加入双端队列FIFODeque{@link FIFOMap#FIFODeque}
     */
    @Override
    public V put(K key, V value) {
        FIFODeque.offerLast(key);
        return super.put(key, value);
    }

    /**
     * 删除下一个最晚被加入队列的元素
     * @return 返回旧值
     */
    public V deleteOldestValue(){
        return this.remove(FIFODeque.pollFirst());
    }

}
