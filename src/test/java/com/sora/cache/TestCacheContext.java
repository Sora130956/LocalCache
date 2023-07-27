package com.sora.cache;

import com.sora.exception.CacheRuntimeException;
import com.sora.strategy.evict.CacheEvictConsts;
import com.sora.strategy.expire.ExpireConsts;
import org.junit.Test;
import org.junit.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author Sora
 */
public class TestCacheContext {

    static Random random = new Random();

    @Test
    public void testCacheContextBuilder() throws CacheRuntimeException {
        CacheContext<String,String> cacheContext = CacheContextBuilder.<String,String>startBuilding()
                .evictType(CacheEvictConsts.FIFO_EVICT)
                .maxSize(32)
                .expectRemoveRate(0.8F)
                .build();

        for(int i=0;i<100;i++){
            cacheContext.put(String.valueOf(i),String.valueOf(i));
        }

        System.out.println(cacheContext);
    }

    /**
     * @param border 生成值的范围
     * @param weight 有百分之weight的概率生成border*0.2以内的值,百分之(100-weight)的概率生成border以内的值
     * @param count 生成count个
     * @return 返回随机数数组
     */
    public static String[] randomNumberWithWeighting(int border,int weight,int count){
        String[] array = new String[count];
        int hotBorder = (int) (border*0.2);
        for(int i=0;i<count;i++){
            int happens = random.nextInt(100);
            if (happens <= weight){
                array[i] = String.valueOf(random.nextInt(hotBorder));
            } else {
                array[i] = String.valueOf(random.nextInt(border));
            }
        }
        return array;
    }

    @Test
    public void testCacheContextWithRandomData() throws CacheRuntimeException {
        CacheContext<String,String> cacheContext = CacheContextBuilder.<String,String>startBuilding()
                .evictType(CacheEvictConsts.LRU)
                .maxSize(32)
                .expectRemoveRate(0.8F)
                .build();

        int N = 1000000;
        int weight = 80;
        int border = 100;
        String[] randoms = randomNumberWithWeighting(border, weight, N);


        int useBeforeTime = 10;
        for(int i=0;i<N/2;i++){
            String K = randoms[i];
            String V = randoms[N-i-1];
            cacheContext.put(K,V);
            String val = cacheContext.get(K);
            assertEquals(V,val);
        }

        String notExists = cacheContext.get(String.valueOf(border + 1));
        assertNull(notExists);

        System.out.println(cacheContext);
    }

    @Test
    public void testExpire() throws CacheRuntimeException, InterruptedException {
        CacheContext<String,String> cacheContext = CacheContextBuilder.<String,String>startBuilding()
                .evictType(CacheEvictConsts.FIFO_EVICT)
                .maxSize(32)
                .expectRemoveRate(0.8F)
                .expireType(ExpireConsts.BASIC_EXPIRE)
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

}
