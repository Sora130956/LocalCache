package com.github.sora.strategy.evict;

/**
 * 驱逐策略常量类,包括驱逐策略名称和创建其对应的dataMap的lambda表达式
 * @author Sora
 */
public class CacheEvictConst {

    public static final String FIFO_EVICT = "FIFO";

    public static final String LRU = "LRU";

    public static final String WTinyLFU = "WTinyLFU";

}
