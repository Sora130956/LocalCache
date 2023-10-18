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
     * 基本功能测试方法
     */
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
