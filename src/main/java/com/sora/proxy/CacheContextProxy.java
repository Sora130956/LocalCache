package com.sora.proxy;

import com.sora.cache.CacheContext;
import com.sora.cache.CacheInterceptorExecutor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


/**
 * 动态代理类,目的是用来增加拦截器
 * @author Sora
 */
public class CacheContextProxy<K,V> implements MethodInterceptor {

    private final CacheContext<K,V> target;

    private CacheInterceptorExecutor cacheInterceptorExecutor = new CacheInterceptorExecutor();

    public CacheContextProxy(CacheContext<K,V> cacheContext){
        target = cacheContext;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        // 委托CacheInterceptorExecutor来执行方法
        return cacheInterceptorExecutor.execute(method, objects, methodProxy, target);
    }

    /**
     * @return 返回target的代理类
     */
    @SuppressWarnings("unchecked")
    public CacheContext<K,V> proxy(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return (CacheContext<K,V>) enhancer.create();
    }


}
