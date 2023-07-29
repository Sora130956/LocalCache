package com.github.sora.Interceptor.manager;

import com.github.sora.Interceptor.BaseCacheInterceptor;
import com.github.sora.Interceptor.ExpireInterceptor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 过期拦截器列表工厂
 * @author Sora
 */
public class ExpireInterceptorsManager {

    private static final List<BaseCacheInterceptor> EXPIRE_INTERCEPTOR_LIST;

    static{
        EXPIRE_INTERCEPTOR_LIST = new ArrayList<>();
        EXPIRE_INTERCEPTOR_LIST.add(new ExpireInterceptor());

        EXPIRE_INTERCEPTOR_LIST.sort(Comparator.comparingInt(a -> a.PRIORITY));
    }

    public static List<BaseCacheInterceptor> getExpireInterceptorList(){
        return EXPIRE_INTERCEPTOR_LIST;
    }

    public static List<BaseCacheInterceptor> addExpireInterceptor(BaseCacheInterceptor newCacheInterceptor){
        EXPIRE_INTERCEPTOR_LIST.add(newCacheInterceptor);
        EXPIRE_INTERCEPTOR_LIST.sort(Comparator.comparingInt(a -> a.PRIORITY));
        return EXPIRE_INTERCEPTOR_LIST;
    }

}
