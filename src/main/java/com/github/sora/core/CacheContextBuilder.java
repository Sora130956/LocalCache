package com.github.sora.core;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.mediator.CacheContextSerialMediator;
import com.github.sora.proxy.CacheContextProxy;
import com.github.sora.strategy.evict.CacheEvictConst;
import com.github.sora.strategy.evict.factory.CacheMapFactory;
import com.github.sora.strategy.evict.map.Entry;
import com.github.sora.strategy.evict.map.SketchFilter;
import com.github.sora.strategy.evict.map.WindowMap;
import com.github.sora.strategy.expire.ExpireConst;
import com.github.sora.strategy.expire.factory.ExpireFactory;
import com.github.sora.strategy.evict.factory.CacheEvictFactory;
import com.github.sora.strategy.expire.factory.ExpireMapFactory;
import com.github.sora.strategy.serialize.SerialConst;
import com.github.sora.strategy.serialize.factofy.SerailFactory;
import org.nustaq.serialization.FSTConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Cache上下文的建造者类
 * @author Sora
 * @since 0.0.1
 */
public class CacheContextBuilder<K,V> {

    private CacheContextBuilder(){}

    /**
     * 默认持久化策略为定时持久化
     */
    private String serialType = SerialConst.BASIC_SERIAL;

    /**
     * 默认驱逐策略为LRU
     */
    private String evictType = CacheEvictConst.LRU;

    /**
     * 默认过期策略为定时扫描+惰性删除
     */
    private String expireType = ExpireConst.BASIC_EXPIRE;

    /**
     * 预期的数据规模,默认为16
     */
    private int maxSize = 16;

    /**
     * 希望数据规模在驱逐策略执行后为{@code maxSize}的百分之多少,默认为80%
     */
    private float expectRemoveRate = 0.8F;

    private FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();

    public static <K,V> CacheContextBuilder<K,V> startBuilding(){
        return new CacheContextBuilder<>();
    }

    public CacheContextBuilder<K,V> evictType(String evictType){
        this.evictType = evictType;
        return this;
    }

    public CacheContextBuilder<K,V> maxSize(int maxSize){
        this.maxSize = maxSize;
        return this;
    }

    public CacheContextBuilder<K,V> expectRemoveRate(float expectRemoveRate){
        this.expectRemoveRate = expectRemoveRate;
        return this;
    }

    public CacheContextBuilder<K,V> expireType(String expireType) {
        this.expireType = expireType;
        return this;
    }

    public CacheContextBuilder<K,V> serialType(String serialType) {
        this.serialType = serialType;
        return this;
    }

    private String serialName = "cache_context" + LocalDate.now().toString() + "T" + String.valueOf(System.currentTimeMillis());

    public CacheContextBuilder<K,V> serialName(String serialName){
        this.serialName = serialName;
        return this;
    }

    /**
     * @return 返回构建好的CacheContext的代理对象
     */
    public CacheContext<K,V> build() throws CacheRuntimeException {
        Map<K, V> cacheMap = CacheMapFactory.getCacheMap(this.evictType, this.maxSize, this.expectRemoveRate);
        Map expireMap = ExpireMapFactory.getExpireMap(expireType);

        CacheContext<K,V> cacheContext = new CacheContext<>(
                cacheMap,
                CacheEvictFactory.getCacheEvict(this.evictType),
                this.maxSize,
                this.expectRemoveRate,
                ExpireFactory.getExpire(expireType,cacheMap, expireMap),
                serialName
        );

        CacheContextSerialMediator cacheContextSerialMediator = new CacheContextSerialMediator(evictType,expireType,cacheMap,expireMap,maxSize,expectRemoveRate,serialType,serialName);
        cacheContext.setSerial(SerailFactory.getSerial(this.serialType,cacheContextSerialMediator));

        // 返回代理对象
        return new CacheContextProxy<>(cacheContext).proxy();
    }

    /**
     * 从序列化得到的CacheContext文件反序列化成CacheContext
     * @param fileName 序列化文件的名字
     * @return 反序列化后得到的CacheContext
     */
    @SuppressWarnings("unchecked")
    public CacheContext<K,V> loadCacheContext(String fileName) throws IOException, CacheRuntimeException, NoSuchFieldException, IllegalAccessException {
        File cacheContextFile = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(cacheContextFile);
        long len = cacheContextFile.length();
        byte[] bytes = new byte[(int) len];
        fileInputStream.read(bytes);
        CacheContextSerialMediator cacheContextSerialMediator = (CacheContextSerialMediator) fstConfiguration.asObject(bytes);

        this.serialName = cacheContextSerialMediator.getSerialName();
        this.evictType = cacheContextSerialMediator.getEvictType();
        this.expectRemoveRate = cacheContextSerialMediator.getExpectRemoveRate();
        this.maxSize = cacheContextSerialMediator.getMaxSize();
        this.expireType = cacheContextSerialMediator.getExpireType();
        Map cacheMap = cacheContextSerialMediator.getCacheMap();
        Map expireMap = cacheContextSerialMediator.getExpireMap();

        if (evictType.equals(CacheEvictConst.WTinyLFU)){
            // 如果驱逐策略为WTinyLFU,则需要重新设置SketchFilter中的election成员
            WindowMap<K,V> windowMap = (WindowMap<K, V>) cacheMap;
            Class<WindowMap> windowMapClass = WindowMap.class;
            Field sketchFilter = windowMapClass.getDeclaredField("sketchFilter");
            /**
             * 如果不设置accessible为true则没有对private成员的访问权限
             * java.lang.IllegalAccessException:
             * Class com.github.sora.core.CacheContextBuilder can not access a member of
             * class com.github.sora.strategy.evict.map.WindowMap with modifiers "private"
             */
            sketchFilter.setAccessible(true);
            SketchFilter sketchFilterFromWindowMap = (SketchFilter) sketchFilter.get(windowMap);
            sketchFilterFromWindowMap.recreateElection();
        }

        CacheContext<K,V> cacheContext = new CacheContext<>(
                cacheMap,
                CacheEvictFactory.getCacheEvict(this.evictType),
                this.maxSize,
                this.expectRemoveRate,
                ExpireFactory.getExpire(expireType,cacheMap, expireMap),
                serialName
        );

        cacheContext.setSerial(SerailFactory.getSerial(this.serialType,cacheContextSerialMediator));



        return new CacheContextProxy<>(cacheContext).proxy();
    }

}
