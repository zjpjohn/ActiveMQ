package com.zjp.mq.cache;

/**
 * Copyright @2016 Xian xxx Co.qizhi
 * All right reserved
 *
 * @author: zjprevenge
 * date: 2016/3/13
 */
public interface CacheService<K, V> {

    /**
     * 设置缓存
     *
     * @param key    缓存键
     * @param value  缓存值
     * @return
     */
    boolean set(K key, V value);

    /**
     * 获取缓存之
     *
     * @param key 缓存键
     * @return
     */
    V get(K key);

    /**
     * 清除缓存
     *
     * @param key 缓存键
     * @return
     */
    boolean remove(K key);

    /**
     * 清楚所有缓存
     *
     * @return
     */
    boolean flushAll();
}
