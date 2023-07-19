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

}
