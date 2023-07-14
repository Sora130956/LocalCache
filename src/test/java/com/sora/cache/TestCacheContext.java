package com.sora.cache;

import com.sora.exception.CacheRuntimeException;
import com.sora.strategy.evict.CacheEvictConsts;
import org.junit.Test;

/**
 * @author Sora
 */
public class TestCacheContext {

    @Test
    public void testCacheContextBuilder() throws CacheRuntimeException {
        CacheContext<String,String> cacheContext = CacheContextBuilder.startBuilding()
                .evictType(CacheEvictConsts.LRU)
                .maxSize(32)
                .expectRemoveRate(0.8F)
                .build();

        for(int i=0;i<100;i++){
            cacheContext.put(String.valueOf(i),String.valueOf(i));
        }

        System.out.println(cacheContext);
    }

}
