package com.sora.Interceptor;


import com.sora.exception.CacheRuntimeException;
import com.sora.mediator.CacheContextMediator;

/**
 * 拦截器基类
 * @author Sora
 */
public abstract class BaseCacheInterceptor {

    /**
     * 是否有原方法之前、之后的增强逻辑,默认都为false
     */
    private boolean before;
    private boolean after;

    public abstract boolean isBefore();

    public abstract boolean isAfter();

    /**
     * 执行优先级
     */
    public final int PRIORITY = 0;

    public abstract <K,V> Object beforeProcess(CacheContextMediator<K,V> cacheContextMediator) throws CacheRuntimeException;

    public abstract <K,V> Object afterProcess(CacheContextMediator<K,V> cacheContextMediator) throws CacheRuntimeException;

}
