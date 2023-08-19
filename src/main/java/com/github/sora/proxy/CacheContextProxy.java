package com.github.sora.proxy;

import com.github.sora.core.CacheContext;
import com.github.sora.core.CacheInterceptorExecutor;
import com.sun.org.apache.xpath.internal.operations.Equals;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


/**
 * 动态代理类,目的是为了使用拦截器来增强原方法
 * @author Sora
 */
public class CacheContextProxy<K,V> implements MethodInterceptor {

    private final CacheContext<K,V> target;

    private final CacheInterceptorExecutor cacheInterceptorExecutor = new CacheInterceptorExecutor();

    public CacheContextProxy(CacheContext<K,V> cacheContext){
        target = cacheContext;
    }

    /**
     * 通过{@code CacheContext}的代理对象调用方法时,所有方法都会被被这个方法拦截。
     * 该方法委托{@code CacheInterceptorExecutor}来执行方法。
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
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
