package com.sora.cache;


import com.sora.annotation.CacheInterceptor;
import com.sora.exception.CacheRuntimeException;
import com.sora.strategy.expire.IExpire;
import com.sora.strategy.evict.AbstractCacheEvict;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * Cache上下文,维护一个Map来保存缓存数据
 * 一般来说,CacheContext构建好了之后,其中的成员就不允许变更了。
 * @param <K> 缓存Map的Key
 * @param <V> 缓存Map的Value
 * @author Sora
 */
public class CacheContext<K,V> {

    /**
     * 过期策略类
     */
    private IExpire<K,V> expire;

    /**
     * 保存缓存数据的Map
     */
    private Map<K,V> cacheDataMap;

    /**
     * 缓存驱逐策略
     */
    private AbstractCacheEvict cacheEvict;

    /**
     * 驱逐策略类型
     */
    private String evictType = null;

    /**
     * 指定的缓存最大容量,元素数量超过maxSize就会触发驱逐策略。
     * {@link CacheContext#put}
     */
    private int maxSize;

    /**
     * 期望数据量的规模,表示触发驱逐策略时期望删除百分之多少的数据
     */
    private float expectRemoveRate;

    public CacheContext(){}

    public CacheContext(Map<K, V> cacheDataMap, AbstractCacheEvict cacheEvict, String evictType, int maxSize, float expectRemoveRate,IExpire<K,V> expire) {
        this.cacheDataMap = cacheDataMap;
        this.cacheEvict = cacheEvict;
        this.evictType = evictType;
        this.maxSize = maxSize;
        this.expectRemoveRate = expectRemoveRate;
        this.expire = expire;
    }

    public String getEvictType(){
        return this.evictType;
    }

    @CacheInterceptor(expire = true)
    public V get(K key){
        return cacheDataMap.get(key);
    }

    /**
     * 在put方法上开启evict
     */
    @CacheInterceptor(evict = true)
    public V put(K key,V value) throws CacheRuntimeException {
        if (cacheDataMap.size() < maxSize){
            return cacheDataMap.put(key, value);
        } else {
            throw new CacheRuntimeException("缓存已满,无法添加更多数据。");
        }
    }

    public boolean expire(K key,Long ttl){
        return expire.expire(key,ttl);
    }

    @CacheInterceptor(refresh = true)
    public int size(){
        return cacheDataMap.size();
    }

    @CacheInterceptor(refresh = true)
    public boolean isEmpty() {
        return cacheDataMap.isEmpty();
    }

    @CacheInterceptor(expire = true)
    public boolean containsKey(K key) {
        return cacheDataMap.containsKey(key);
    }

    /**
     * TODO 每次调用这个方法都要清空一次缓存Map中的过期键值对,如果经常调用这个方法,就会造成很多次无意义地遍历过期Map。有什么好办法解决？
     */
    @CacheInterceptor(refresh = true)
    public boolean containsValue(V value) {
        return cacheDataMap.containsValue(value);
    }

    public V remove(K key) {
        return cacheDataMap.remove(key);
    }

    public void putAll(Map<K,V> m) {
        cacheDataMap.putAll(m);
    }

    public void clear() {
        cacheDataMap.clear();
    }

    @CacheInterceptor(refresh = true)
    public Set<K> keySet() {
        return cacheDataMap.keySet();
    }

    @CacheInterceptor(refresh = true)
    public Collection<V> values() {
        return cacheDataMap.values();
    }

    @CacheInterceptor(refresh = true)
    public Set<Map.Entry<K,V>> entrySet() {
        return cacheDataMap.entrySet();
    }

    /**
     * @return 返回cacheDataMap中的键值对内容
     */
    @Override
    @CacheInterceptor(refresh = true)
    public String toString() {
        Set<Map.Entry<K, V>> entries = entrySet();
        StringBuilder sb = new StringBuilder();
        sb.append("CacheSize:").append(cacheDataMap.size()).append("\r\n");
        sb.append("MaxSize:").append(this.maxSize).append("\r\n");
        for(Map.Entry<K,V> entry : entries){
            sb.append("Key:").append(entry.getKey()).append("   Value:").append(entry.getValue()).append("\r\n");
        }
        return sb.toString();
    }


    /**
     * get方法只有同一个包下的类才能访问
     */
    Map<K, V> getCacheDataMap() {
        return cacheDataMap;
    }

    AbstractCacheEvict getCacheEvict() {
        return cacheEvict;
    }

    int getMaxSize() {
        return maxSize;
    }

    float getExpectRemoveRate() {
        return expectRemoveRate;
    }

    IExpire<K,V> getExpire(){return expire;}
}
