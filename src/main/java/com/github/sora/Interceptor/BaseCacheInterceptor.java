package com.github.sora.Interceptor;


import com.github.sora.Interceptor.manager.EvictInterceptorsManager;
import com.github.sora.annotation.CacheInterceptor;
import com.github.sora.exception.CacheRuntimeException;
import com.github.sora.mediator.CacheContextMediator;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 拦截器基类
 * @author Sora
 */
public abstract class BaseCacheInterceptor {

    /**
     * 返回是否存在拦截器前置方法
     * @return 实现类决定返回true或者false
     */
    public abstract boolean isBefore();

    /**
     * 返回是否存在拦截器后置方法
     * @return 实现类决定返回true或者false
     */
    public abstract boolean isAfter();

    /**
     * 执行优先级
     */
    public final int PRIORITY = 0;

    /**
     * 拦截器的前置方法,如果希望在CacheContext中的某个方法之前执行该前置方法,需要实现本方法并将该类的before成员置为true。
     * 然后将其注册到拦截器管理类中,并在想要加强的方法上开启相应的注解。
     * 例如,希望自定义一个驱逐拦截器,首先需要实现该抽象类,并调用
     * {@link EvictInterceptorsManager#addEvictInterceptor(BaseCacheInterceptor)}
     * 方法将其注册到拦截器管理类中,保证CacheContext中希望加强的方法开启了{@link CacheInterceptor#evict()}即可。
     *
     * @param cacheContextMediator 缓存上下文中介者实例,封装了缓存上下文的成员
     * @param method               被代理的原方法
     * @param objects              原方法的参数
     * @param methodProxy          代理方法
     * @param <K>                  缓存Map的key
     * @param <V>                  缓存Map的Value
     * @throws CacheRuntimeException 策略类执行方法失败时抛出异常
     */
    public abstract <K,V> void beforeProcess(CacheContextMediator<K,V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException;


    /**
     * 拦截器后置方法,使用方法与前置方法类似
     *
     * @param cacheContextMediator 缓存上下文中介者实例,封装了缓存上下文的成员
     * @param method               被代理的原方法
     * @param objects              原方法的参数
     * @param methodProxy          代理方法
     * @param <K>                  缓存Map的key
     * @param <V>                  缓存Map的Value
     * @throws CacheRuntimeException 策略类执行方法失败时抛出异常
     */
    public abstract <K,V> void afterProcess(CacheContextMediator<K,V> cacheContextMediator, Method method, Object[] objects, MethodProxy methodProxy) throws CacheRuntimeException;

}
