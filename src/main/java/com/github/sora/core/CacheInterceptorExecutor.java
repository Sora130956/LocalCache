package com.github.sora.core;

import com.github.sora.Interceptor.BaseCacheInterceptor;
import com.github.sora.Interceptor.manager.ExpireInterceptorsManager;
import com.github.sora.Interceptor.manager.RefreshInterceptorsManager;
import com.github.sora.annotation.CacheInterceptor;
import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.mediator.CacheContextMediator;
import com.github.sora.Interceptor.manager.EvictInterceptorsManager;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * 拦截器执行类,其中维护了各种Cache拦截器。会先根据注解{@link CacheInterceptor}执行相应拦截器前置方法,再去执行被代理对象的方法,再执行这些拦截器的后置方法。
 * @author Sora
 */
public class CacheInterceptorExecutor{

    /**
     * 为了防止每次执行方法都构建{@link CacheContext}的中介者实例,使用一个Map来缓存。
     */
    private static final HashMap<CacheContext<?,?>, CacheContextMediator> CACHE_CONTEXT_MEDIATOR_MAP = new HashMap<>();

    /**
     * 驱逐拦截器列表
     */
    List<BaseCacheInterceptor> evictInterceptorList = EvictInterceptorsManager.getEvictInterceptorList();

    /**
     * 过期拦截器列表
     */
    List<BaseCacheInterceptor> expireInterceptorList = ExpireInterceptorsManager.getExpireInterceptorList();

    /**
     * 刷新拦截器列表
     */
    List<BaseCacheInterceptor> refreshInterceptorList = RefreshInterceptorsManager.getRefreshInterceptorList();

    /**
     * 通过{@link CacheContext}的代理对象调用方法时,实际会执行这个execute方法。原方法的功能被一些拦截器增强。
     * @param method 被代理的原方法
     * @param objects 参数列表
     * @param methodProxy 代理方法
     * @param cacheContext 调用方法的缓存上下文实例
     * @return 就是原方法的返回值,但可能被后置拦截器处理
     * @param <K> 缓存Map的key
     * @param <V> 缓存Map的value
     * @throws Throwable 当拦截器或原方法执行出错时抛出异常
     */
    @SuppressWarnings("unchecked")
    public <K,V> Object execute(Method method, Object[] objects, MethodProxy methodProxy,CacheContext<K,V> cacheContext) throws Throwable {
        CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);
        if (cacheInterceptor != null){
            // 只有存在@CacheInterceptor注解才执行拦截器
            //初始化CacheContextMediator,即CacheContext的中介对象实例
            CacheContextMediator<K,V> cacheContextMediator = CACHE_CONTEXT_MEDIATOR_MAP.get(cacheContext);
            if (cacheContextMediator == null){
                cacheContextMediator = CacheContextMediator.<K,V>newInstance()
                        .cacheDataMap(cacheContext.getCacheDataMap())
                        .maxSize(cacheContext.getMaxSize())
                        .evictType(cacheContext.getEvictType())
                        .expectRemoveRate(cacheContext.getExpectRemoveRate())
                        .cacheEvict(cacheContext.getCacheEvict())
                        .expire(cacheContext.getExpire());

                CACHE_CONTEXT_MEDIATOR_MAP.put(cacheContext,cacheContextMediator);
            }

            // 执行驱逐拦截器的前置方法
            if (cacheInterceptor.evict()){
                beforeFunction(evictInterceptorList,method,cacheContextMediator,objects,methodProxy);
            }

            //执行过期拦截器的前置方法
            if(cacheInterceptor.expire()){
                beforeFunction(expireInterceptorList,method,cacheContextMediator,objects,methodProxy);
            }

            //执行刷新拦截器的前置方法
            if(cacheInterceptor.refresh()){
                beforeFunction(refreshInterceptorList,method,cacheContextMediator,objects,methodProxy);
            }

            Object result = method.invoke(cacheContext, objects);

            // 执行驱逐拦截器的后置方法
            if (cacheInterceptor.evict()){
                afterFunction(evictInterceptorList,method,cacheContextMediator,objects,methodProxy);
            }

            //执行过期拦截器的后置方法
            if(cacheInterceptor.expire()){
                afterFunction(expireInterceptorList,method,cacheContextMediator,objects,methodProxy);
            }

            //执行刷新拦截器的后置方法
            if(cacheInterceptor.refresh()){
                afterFunction(refreshInterceptorList,method,cacheContextMediator,objects,methodProxy);
            }

            //返回原方法执行结果
            return result;

        } else {
            // 否则只执行原方法
            return method.invoke(cacheContext,objects);
        }
    }

    /**
     * 执行拦截器的前置方法
     */
    public <K,V> void beforeFunction(List<BaseCacheInterceptor> interceptorList, Method method, CacheContextMediator<K,V> cacheContextMediator,Object[] objects,MethodProxy methodProxy) throws CacheRuntimeException {
        for(BaseCacheInterceptor interceptor : interceptorList){
            if(interceptor.isBefore()){
                interceptor.beforeProcess(cacheContextMediator, method, objects, methodProxy);
            }
        }
    }

    /**
     * 执行拦截器的后置方法
     */
    public <K,V> void afterFunction(List<BaseCacheInterceptor> interceptorList, Method method, CacheContextMediator<K,V> cacheContextMediator,Object[] objects,MethodProxy methodProxy) throws CacheRuntimeException {
        for(BaseCacheInterceptor interceptor : interceptorList){
            if(interceptor.isAfter()){
                interceptor.afterProcess(cacheContextMediator, method, objects, methodProxy);
            }
        }
    }

}
