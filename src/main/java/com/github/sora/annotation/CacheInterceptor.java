package com.github.sora.annotation;

import com.github.sora.Interceptor.EvictInterceptor;
import com.github.sora.Interceptor.ExpireInterceptor;
import com.github.sora.Interceptor.RefreshInterceptor;

import java.lang.annotation.*;

/**
 * 当这个注解被注解到某个方法上时,表示启用某些类型的拦截器。这些拦截器会在改方法的执行前后执行。
 * @author Sora
 */

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheInterceptor {


    /**
     * 若开启该注释,则会在注释方法前后执行驱逐拦截器{@link EvictInterceptor}的前、后置方法。
     * 默认采用LRU策略。
     */
    boolean evict() default false;

    /**
     * 若开启该注释,则会在注释方法前后执行过期拦截器{@link ExpireInterceptor}的前、后置方法。
     * 被注解方法的第一个参数必须是想要获取的键值对的{@code key}。
     * 默认采用惰性删除策略,在获取{@code key}对应的键值对之前,如果该key已经过期,则先将其从缓存Map中删除。
     */
    boolean expire() default false;

    /**
     * 若开启该注释,则会在注释方法前后执行过期拦截器{@link RefreshInterceptor}的前、后置方法。
     * 默认策略是在被注解方法执行之前,先清空缓存Map中的所有过期数据。
     */
    boolean refresh() default false;

}
