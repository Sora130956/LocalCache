package com.github.sora.core;


import com.github.sora.annotation.CacheInterceptor;
import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.strategy.expire.IExpire;
import com.github.sora.strategy.evict.AbstractCacheEvict;
import com.github.sora.strategy.serialize.ISerial;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.*;


/**
 * Cache上下文,维护一个Map来保存缓存数据
 * 一般来说,CacheContext构建好了之后,其中的成员就不允许变更了。
 * @param <K> 缓存Map的Key
 * @param <V> 缓存Map的Value
 * @author Sora
 */
@SuppressWarnings("unused")
public class CacheContext<K,V> implements Serializable {

    /**
     * 持久化策略
     */
    private ISerial<K,V> serial;

    /**
     * 过期策略
     */
    private IExpire<K,V> expire;

    /**
     * 缓存Map,用于保存缓存数据
     */
    private Map<K,V> cacheDataMap;

    /**
     * 驱逐策略
     */
    private AbstractCacheEvict cacheEvict;


    /**
     * 指定的缓存最大容量,元素数量超过maxSize就会触发驱逐策略。
     */
    private int maxSize;

    /**
     * 希望数据规模在驱逐策略执行后为{@code maxSize}的百分之多少,默认为80%
     */
    private float expectRemoveRate;

    private String serialName;

    public String getSerialName() {
        return serialName;
    }

    public CacheContext(){}

    public CacheContext(Map<K, V> cacheDataMap, AbstractCacheEvict cacheEvict, int maxSize, float expectRemoveRate,IExpire<K,V> expire,String serialName) {
        this.cacheDataMap = cacheDataMap;
        this.cacheEvict = cacheEvict;
        this.maxSize = maxSize;
        this.expectRemoveRate = expectRemoveRate;
        this.expire = expire;
        this.serialName = serialName;
    }

    void setSerial(ISerial<K, V> serial) {
        this.serial = serial;
    }

    @CacheInterceptor(expire = true)
    public V get(K key){
        return cacheDataMap.get(key);
    }

    @CacheInterceptor(evict = true)
    public V put(K key,V value) throws CacheRuntimeException {
        return cacheDataMap.put(key, value);
    }

    public boolean serial(){
        this.serial.doSerial();
        return true;
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
     * @return 返回缓存Map中的键值对内容、以及maxSize和当前的数据量
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheContext<?, ?> that = (CacheContext<?, ?>) o;
        return maxSize == that.maxSize && Float.compare(that.expectRemoveRate, expectRemoveRate) == 0 && Objects.equals(expire, that.expire) && Objects.equals(cacheDataMap, that.cacheDataMap) && Objects.equals(cacheEvict, that.cacheEvict);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expire, cacheDataMap, cacheEvict, maxSize, expectRemoveRate);
    }

}
