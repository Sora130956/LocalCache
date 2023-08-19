package com.github.sora.core;

import com.github.sora.strategy.evict.CacheEvictConst;
import com.github.sora.strategy.expire.ExpireConst;
import com.github.sora.exception.CacheRuntimeException;
import org.junit.Test;
import org.nustaq.serialization.FSTConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

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
                .evictType(CacheEvictConst.FIFO_EVICT)
                .maxSize(32)
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
    public void testSerial() throws CacheRuntimeException, InterruptedException, IOException {
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

}
