package com.sora.Interceptor.manager;

import com.sora.Interceptor.BaseCacheInterceptor;
import com.sora.Interceptor.EvictInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 驱逐拦截器列表工厂
 * @author Sora
 */
public class EvictInterceptorsManager {

    private static final List<BaseCacheInterceptor> EVICT_INTERCEPTOR_LIST;

    static{
        EVICT_INTERCEPTOR_LIST = new ArrayList<>();
        EVICT_INTERCEPTOR_LIST.add(new EvictInterceptor());

        EVICT_INTERCEPTOR_LIST.sort((a,b) -> a.PRIORITY - b.PRIORITY);
    }

    public static List<BaseCacheInterceptor> getEvictInterceptorList(){
        return EVICT_INTERCEPTOR_LIST;
    }

    public static List<BaseCacheInterceptor> addEvictInterceptor(BaseCacheInterceptor newCacheInterceptor){
        EVICT_INTERCEPTOR_LIST.add(newCacheInterceptor);
        EVICT_INTERCEPTOR_LIST.sort((a,b) -> a.PRIORITY - b.PRIORITY);
        return EVICT_INTERCEPTOR_LIST;
    }

}
