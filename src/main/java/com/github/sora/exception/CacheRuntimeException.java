package com.github.sora.exception;

/**
 * 缓存运行时异常类
 * @author Sora
 */
public class CacheRuntimeException extends Exception{

    public CacheRuntimeException(){

    }

    public CacheRuntimeException(String message){
        super(message);
    }

}
