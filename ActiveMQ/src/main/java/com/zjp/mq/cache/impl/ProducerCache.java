package com.zjp.mq.cache.impl;

import com.zjp.mq.cache.CacheService;
import com.zjp.mq.producer.ActiveMQTxMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 */
@Component
public class ProducerCache implements CacheService<String, ActiveMQTxMessageProducer> {

    private static final Logger log = LoggerFactory.getLogger(ProducerCache.class);

    private Map<String, ActiveMQTxMessageProducer> cacheDB;

    public ProducerCache() {
        cacheDB = new ConcurrentHashMap<String, ActiveMQTxMessageProducer>();
    }

    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     * @return
     */
    public boolean set(String key, ActiveMQTxMessageProducer value) {
        if (key == null || value == null) {
            log.error("param exception,add cache faile");
            return false;
        }
        try {
            cacheDB.put(key, value);
            return true;
        } catch (Exception e) {
            log.error("add cache error: {}", e);
        }
        return false;
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return
     */
    public ActiveMQTxMessageProducer get(String key) {
        if (key == null) {
            log.warn("key must not be null");
            return null;
        }
        ActiveMQTxMessageProducer value = null;
        try {
            value = cacheDB.get(key);
        } catch (Exception e) {
            log.error("get value from cache error: {}", e);
        }
        return value;
    }

    /**
     * 清除缓存
     *
     * @param key 缓存键
     * @return
     */
    public boolean remove(String key) {
        if (key == null) {
            log.warn("key must not be null...");
            return false;
        }
        try {
            cacheDB.remove(key);
            return true;
        } catch (Exception e) {
            log.error("remove cache error: {}", e);
        }
        return false;
    }

    /**
     * 清除所有缓存
     *
     * @return
     */
    public boolean flushAll() {
        try {
            cacheDB.clear();
            return true;
        } catch (Exception e) {
            log.error("flush cache error: {}", e);
        }
        return false;
    }
}
