package com.sora.Interceptor;

import com.sora.exception.CacheRuntimeException;
import com.sora.expire.IExpire;
import com.sora.mediator.CacheContextMediator;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 过期拦截器
 * @author Sora
 */
public class ExpireInterceptor extends BaseCacheInterceptor{

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
    @SuppressWarnings("unchecked")
    public <K, V> Object beforeProcess(CacheContextMediator<K,V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException {
        return cacheContextMediator.getExpire().expire((K) objects[0]);
    }

    @Override
    public <K, V> Object afterProcess(CacheContextMediator<K,V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException {
        return null;
    }
}
