package com.sora.annotation;

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
     * 驱逐策略默认关闭。因为只有在例如put之类的方法才需要开启驱逐策略。
     * 原本调用驱逐策略的代码是写死在put方法中的。如果之后需要修改逻辑、或者另外的方法需要开启驱逐策略,就需要增加、修改很多代码。
     */
    boolean evict() default false;

    /**
     * 过期策略默认关闭。
     * 该选项应该作用在获得缓存Map中某个特定的键值对上,用于惰性删除。如果该键值对已过期,则在原方法执行之前将其从缓存Map中删除。
     * 该选项如果设置为true,则被注解方法的第一个参数必须是想要获取的键值对的key。
     */
    boolean expire() default false;

    /**
     * 该选项如果设置为true,则在原方法执行之前,会先将缓存Map中所有的过期键值对删除。
     */
    boolean refresh() default false;

}
