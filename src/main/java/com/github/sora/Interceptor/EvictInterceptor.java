package com.github.sora.Interceptor;

import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.mediator.CacheContextMediator;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 驱逐拦截器
 * @author Sora
 */
public class EvictInterceptor extends BaseCacheInterceptor{

    public final int PRIORITY = 0;


    @Override
    public boolean isBefore() {
        return false;
    }

    @Override
    public boolean isAfter() {
        return true;
    }

    @Override
    public <K, V> void beforeProcess(CacheContextMediator<K, V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException {
    }

    @Override
    public <K, V> void afterProcess(CacheContextMediator<K, V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException {
        // 在原方法执行之后进行驱逐
        cacheContextMediator.getCacheEvict().doEvict(cacheContextMediator);
    }

}
