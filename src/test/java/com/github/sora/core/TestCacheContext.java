package com.github.sora.core;

import com.github.sora.strategy.evict.CacheEvictConst;
import com.github.sora.strategy.expire.ExpireConst;
import com.github.sora.exception.CacheRuntimeException;
import org.junit.Test;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.github.sora.strategy.evict.CacheEvictConst.LRU;
import static com.github.sora.strategy.evict.CacheEvictConst.WTinyLFU;
import static com.github.sora.strategy.expire.ExpireConst.SORT_EXPIRE;

/**
 * @author Sora
 */
public class TestCacheContext {

    /**
     * 对驱逐策略的基本测试
     */
    @Test
    public void testCacheContextBuilder() throws CacheRuntimeException {

        CacheContext<String,String> cacheContext = CacheContextBuilder.<String,String>startBuilding()
                .evictType(WTinyLFU)
                .maxSize(128)
                .expectRemoveRate(0.8F)
                .build();

        for(int i=0;i<100;i++){
            cacheContext.put(String.valueOf(i),String.valueOf(i));
        }

        System.out.println(cacheContext);
    }

    /**
     * 对过期策略的基本测试
     */
    @Test
    public void testExpire() throws CacheRuntimeException, InterruptedException {
        CacheContext<String,String> cacheContext = CacheContextBuilder.<String,String>startBuilding()
                .evictType(CacheEvictConst.FIFO_EVICT)
                .maxSize(32)
                .expectRemoveRate(0.8F)
                .expireType(ExpireConst.BASIC_EXPIRE)
                .build();

        cacheContext.put("1","1");
        cacheContext.put("2","2");
        cacheContext.put("3","3");
        cacheContext.put("4","4");
        cacheContext.put("5","5");

        cacheContext.expire("1",1000L);
        cacheContext.expire("3",2000L);
        System.out.println(cacheContext);

        Thread.sleep(1100L);
        System.out.println(cacheContext);

        Thread.sleep(1100L);
        System.out.println(cacheContext);
        cacheContext.expire("2",1000L);
        cacheContext.expire("4",4000L);
        cacheContext.expire("1",100L);
        cacheContext.expire("3",1000L);
        Thread.sleep(5000L);
        System.out.println(cacheContext);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerial() throws CacheRuntimeException, InterruptedException, IOException, NoSuchFieldException, IllegalAccessException {
        CacheContext<String,String> cacheContext = CacheContextBuilder.<String,String>startBuilding()
                .evictType(CacheEvictConst.FIFO_EVICT)
                .maxSize(32)
                .expectRemoveRate(0.8F)
                .serialName("FORTEST")
                .build();

        for(int i=0;i<100;i++){
            cacheContext.put(String.valueOf(i),String.valueOf(i));
        }
        System.out.println(cacheContext);

        System.out.println(cacheContext.getSerialName());

        cacheContext.serial();

        String fileName = cacheContext.getSerialName();

        CacheContext<String, String> loadedCacheContext = CacheContextBuilder.<String, String>startBuilding().loadCacheContext(fileName);

        System.out.println(loadedCacheContext);

    }

    @Test
    public void testWLFU() throws CacheRuntimeException {
        CacheContext<String,String> cacheContext = CacheContextBuilder.<String,String>startBuilding()
                .evictType(WTinyLFU)
                .maxSize(128)
                .expectRemoveRate(0.8F)
                .build();

        for(int i=0;i<100;i++){
            cacheContext.put(String.valueOf(i),String.valueOf(i));
        }

        for(int i=0;i<20;i++){
            cacheContext.get(String.valueOf(i));
            cacheContext.get(String.valueOf(i));
            cacheContext.get(String.valueOf(i));
        }


        for(int i=0;i<100;i++){
            cacheContext.put(String.valueOf(i),String.valueOf(i));
        }

        System.out.println(cacheContext);
    }

    @Test
    public void testBasicFunction() throws CacheRuntimeException, InterruptedException, IOException, NoSuchFieldException, IllegalAccessException {
        // 创建cacheContext时，可以指定内存淘汰策略、过期策略、缓存大小、触发内存淘汰策略的阈值
        CacheContext<String, String> cacheContext = CacheContextBuilder.<String, String>startBuilding()
                .evictType(WTinyLFU)
                .maxSize(128)
                .expectRemoveRate(0.8F)
                .expireType(SORT_EXPIRE)
                .build();

        for(int i=0; i<20; i++){
            // 向缓存中添加数据
            cacheContext.put(String.valueOf(i),String.valueOf(i));
        }

        for(int i=0; i<20; i++){
            // 向缓存中查询数据
            String value = cacheContext.get(String.valueOf(i));
        }

        for(int i=0; i<20; i++){
            // 修改缓存中的数据
            cacheContext.put(String.valueOf(i), String.valueOf(i) + "被修改了");
        }

        // 删除缓存中的数据
        cacheContext.remove(String.valueOf(8));

        // 指定缓存中某个键值对的过期时间，单位为毫秒
        cacheContext.expire(String.valueOf(10), 1000L);

        // 演示过期功能
        System.out.println("过期策略触发之前：");
        System.out.println(cacheContext);
        Thread.sleep(1200L);
        System.out.println("到了设定的过期时间，触发过期策略：");
        System.out.println(cacheContext);

        // 演示序列化、反序列化功能
        // 手动将cacheContext持久化到磁盘, 默认的持久化名称为当前cacheContext创建时的时间戳
        cacheContext.serial();
        CacheContext<String,String> loadedCacheContext = CacheContextBuilder.<String,String>startBuilding().loadCacheContext(cacheContext.getSerialName());
        System.out.println("反序列化后的cacheContext：");
        System.out.println(loadedCacheContext);
    }

}
