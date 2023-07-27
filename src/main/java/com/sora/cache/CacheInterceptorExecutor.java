package com.sora.cache;

import com.sora.Interceptor.BaseCacheInterceptor;
import com.sora.Interceptor.manager.EvictInterceptorsManager;
import com.sora.Interceptor.manager.ExpireInterceptorsManager;
import com.sora.Interceptor.manager.RefreshInterceptorsManager;
import com.sora.annotation.CacheInterceptor;
import com.sora.exception.CacheRuntimeException;
import com.sora.mediator.CacheContextMediator;
import net.sf.cglib.proxy.MethodProxy;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * 拦截器执行类,其中维护了各种Cache拦截器。会先执行这些拦截器方法,再去执行被代理对象的方法。
 * @author Sora
 */
public class CacheInterceptorExecutor{

    private static final HashMap<CacheContext,CacheContextMediator> cacheContextMediatorMap = new HashMap<>();

    List<BaseCacheInterceptor> evictInterceptorList = EvictInterceptorsManager.getEvictInterceptorList();

    List<BaseCacheInterceptor> expireInterceptorList = ExpireInterceptorsManager.getExpireInterceptorList();

    List<BaseCacheInterceptor> refreshInterceptorList = RefreshInterceptorsManager.getRefreshInterceptorList();

    public <K,V> Object execute(Method method, Object[] objects, MethodProxy methodProxy,CacheContext<K,V> cacheContext) throws Throwable {
        CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);
        if (cacheInterceptor != null){
            // 只有存在@CacheInterceptor注解才执行拦截器
            //初始化CacheContextMediator,即CacheContext的中介对象实例
            CacheContextMediator cacheContextMediator = cacheContextMediatorMap.get(cacheContext);
            if (cacheContextMediator == null){
                cacheContextMediator = CacheContextMediator.newInstence()
                        .cacheDataMap(cacheContext.getCacheDataMap())
                        .maxSize(cacheContext.getMaxSize())
                        .evictType(cacheContext.getEvictType())
                        .expectRemoveRate(cacheContext.getExpectRemoveRate())
                        .cacheEvict(cacheContext.getCacheEvict())
                        .expire(cacheContext.getExpire());

                cacheContextMediatorMap.put(cacheContext,cacheContextMediator);
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

    public <K,V> void beforeFunction(List<BaseCacheInterceptor> interceptorList, Method method, CacheContextMediator<K,V> cacheContextMediator,Object[] objects,MethodProxy methodProxy) throws CacheRuntimeException {
        for(BaseCacheInterceptor interceptor : interceptorList){
            if(interceptor.isBefore()){
                interceptor.beforeProcess(cacheContextMediator, method, objects, methodProxy);
            }
        }
    }

    public <K,V> void afterFunction(List<BaseCacheInterceptor> interceptorList, Method method, CacheContextMediator<K,V> cacheContextMediator,Object[] objects,MethodProxy methodProxy) throws CacheRuntimeException {
        for(BaseCacheInterceptor interceptor : interceptorList){
            if(interceptor.isAfter()){
                interceptor.afterProcess(cacheContextMediator, method, objects, methodProxy);
            }
        }
    }

}
