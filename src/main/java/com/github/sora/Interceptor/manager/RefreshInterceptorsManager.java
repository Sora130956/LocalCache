package com.github.sora.Interceptor.manager;

import com.github.sora.Interceptor.BaseCacheInterceptor;
import com.github.sora.Interceptor.RefreshInterceptor;

import java.util.ArrayList;
import java.util.Comparator;
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

        REFRESH_INTERCEPTOR_LIST.sort(Comparator.comparingInt(a -> a.PRIORITY));
    }

    public static List<BaseCacheInterceptor> getRefreshInterceptorList(){
        return REFRESH_INTERCEPTOR_LIST;
    }

    public static List<BaseCacheInterceptor> addRefreshInterceptor(BaseCacheInterceptor newCacheInterceptor){
        REFRESH_INTERCEPTOR_LIST.add(newCacheInterceptor);
        REFRESH_INTERCEPTOR_LIST.sort(Comparator.comparingInt(a -> a.PRIORITY));
        return REFRESH_INTERCEPTOR_LIST;
    }

}
