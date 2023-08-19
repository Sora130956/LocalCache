package com.github.sora.mediator;

import com.github.sora.strategy.evict.AbstractCacheEvict;

import java.io.Serializable;
import java.util.Map;

/**
 * 持久化中介者类，保存Cache上下文的元信息
 * @author Sora
 */
public class CacheContextSerialMediator extends BaseCacheContextMediator implements Serializable {

        String serialName;

        private String evictType;

        private String expireType;

        private Map cacheMap;

        private Map expireMap;

        private int maxSize;

        private float expectRemoveRate;

        private String serialType;

        public CacheContextSerialMediator(String evictType, String expireType, Map cacheMap, Map expireMap, int maxSize, float expectRemoveRate, String serialType,String serialName) {
                this.evictType = evictType;
                this.expireType = expireType;
                this.cacheMap = cacheMap;
                this.expireMap = expireMap;
                this.maxSize = maxSize;
                this.expectRemoveRate = expectRemoveRate;
                this.serialType = serialType;
                this.serialName = serialName;
        }

        public String getEvictType() {
                return evictType;
        }

        public String getExpireType() {
                return expireType;
        }

        public Map getCacheMap() {
                return cacheMap;
        }

        public Map getExpireMap() {
                return expireMap;
        }

        public int getMaxSize() {
                return maxSize;
        }

        public float getExpectRemoveRate() {
                return expectRemoveRate;
        }

        public String getSerialType() {
                return serialType;
        }

        public String getSerialName() {
                return serialName;
        }
}
