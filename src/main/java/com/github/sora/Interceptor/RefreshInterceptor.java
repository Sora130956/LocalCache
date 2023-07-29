package com.github.sora.Interceptor;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.mediator.CacheContextMediator;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 刷新拦截器
 * @author Sora
 */
public class RefreshInterceptor extends BaseCacheInterceptor {

    @Override
    public boolean isBefore() {
        return true;
    }

    @Override
    public boolean isAfter() {
        return false;
    }

    @Override
    public <K, V> void beforeProcess(CacheContextMediator<K,V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException {
        cacheContextMediator.getExpire().refresh();
    }

    @Override
    public <K, V> void afterProcess(CacheContextMediator<K,V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException {
    }
}
