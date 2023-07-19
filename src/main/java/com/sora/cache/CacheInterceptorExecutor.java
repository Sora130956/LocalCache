package com.sora.cache;

import com.sora.annotation.CacheInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 拦截器执行类,其中维护了各种Cache拦截器。会先执行这些拦截器方法,再去执行被代理对象的方法。
 * @author Sora
 */
public class CacheInterceptorExecutor {

    public Object execute(Object o, Method method, Object[] objects, MethodProxy methodProxy,CacheContext cacheContext) throws Throwable {
        //TODO 完善该方法
        System.out.println("通过代理类执行方法");
        CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);
        // 只有存在该注解才去考虑开启了哪些拦截器
        if(cacheInterceptor!=null && cacheInterceptor.evict()){
            System.out.println("发现evict开启,执行驱逐策略");
        }
        System.out.println("然后执行原方法");
        // TIP 注意method.invoke方法!原本是通过对象实例来调用方法,现在是通过method实例来指定某个对象调用method方法。
        // TIP 这里正好可以指定原对象cacheContext执行原方法
        return method.invoke(cacheContext,objects);
    }

}
