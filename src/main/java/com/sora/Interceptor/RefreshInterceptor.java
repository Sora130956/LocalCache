package com.sora.Interceptor;

import com.sora.exception.CacheRuntimeException;
import com.sora.mediator.CacheContextMediator;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 刷新拦截器
 * @author Sora
 */
public class RefreshInterceptor extends BaseCacheInterceptor {

    private boolean before = true;
    private boolean after = false;

    @Override
    public boolean isBefore() {
        return before;
    }

    @Override
    public boolean isAfter() {
        return after;
    }

    @Override
    public <K, V> Object beforeProcess(CacheContextMediator<K,V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException {
        cacheContextMediator.getExpire().refresh();
        return null;
    }

    @Override
    public <K, V> Object afterProcess(CacheContextMediator<K,V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException {
        return null;
    }
}
