package com.github.sora.strategy.expire;

import java.util.*;
import java.util.concurrent.*;

/**
 * 基本的过期策略
 * @author Sora
 */
public class BasicExpire<K,V> implements IExpire<K,V>{
    /**
     * 过期时间Map
     */
    protected HashMap<K,Long> expireMap;

    /**
     * 管理执行定时扫描任务的线程的线程池,这个线程池只管理一个线程。
     */
    protected ScheduledExecutorService scheduledScanExecutor;

    /**
     * 过期Map与缓存Map是相关联的,所以过期策略类中需要依赖缓存Map
     */
    protected Map<K,V> cacheMap;

    @SuppressWarnings("unchecked")
    public BasicExpire(Map<K,V> cacheMap,Map expireMap) {
        this.cacheMap = cacheMap;
        this.expireMap = (HashMap<K, Long>) expireMap;
        init();
    }

    public BasicExpire() {
    }

    public void init(){
        scheduledScanExecutor = Executors.newSingleThreadScheduledExecutor();
        // 设置定时任务为每一秒钟清空一次过期的key
        scheduledScanExecutor.schedule(this::refresh, 1, TimeUnit.SECONDS);
    }

    /**
     * @param key 设置过期时间的key
     * @param ttl 设置的过期时间,单位为毫秒
     * @return 设置成功则返回true
     */
    @Override
    public boolean expire(K key, Long ttl) {
        return expireAt(key, ttl + System.currentTimeMillis());
    }

    /**
     * @param key 希望为缓存Map中的某个key设置过期时间
     * @param timeStamp 设置{@param key}在哪个时间戳过期
     * @return 返回是否设置成功
     */
    @Override
    public boolean expireAt(K key, Long timeStamp) {
        if(cacheMap.containsKey(key)) {
            expireMap.put(key, timeStamp);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param key 如果某个key对应的键值对过期了,将其从缓存Map和过期Map中移除
     * @return 返回是否移除成功
     */
    @Override
    public boolean expire(K key) {
        Long expiredTime = expireMap.get(key);
        if (Objects.nonNull(expiredTime) && expiredTime < System.currentTimeMillis()){
            expireMap.remove(key);
            cacheMap.remove(key);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 朴素的过期策略,每隔一秒钟就遍历过期Map,删除其中所有的过期Key
     * 该方法必须严格地删除缓存Map中所有的过期键值对,不希望被修改,所以用final修饰。
     * @return 返回清除的键值对数目
     */
    @Override
    public int refresh() {
        Set<Map.Entry<K, Long>> entries = expireMap.entrySet();
        int startCount = entries.size();
        if (startCount == 0){
            return 0;
        }
        long currentTimeMillis = System.currentTimeMillis();

        Iterator<Map.Entry<K, Long>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<K, Long> entry = iterator.next();
            if (entry.getValue() <= currentTimeMillis){
                cacheMap.remove(entry.getKey());
                iterator.remove();
            }
        }

        int endCount = entries.size();
        return startCount - endCount;
    }

}
