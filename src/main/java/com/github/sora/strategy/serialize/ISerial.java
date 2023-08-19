package com.github.sora.strategy.serialize;

import java.io.File;
import java.io.Serializable;

/**
 * @author Sora
 */
public interface ISerial<K,V> extends Serializable {
    /**
     * 序列化CacheContext,保存到磁盘中
     * @return 序列化是否成功
     */
    boolean doSerial();
}
