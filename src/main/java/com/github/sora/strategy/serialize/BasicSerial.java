package com.github.sora.strategy.serialize;

import com.github.sora.core.CacheContext;
import com.github.sora.mediator.CacheContextSerialMediator;
import org.nustaq.serialization.FSTConfiguration;

import java.io.*;

/**
 * 定时持久化策略
 */
public class BasicSerial<K,V> implements ISerial<K,V>{

    public BasicSerial(){
    }

    public BasicSerial(CacheContextSerialMediator cacheContextSerialMediator){
        this.cacheContextSerialMediator = cacheContextSerialMediator;
    }

    private CacheContextSerialMediator cacheContextSerialMediator;

    FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();

    @Override
    public boolean doSerial() {
        File file = new File(cacheContextSerialMediator.getSerialName());
        byte[] bytes = fstConfiguration.asByteArray(cacheContextSerialMediator);
        if (file.exists()){
            // 如果之前进行过持久化，则将旧文件加上old后缀，在当前CacheContext持久化完毕后，删除旧文件
            file.renameTo(new File(cacheContextSerialMediator.getSerialName() + "-old"));
        }
        try {
            file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.close();
            File oldFile = new File(cacheContextSerialMediator.getSerialName() + "-old");
            if (oldFile.exists()){
                oldFile.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
