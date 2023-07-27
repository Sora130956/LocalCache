package com.sora.Interceptor.manager;

import com.sora.Interceptor.BaseCacheInterceptor;
import com.sora.Interceptor.EvictInterceptor;
import com.sora.Interceptor.RefreshInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 刷新拦截器列表工厂
 * @author Sora
 */
public class RefreshInterceptorsManager {

    private static final List<BaseCacheInterceptor> REFRESH_INTERCEPTOR_LIST;

    static{
        REFRESH_INTERCEPTOR_LIST = new ArrayList<>();
        REFRESH_INTERCEPTOR_LIST.add(new RefreshInterceptor());

        REFRESH_INTERCEPTOR_LIST.sort((a, b) -> a.PRIORITY - b.PRIORITY);
    }

    public static List<BaseCacheInterceptor> getRefreshInterceptorList(){
        return REFRESH_INTERCEPTOR_LIST;
    }

    public static List<BaseCacheInterceptor> addRefreshInterceptor(BaseCacheInterceptor newCacheInterceptor){
        REFRESH_INTERCEPTOR_LIST.add(newCacheInterceptor);
        REFRESH_INTERCEPTOR_LIST.sort((a, b) -> a.PRIORITY - b.PRIORITY);
        return REFRESH_INTERCEPTOR_LIST;
    }

}
