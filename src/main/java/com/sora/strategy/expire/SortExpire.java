package com.sora.strategy.expire;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 使用TreeMap实现过期Map,expire和定时扫描都调用refresh方法。
 * 这样做的原因是,过期Map中的元素是按过期时间戳排序的,最先过期的元素会排在前面,这样每隔3s进行一次refresh,每次获取缓存中的数据时也refresh,可以避免大量的对过期Map的无效遍历
 * @author Sora
 */
public class SortExpire<K,V> extends BasicExpire<K,V> implements IExpire<K,V>{

    /**
     * 使用TreeMap实现过期Map
     * TreeMap只能按照key排序！！！所以key必须是时间戳！这也是为什么只要获取元素就必须调用refresh
     * 因为如果要针对地过期某个特定的key,不可能去遍历整个红黑树,因为这个红黑树是按时间戳排序的,要找某个key就只能遍历整个红黑树
     */
    protected TreeMap<Long,K> expireMap = new TreeMap<>(Long::compare);

    public SortExpire(Map<K, V> cacheMap){
        this.cacheMap = cacheMap;
        init();
    }

    @Override
    public void init() {
        scheduledScanExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledScanExecutor.schedule(this::refresh,3,TimeUnit.SECONDS);
    }

    @Override
    public boolean expire(K key, Long ttl) {
        return expireAt(key,ttl+System.currentTimeMillis());
    }

    @Override
    public boolean expireAt(K key, Long timeStamp) {
        if (cacheMap.containsKey(key)){
            // 只有当原缓存Map存在该key时,才将该key接入过期Map。否则会导致过期Map与缓存Map数据不一致
            expireMap.put(timeStamp,key);
            return true;
        }
        return false;
    }

    @Override
    public boolean expire(K key) {
        refresh();
        return true;
    }

    /**
     * 删除红黑树最顶端的那些元素,未被删除的键值对一定还没有过期。因为这个红黑树是按照过期时间排序的,最顶端的元素最先过期。
     */
    @Override
    public int refresh() {
        int count=0;
        while (isPeekExpire()){
            Map.Entry<Long, K> entry = expireMap.pollFirstEntry();
            cacheMap.remove(entry.getValue());
            count++;
        }
        return count;
    }

    private boolean isPeekExpire(){
        return !expireMap.isEmpty() && expireMap.firstKey() <= System.currentTimeMillis();
    }

}
